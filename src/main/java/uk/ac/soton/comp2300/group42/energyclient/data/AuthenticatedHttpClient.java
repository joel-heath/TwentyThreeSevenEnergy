package uk.ac.soton.comp2300.group42.energyclient.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.AuthResponseDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.security.TokenStorageService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class AuthenticatedHttpClient {

    private String accessToken;
    private String refreshToken;
    private final ObjectMapper mapper;
    private final HttpClient client;
    private final TokenStorageService tokenStorage;
    private static final String API_ROOT_URL = "http://localhost:8080/api/"; // in production will be something like "https://group42.ecs.soton.ac.uk/api/"

    @Inject
    public AuthenticatedHttpClient(ObjectMapper mapper, TokenStorageService tokenStorage) {
        this.mapper = mapper;
        this.tokenStorage = tokenStorage;
        this.refreshToken = tokenStorage.getRefreshToken();
        this.accessToken = null;
        this.client = HttpClient.newBuilder()
                           .version(HttpClient.Version.HTTP_2)
                           .connectTimeout(Duration.ofSeconds(10))
                           .build();
    }

    public void setTokenPair(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;

        if (refreshToken != null && !refreshToken.isEmpty()) {
            tokenStorage.saveRefreshToken(refreshToken);
        }
    }

    public void clearTokenPair() {
        this.accessToken = null;
        this.refreshToken = null;
        tokenStorage.clearRefreshToken();
    }

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
        if (accessToken != null && !accessToken.isEmpty())
            builder.header("Authorization", "Bearer " + accessToken);
        HttpRequest request = builder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401 && refreshToken != null) {
            System.out.println("Access token expired. Attempting refresh...");

            if (performRefresh()) {
                builder.setHeader("Authorization", "Bearer " + accessToken);
                response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            }
        }

        return response;
    }

    private boolean performRefresh() {
        try {
            Map<String, String> body = new HashMap<>();
            body.put("refreshToken", refreshToken);
            String json = mapper.writeValueAsString(body);

            HttpRequest refreshRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_ROOT_URL + "auth/refresh"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(refreshRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                AuthResponseDTO authResponse = mapper.readValue(response.body(), AuthResponseDTO.class);
                this.accessToken = authResponse.accessToken();
                // If we choose to rotate refresh tokens, we'd update it here
                // if (authResponse.refreshToken() != null) {
                //     this.refreshToken = authResponse.refreshToken();
                //     tokenStorage.saveRefreshToken(this.refreshToken);
                // }
                return true;
            }
            else {
                tokenStorage.clearRefreshToken();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Refresh failed. User must log in again.");
        return false;
    }
}
