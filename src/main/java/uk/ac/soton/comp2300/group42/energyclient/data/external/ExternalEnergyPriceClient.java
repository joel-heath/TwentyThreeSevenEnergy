package uk.ac.soton.comp2300.group42.energyclient.data.external;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.EnergyPriceApiRootUri;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.ExternalMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.DataFetchException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.NetworkException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Singleton
public class ExternalEnergyPriceClient {

    private final HttpClient client;
    private final JsonMapper mapper;
    private final URI apiRootUri;
    private final Clock clock;

    @Inject
    public ExternalEnergyPriceClient(
            @ExternalMapper JsonMapper mapper,
            @EnergyPriceApiRootUri URI apiRootUri,
            Clock clock) {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = mapper;
        this.apiRootUri = apiRootUri;
        this.clock = clock;
    }

    public List<UnitRateResponse> fetchNext12Hours() {
        ZonedDateTime now = ZonedDateTime.now(clock);
        ZonedDateTime twelveHoursLater = now.plusHours(12);

        String fromStr = now.format(DateTimeFormatter.ISO_INSTANT);
        String toStr = twelveHoursLater.format(DateTimeFormatter.ISO_INSTANT);

        String query = "?period_from=" + URLEncoder.encode(fromStr, StandardCharsets.UTF_8) +
                       "&period_to=" + URLEncoder.encode(toStr, StandardCharsets.UTF_8) +
                       "&page_size=24"; // there are 24 half-hour slots in 12 hours

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(apiRootUri.resolve(query))
                .GET()
                .build();

        String response = fetchRawData(request);

        try {
            UpcomingUnitRatesResponse res = mapper.readValue(response, new TypeReference<>() {});
            return res.results().reversed();
        }
        catch (JacksonException e) {
            throw new DataFetchException("Failed to deserialize response from " + request.uri() + " to List<UnitRate>", e);
        }
    }

    public List<UnitRateResponse> fetchNext24Hours() {
        ZonedDateTime now = ZonedDateTime.now(clock);
        ZonedDateTime twentyFourHoursLater = now.plusHours(24);

        String fromStr = now.format(DateTimeFormatter.ISO_INSTANT);
        String toStr = twentyFourHoursLater.format(DateTimeFormatter.ISO_INSTANT);

        String query =  "?period_from=" + URLEncoder.encode(fromStr, StandardCharsets.UTF_8) +
                        "&period_to=" + URLEncoder.encode(toStr, StandardCharsets.UTF_8) +
                        "&page_size=48"; // there are 48 half-hour slots in 24 hours

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(apiRootUri.resolve(query))
                .GET()
                .build();

        String response = fetchRawData(request);

        try {
            UpcomingUnitRatesResponse res = mapper.readValue(response, new TypeReference<>() {});
            return res.results().reversed();
        }
        catch (JacksonException e) {
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
