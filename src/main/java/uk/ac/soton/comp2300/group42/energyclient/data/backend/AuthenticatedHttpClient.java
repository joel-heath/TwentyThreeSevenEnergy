package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.energyclient.data.security.TokenStorageService;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;
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
    private final ObjectMapper mapper;
    private final HttpClient client;
    private final SessionManager sessionManager;
    private final TokenStorageService tokenStorage;
    private static final String API_ROOT_URL = "http://localhost:8080/api/"; // in production will be something like "https://group42.ecs.soton.ac.uk/api/"

    @Inject
    public AuthenticatedHttpClient(ObjectMapper mapper, SessionManager sessionManager, TokenStorageService tokenStorage) {
        this.mapper = mapper;
        this.sessionManager = sessionManager;
        this.tokenStorage = tokenStorage;
        this.refreshToken = tokenStorage.getRefreshToken();
        this.accessToken = null;
        this.client = HttpClient.newBuilder()
                                .version(HttpClient.Version.HTTP_2)
                                .connectTimeout(Duration.ofSeconds(10))
                                .build();

        if (this.refreshToken != null && !this.refreshToken.isEmpty()) {
            this.sessionManager.setLoggedIn(true);
        }
    }

    public void setTokenPair(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;

        if (this.refreshToken != null && !this.refreshToken.isEmpty())
            tokenStorage.saveRefreshToken(refreshToken);

        sessionManager.setLoggedIn(true);
    }

    public void clearTokenPair() {
        this.accessToken = null;
        this.refreshToken = null;
        tokenStorage.clearRefreshToken();
        sessionManager.setLoggedIn(false);
    }

    public HttpResponse<String> get(String url) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder().GET(), API_ROOT_URL + url);
    }

    public HttpResponse<String> post(String url, String jsonBody) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)), API_ROOT_URL + url);
    }

    public HttpResponse<String> put(String url, String jsonBody) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody)), API_ROOT_URL + url);
    }

    public HttpResponse<String> delete(String url) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder().DELETE(), API_ROOT_URL + url);
    }

    private HttpResponse<String> send(HttpRequest.Builder builder, String url) throws IOException, InterruptedException {
        builder.uri(URI.create(url));
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
                    .uri(URI.create(API_ROOT_URL + "auth/refresh"))
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
            else {
                clearTokenPair();
                tokenStorage.clearRefreshToken();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Refresh failed. User must log in again.");
        return false;
    }
}
