package uk.ac.soton.comp2300.group42.energyclient.data.external;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.WeatherApiRootUri;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.ExternalMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.DataFetchException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.NetworkException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Singleton
public class WeatherClient {

    private final HttpClient client;
    private final JsonMapper mapper;
    private final URI apiRootUri;

    @Inject
    public WeatherClient(@ExternalMapper JsonMapper mapper,
                         @WeatherApiRootUri URI apiRootUri
        ) {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = mapper;
        this.apiRootUri = apiRootUri;
    }

    public WeatherResponse fetchCurrentWeather(double lat, double lon) {
        String query = String.format("?latitude=%f&longitude=%f&current=temperature_2m,shortwave_radiation&hourly=temperature_2m,shortwave_radiation&forecast_days=1",
                lat, lon);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(apiRootUri.resolve(query))
                .GET()
                .build();

        String response = fetchRawData(request);

        try {
            return mapper.readValue(response, new TypeReference<>() {});
        } catch (JacksonException e) {
            throw new DataFetchException("Failed to deserialize weather data from " + request.uri(), e);
        }
    }

    public List<WeatherResponse> fetchWeatherForecast(double lat, double lon) {
        String query = String.format("?latitude=%f&longitude=%f&current=temperature_2m,shortwave_radiation&hourly=temperature_2m,shortwave_radiation&forecast_days=7",
                lat, lon);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(apiRootUri.resolve(query))
                .GET()
                .build();

        String response = fetchRawData(request);

        try {
            return mapper.readValue(response, new TypeReference<>() {});
        } catch (JacksonException e) {
            throw new DataFetchException("Failed to deserialize weather data from " + request.uri(), e);
        }
    }

    protected String fetchRawData(HttpRequest request) {
        try {
            HttpResponse<String> response = getClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new NetworkException("Weather API returned status: " + response.statusCode(), null);
            }
            return response.body();
        } catch (IOException e) {
            throw new NetworkException("Network error while accessing weather API: " + request.uri(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NetworkException("Interrupted while accessing weather API", e);
        }
    }

    protected HttpClient getClient() {
        return client;
    }
}