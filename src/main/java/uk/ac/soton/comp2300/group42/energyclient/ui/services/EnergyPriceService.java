package uk.ac.soton.comp2300.group42.energyclient.ui.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
}
