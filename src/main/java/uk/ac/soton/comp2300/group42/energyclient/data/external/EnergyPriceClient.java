package uk.ac.soton.comp2300.group42.energyclient.data.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.ExternalMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.DataFetchException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.NetworkException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Singleton
public class EnergyPriceClient {

    private static final String BASE_URL = "https://api.octopus.energy/v1/products/AGILE-18-02-21/electricity-tariffs/E-1R-AGILE-18-02-21-A/standard-unit-rates/";

    private final HttpClient client;
    private final ObjectMapper mapper;

    @Inject
    public EnergyPriceClient(@ExternalMapper ObjectMapper mapper) {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = mapper;
    }

    public List<UnitRateResponse> fetchNext12Hours() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime twelveHoursLater = now.plusHours(12);

        String fromStr = now.format(DateTimeFormatter.ISO_INSTANT);
        String toStr = twelveHoursLater.format(DateTimeFormatter.ISO_INSTANT);

        // page_size=24 because there are 24 half-hour slots in 12 hours
        String fullUrl = BASE_URL + "?period_from=" + fromStr + "&period_to=" + toStr + "&page_size=24";

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(fullUrl))
                .GET()
                .build();

        String response = fetchRawData(request);

        try {
            UpcomingUnitRatesResponse res = mapper.readValue(response, new TypeReference<>() {});
            return res.results().reversed();
        }
        catch (JsonProcessingException e) {
            throw new DataFetchException("Failed to deserialize response from " + request.uri() + " to List<UnitRate>", e);
        }
    }

    private String fetchRawData(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
        catch (IOException e) {
            throw new NetworkException("Network error while accessing " + request.uri(), e);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NetworkException("Interrupted while accessing " + request.uri(), e);
        }
    }
}
