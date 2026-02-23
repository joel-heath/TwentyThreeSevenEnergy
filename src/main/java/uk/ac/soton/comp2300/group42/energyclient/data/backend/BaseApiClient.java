package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.NetworkException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.DataFetchException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.UnauthorizedException;

import java.io.IOException;
import java.net.http.HttpResponse;

public abstract class BaseApiClient {

    protected final AuthenticatedHttpClient httpClient;
    protected final ObjectMapper mapper;

    protected BaseApiClient(AuthenticatedHttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    protected <T> T get(String path, Class<T> responseType) {
        try {
            HttpResponse<String> response = httpClient.get(path);
            return handleResponse(response, responseType, path);
        }
        catch (IOException e) {
            throw new NetworkException("Network error while accessing " + path, e);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NetworkException("Interrupted while accessing " + path, e);
        }
    }

    protected <T> T post(String path, Object body, Class<T> responseType) {
        try {
            String jsonBody = mapper.writeValueAsString(body);
            HttpResponse<String> response = httpClient.post(path, jsonBody);
            return handleResponse(response, responseType, path);
        }
        catch (JsonProcessingException e) {
            throw new DataFetchException("Failed to serialize request body while accessing " + path, e);
        }
        catch (IOException e) {
            throw new NetworkException("Network error while accessing " + path, e);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NetworkException("Interrupted while accessing " + path, e);
        }
    }

    // Centralized response handling
    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseType, String path) {
        if (response.statusCode() == 401)
            throw new UnauthorizedException("Unauthorized access to " + path);

        if (response.statusCode() < 200 || response.statusCode() >= 300)
            throw new ApiException("HTTP " + response.statusCode() + " while accessing " + path, response.statusCode());

        if (response.body() == null || response.body().trim().isEmpty()) // eg 204 No Content
            return null;

        try {
            return mapper.readValue(response.body(), responseType);
        }
        catch (JsonProcessingException e) {
            throw new DataFetchException("Failed to deserialize response from " + path + " to " + responseType.getSimpleName(), e);
        }
    }
}