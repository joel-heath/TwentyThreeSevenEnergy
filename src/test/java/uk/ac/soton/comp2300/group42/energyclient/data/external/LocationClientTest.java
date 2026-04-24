package uk.ac.soton.comp2300.group42.energyclient.data.external;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LocationClientTest {

    private JsonMapper mapper;
    private LocationClient client;

    @BeforeEach
    void setUp() {
        mapper = mock(JsonMapper.class);
        client = new LocationClient(mapper, URI.create("http://test.com"));
    }

    @Test
    void fetchCurrentLocation_success_returnsParsedResponse() throws Exception {
        LocationResponse expected = new LocationResponse(1, 2, "City", "Country");

        LocationClient spy = spy(client);

        HttpClient mockHttpClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        when(mockResponse.body()).thenReturn("{json}");
        when(mockHttpClient.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        doReturn(mockHttpClient).when(spy).getClient();

        when(mapper.readValue(anyString(), eq(LocationResponse.class)))
                .thenReturn(expected);

        LocationResponse result = spy.fetchCurrentLocation();

        assertEquals(expected, result);
    }

    @Test
    void fetchCurrentLocation_failure_returnsDefault() throws Exception {
        LocationClient spy = spy(client);

        HttpClient mockHttpClient = mock(HttpClient.class);

        // simulate failure
        when(mockHttpClient.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new RuntimeException());

        // ✅ IMPORTANT: ensure getClient() is not null
        doReturn(mockHttpClient).when(spy).getClient();

        LocationResponse result = spy.fetchCurrentLocation();

        assertEquals(50.93, result.lat());
        assertEquals(-1.39, result.lon());
        assertEquals("Southampton", result.city());
        assertEquals("UK", result.country());
    }

    @Test
    void getClient_returnsConstructedClient() {
        assertNotNull(client.getClient());
    }
}
