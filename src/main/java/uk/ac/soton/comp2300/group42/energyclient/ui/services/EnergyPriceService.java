package uk.ac.soton.comp2300.group42.energyclient.ui.services;

import com.google.gson.reflect.TypeToken;
import uk.ac.soton.comp2300.group42.energyclient.data.api.UnitRate;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class EnergyPriceService {

    private static final String API_URL =
            "https://api.octopus.energy/v1/products/AGILE-18-02-21/"
                    + "electricity-tariffs/E-1R-AGILE-18-02-21-A/standard-unit-rates/?page_size=1";

    private final HttpClient client = HttpClient.newHttpClient();

    public String fetchRawData() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public List<UnitRate> fetchPriceData() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return parseJson(response.body());
    }

    public List<UnitRate> parseJson(String jsonResponse) {
        Gson gson = new Gson();

        // 1. Parse the string into a JSON Object
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

        // 2. Extract the "results" array specifically
        var resultsArray = jsonObject.get("results");

        // 3. Convert that array into a List of UnitRate objects
        Type listType = new TypeToken<List<UnitRate>>() {}.getType();
        return gson.fromJson(resultsArray, listType);
    }
}
