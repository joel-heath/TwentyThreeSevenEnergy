package uk.ac.soton.comp2300.group42.energyclient.data.external;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnergyPriceClientTest {

    private MockWebServer mockWebServer;
    private EnergyPriceClient client;

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

        client = new EnergyPriceClient(mapper, mockBaseUrl, fixedClock);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void fetchNext12Hours_ConstructsCorrectUrlAndParsesResponse() throws Exception {
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
}