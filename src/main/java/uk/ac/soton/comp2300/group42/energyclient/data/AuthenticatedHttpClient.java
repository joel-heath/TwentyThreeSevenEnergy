package uk.ac.soton.comp2300.group42.energyclient.data;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AuthenticatedHttpClient {

    private String accessToken;
    private final HttpClient client;
    private static final String API_ROOT_URL = "https://localhost:8080/api/"; // in production will be something like "https://group42.ecs.soton.ac.uk/api/"

    public AuthenticatedHttpClient() {
        accessToken = null;
        client = HttpClient.newBuilder()
                           .version(HttpClient.Version.HTTP_2)
                           .connectTimeout(Duration.ofSeconds(10))
                           .build();
    }

    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public HttpResponse<String> get(String url) throws IOException, InterruptedException {
        return getAbsolute(API_ROOT_URL + url);
    }

    public HttpResponse<String> getAbsolute(String url) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder().GET(), url);
    }

    public HttpResponse<String> post(String url, String jsonBody) throws IOException, InterruptedException {
        return postAbsolute(API_ROOT_URL + url, jsonBody);
    }
    public HttpResponse<String> postAbsolute(String url, String jsonBody) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)), url);
    }

    private HttpResponse<String> send(HttpRequest.Builder builder, String url) throws IOException, InterruptedException {
        builder.uri(URI.create(url));

        if (accessToken != null && !accessToken.isEmpty()) {
            builder.header("Authorization", "Bearer " + accessToken);
        }

        HttpRequest request = builder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
