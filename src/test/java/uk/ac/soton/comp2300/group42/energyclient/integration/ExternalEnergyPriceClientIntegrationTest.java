package uk.ac.soton.comp2300.group42.energyclient.integration;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.data.external.ExternalEnergyPriceClient;
import uk.ac.soton.comp2300.group42.energyclient.data.external.UnitRateResponse;

import java.io.IOException;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for external energy price API client.
 * Tests the real HTTP request/response cycle with MockWebServer acting as the external API,
 * and validates JSON deserialization and URL parameter construction.
 *
 * Demonstrates: Integration Testing, Service Testing, Boundary Testing
 */
@Tag("integration")
class ExternalEnergyPriceClientIntegrationTest {

    private MockWebServer mockWebServer;
    private ExternalEnergyPriceClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        var mapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        URI mockBaseUrl = URI.create(mockWebServer.url("/v1/products/.../standard-unit-rates/").toString());

        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-03-02T23:02:13Z"),
                ZoneId.of("UTC")
        );

        client = new ExternalEnergyPriceClient(mapper, mockBaseUrl, fixedClock);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    // =========================================================================
    // SERVICE TESTS: Happy path
    // =========================================================================

    @Test
    void fetchNext12Hours_ConstructsCorrectUrlAndParsesResponse_ServiceTest() throws Exception {
        String mockJsonResponse = """
            {
                "count": 2,
                "next": null,
                "previous": null,
                "results": [
                    {
                      "value_exc_vat": 16.8,
                      "value_inc_vat": 17.64,
                      "valid_from": "2026-03-03T22:30:00Z",
                      "valid_to": "2026-03-03T23:00:00Z",
                      "payment_method": null
                    },
                    {
                      "value_exc_vat": 18.27,
                      "value_inc_vat": 19.1835,
                      "valid_from": "2026-03-03T22:00:00Z",
                      "valid_to": "2026-03-03T22:30:00Z",
                      "payment_method": null
                    }
                ]
            }
        """;
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(mockJsonResponse));

        List<UnitRateResponse> results = client.fetchNext12Hours();

        RecordedRequest request = mockWebServer.takeRequest();
        String expectedPathEnd = "?period_from=2026-03-02T23%3A02%3A13Z&period_to=2026-03-03T11%3A02%3A13Z&page_size=24";

        assertNotNull(request.getPath());
        assertTrue(request.getPath().endsWith(expectedPathEnd), "Path was actually: " + request.getPath());

        assertEquals(2, results.size());
        assertEquals(19.1835, results.get(0).valueIncVat());
        assertEquals(17.64, results.get(1).valueIncVat());
    }

    // =========================================================================
    // BOUNDARY TESTS: Edge cases and malformed responses
    // =========================================================================

    @Test
    void fetchNext12Hours_WithEmptyResults_ReturnsEmptyList_BoundaryTest() throws Exception {
        String mockJsonResponse = """
            {
                "count": 0,
                "next": null,
                "previous": null,
                "results": []
            }
        """;
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(mockJsonResponse));

        List<UnitRateResponse> results = client.fetchNext12Hours();

        assertEquals(0, results.size());
    }

    @Test
    void fetchNext12Hours_WithMissingOptionalField_StillParsesSuccessfully_BoundaryTest() throws Exception {
        // Regression: ensure that missing payment_method and value_exc_vat fields don't break parsing
        // The mapper is configured with FAIL_ON_UNKNOWN_PROPERTIES disabled
        String mockJsonResponse = """
            {
                "count": 1,
                "next": null,
                "previous": null,
                "results": [
                    {
                      "value_inc_vat": 20.0,
                      "valid_from": "2026-03-03T22:30:00Z",
                      "valid_to": "2026-03-03T23:00:00Z"
                    }
                ]
            }
        """;
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(mockJsonResponse));

        // Should not throw deserialization exception
        List<UnitRateResponse> results = client.fetchNext12Hours();

        assertEquals(1, results.size());
        assertEquals(20.0, results.get(0).valueIncVat());
    }

    @Test
    void fetchNext12Hours_WithServerError_PropagatesError_BoundaryTest() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));

        // Depends on how ExternalEnergyPriceClient handles 500s
        // This test documents the behavior (may throw or return error, update as needed)
        assertThrows(Exception.class, () -> client.fetchNext12Hours());
    }

    @Test
    void fetchNext12Hours_WithMalformedJson_PropagatesParseError_BoundaryTest() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{invalid json"));

        assertThrows(Exception.class, () -> client.fetchNext12Hours());
    }

    @Test
    void fetchNext12Hours_UrlParametersIncludeCorrectTimeWindow_IntegrationTest() throws Exception {
        String mockJsonResponse = """
            {
                "count": 0,
                "results": []
            }
        """;
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(mockJsonResponse));

        client.fetchNext12Hours();

        RecordedRequest request = mockWebServer.takeRequest();

        // Verify the time window is exactly 12 hours from the fixed clock time
        String query = request.getPath();
        assertTrue(query.contains("period_from=2026-03-02T23%3A02%3A13Z"), "Should include period_from");
        assertTrue(query.contains("period_to=2026-03-03T11%3A02%3A13Z"), "Should include period_to (12 hours later)");
        assertTrue(query.contains("page_size=24"), "Should include page_size parameter");
    }

    @Test
    void fetchNext12Hours_MultipleConsecutiveCalls_ConstructsUrlCorrectly_RegressionTest() throws Exception {
        String response = """
            {
                "count": 0,
                "results": []
            }
        """;
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(response));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(response));

        // Call twice
        client.fetchNext12Hours();
        client.fetchNext12Hours();

        // Both requests should have been properly formed
        RecordedRequest firstRequest = mockWebServer.takeRequest();
        RecordedRequest secondRequest = mockWebServer.takeRequest();

        assertTrue(firstRequest.getPath().contains("period_from="));
        assertTrue(secondRequest.getPath().contains("period_from="));
    }
}

