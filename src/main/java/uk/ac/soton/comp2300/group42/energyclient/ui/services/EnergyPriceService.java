package uk.ac.soton.comp2300.group42.energyclient.ui.services;

import com.google.gson.reflect.TypeToken;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.UnitRate;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class EnergyPriceService {

    private static final String BASE_URL =
            "https://api.octopus.energy/v1/products/AGILE-18-02-21/electricity-tariffs/E-1R-AGILE-18-02-21-A/standard-unit-rates/";

    private final HttpClient client = HttpClient.newHttpClient();

    public String fetchRawData() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }


    public List<UnitRate> parseJson(String jsonResponse) {
        Gson gson = new Gson();

        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

        var resultsArray = jsonObject.get("results");

        Type listType = new TypeToken<List<UnitRate>>() {}.getType();
        return gson.fromJson(resultsArray, listType);
    }

    public List<UnitRate> fetchNext12Hours() throws Exception {

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime twelveHoursLater = now.plusHours(12);

        String fromStr = now.format(DateTimeFormatter.ISO_INSTANT);
        String toStr = twelveHoursLater.format(DateTimeFormatter.ISO_INSTANT);

        // page_size=24 because there are 24 half-hour slots in 12 hours
        String fullUrl = BASE_URL + "?period_from=" + fromStr + "&period_to=" + toStr + "&page_size=24";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(fullUrl)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return parseJson(response.body());
    }
}
