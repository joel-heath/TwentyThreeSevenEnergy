package uk.ac.soton.comp2300.group42.energyclient.data.external;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.DataFetchException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.NetworkException;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    void fetchWeatherForecast_success_returnsParsedList() throws Exception {
        WeatherClient spy = Mockito.spy(client);
        doReturn("[{json}]").when(spy).fetchRawData(any());

        WeatherResponse expectedResponse = mock(WeatherResponse.class);
        List<WeatherResponse> expected = List.of(expectedResponse);
        when(mapper.readValue(anyString(), any(TypeReference.class)))
                .thenReturn(expected);

        List<WeatherResponse> result = spy.fetchWeatherForecast(51.1, -1.2);

        assertEquals(expected, result);
    }

    @Test
    void fetchWeatherForecast_invalidJson_throwsDataFetchException() throws Exception {
        WeatherClient spy = Mockito.spy(client);
        doReturn("bad json").when(spy).fetchRawData(any());
        when(mapper.readValue(anyString(), any(TypeReference.class)))
                .thenThrow(mock(JacksonException.class));

        assertThrows(DataFetchException.class, () -> spy.fetchWeatherForecast(51.1, -1.2));
    }

    @Test
    void fetchRawData_success_returnsBody() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{ok}");
        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        WeatherClient testClient = new WeatherClient(mapper, URI.create("http://test.com")) {
            @Override
            protected HttpClient getClient() {
                return httpClient;
            }
        };

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://test.com")).GET().build();
        assertEquals("{ok}", testClient.fetchRawData(request));
    }

    @Test
    void fetchRawData_non200_throwsNetworkException() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(500);
        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        WeatherClient testClient = new WeatherClient(mapper, URI.create("http://test.com")) {
            @Override
            protected HttpClient getClient() {
                return httpClient;
            }
        };

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://test.com")).GET().build();
        assertThrows(NetworkException.class, () -> testClient.fetchRawData(request));
    }

    @Test
    void fetchRawData_ioFailure_throwsNetworkException() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("offline"));

        WeatherClient testClient = new WeatherClient(mapper, URI.create("http://test.com")) {
            @Override
            protected HttpClient getClient() {
                return httpClient;
            }
        };

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://test.com")).GET().build();
        NetworkException ex = assertThrows(NetworkException.class, () -> testClient.fetchRawData(request));
        assertTrue(ex.getMessage().contains("Network error while accessing weather API"));
    }

    @Test
    void fetchRawData_interrupted_throwsNetworkExceptionAndRestoresInterruptStatus() throws Exception {
        Thread.interrupted();

        HttpClient httpClient = mock(HttpClient.class);
        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("interrupted"));

        WeatherClient testClient = new WeatherClient(mapper, URI.create("http://test.com")) {
            @Override
            protected HttpClient getClient() {
                return httpClient;
            }
        };

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://test.com")).GET().build();

        try {
            NetworkException ex = assertThrows(NetworkException.class, () -> testClient.fetchRawData(request));
            assertTrue(ex.getMessage().contains("Interrupted while accessing weather API"));
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void getClient_returnsConstructedClient() {
        assertNotNull(client.getClient());
    }
}
