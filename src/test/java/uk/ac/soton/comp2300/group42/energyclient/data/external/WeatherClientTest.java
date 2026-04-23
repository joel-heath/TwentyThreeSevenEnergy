package uk.ac.soton.comp2300.group42.energyclient.data.external;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.DataFetchException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.NetworkException;

import java.net.URI;
import java.net.http.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WeatherClientTest {

    private JsonMapper mapper;
    private WeatherClient client;

    @BeforeEach
    void setUp() {
        mapper = mock(JsonMapper.class);
        client = new WeatherClient(mapper, URI.create("http://test.com"));
    }

    @Test
    void fetchCurrentWeather_success_returnsParsedObject() throws Exception {
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("{json}");

        WeatherClient spy = Mockito.spy(client);
        doReturn("{json}").when(spy).fetchRawData(any());

        WeatherResponse expected = mock(WeatherResponse.class);
        when(mapper.readValue(anyString(), any(TypeReference.class)))
                .thenReturn(expected);

        WeatherResponse result = spy.fetchCurrentWeather(50, -1);

        assertEquals(expected, result);
    }

    @Test
    void fetchCurrentWeather_invalidJson_throwsDataFetchException() throws Exception {
        WeatherClient spy = Mockito.spy(client);
        doReturn("bad json").when(spy).fetchRawData(any());

        when(mapper.readValue(anyString(), any(TypeReference.class)))
                .thenThrow(mock(JacksonException.class));

        assertThrows(DataFetchException.class,
                () -> spy.fetchCurrentWeather(50, -1));
    }

    @Test
    void fetchRawData_non200_throwsNetworkException() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(500);
        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        WeatherClient testClient = new WeatherClient(mapper, URI.create("http://test.com")) {
            protected HttpClient getClient() {
                return httpClient;
            }
        };

        assertThrows(NetworkException.class,
                () -> testClient.fetchCurrentWeather(50, -1));
    }
}