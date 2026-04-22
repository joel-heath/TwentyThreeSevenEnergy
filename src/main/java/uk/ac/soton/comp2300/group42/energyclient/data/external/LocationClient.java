package uk.ac.soton.comp2300.group42.energyclient.data.external;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.ExternalMapper;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.LocationApiRootUri;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Singleton
public class LocationClient {

    private final HttpClient client;
    private final JsonMapper mapper;
    private final URI apiRootUri;

    @Inject
    public LocationClient(@ExternalMapper JsonMapper mapper,
                          @LocationApiRootUri URI apiRootUri) {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = mapper;
        this.apiRootUri = apiRootUri;
    }

    public LocationResponse fetchCurrentLocation() {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(apiRootUri)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), LocationResponse.class);
        } catch (Exception e) {
            return new LocationResponse(50.93, -1.39, "Southampton", "UK"); // default to Southampton if API call fails
        }
    }
}
