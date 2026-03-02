package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.data.security.TokenStorageService;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendApiRootUri;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendMapper;
import uk.ac.soton.comp2300.group42.user.AuthResponse;

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
    private final JsonMapper mapper;
    private final HttpClient client;
    private final TokenStorageService tokenStorage;
    private final URI apiRootUri;

    @Inject
    public AuthenticatedHttpClient(@BackendMapper JsonMapper mapper,
                                   @BackendApiRootUri URI apiRootUri,
                                   TokenStorageService tokenStorage) {
        this.mapper = mapper;
        this.apiRootUri = apiRootUri;
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

        if (this.refreshToken != null && !this.refreshToken.isEmpty())
            tokenStorage.saveRefreshToken(refreshToken);
    }

    public void clearTokenPair() {
        this.accessToken = null;
        this.refreshToken = null;
        tokenStorage.clearRefreshToken();
    }

    public HttpResponse<String> get(String path) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder().GET(), apiRootUri.resolve(path));
    }

    public HttpResponse<String> post(String path, String jsonBody) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)), apiRootUri.resolve(path));
    }

    public HttpResponse<String> put(String path, String jsonBody) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody)), apiRootUri.resolve(path));
    }

    public HttpResponse<String> delete(String path) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder().DELETE(), apiRootUri.resolve(path));
    }

    private HttpResponse<String> send(HttpRequest.Builder builder, URI uri) throws IOException, InterruptedException {
        builder.uri(uri);
        if (accessToken != null && !accessToken.isEmpty())
            builder.setHeader("Authorization", "Bearer " + accessToken);
        HttpRequest request = builder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401 && refreshToken != null) {
            System.out.println("Access token expired. Attempting refresh...");

            if (performRefresh()) {
                HttpRequest retryRequest = builder
                        .setHeader("Authorization", "Bearer " + accessToken)
                        .build();
                response = client.send(retryRequest, HttpResponse.BodyHandlers.ofString());
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
                    .uri(apiRootUri.resolve("auth/refresh"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(refreshRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                AuthResponse authResponse = mapper.readValue(response.body(), AuthResponse.class);
                this.accessToken = authResponse.accessToken();
                // If we choose to rotate refresh tokens, we'd update it here
                // if (authResponse.refreshToken() != null) {
                //     this.refreshToken = authResponse.refreshToken();
                //     tokenStorage.saveRefreshToken(this.refreshToken);
                // }
                return true;
            }
            else clearTokenPair();
        }
        catch (IOException | InterruptedException | JacksonException e) {
            System.out.println("Error during token refresh: " + e.getMessage());
        }
        System.out.println("Refresh failed. User must log in again.");
        return false;
    }
}
