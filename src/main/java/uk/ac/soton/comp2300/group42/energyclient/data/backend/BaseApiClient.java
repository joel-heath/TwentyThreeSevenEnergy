package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.NetworkException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.DataFetchException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.UnauthorizedException;

import java.io.IOException;
import java.net.http.HttpResponse;

public abstract class BaseApiClient {

    private final AuthenticatedHttpClient httpClient;
    private final ObjectMapper mapper;

    protected BaseApiClient(AuthenticatedHttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    protected boolean isSuccess(HttpResponse<String> response) {
        return response.statusCode() >= 200 && response.statusCode() < 300;
    }

    protected <T> T get(String path, TypeReference<T> responseType) {
        HttpResponse<String> response = get(path);
        return handleResponse(response, responseType);
    }

    protected HttpResponse<String> get(String path) {
        try {
            return httpClient.get(path);
        }
        catch (IOException e) {
            throw new NetworkException("Network error while accessing " + path, e);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NetworkException("Interrupted while accessing " + path, e);
        }
    }

    protected <T> T post(String path, Object body, TypeReference<T> responseType) {
        HttpResponse<String> response = post(path, body);
        return handleResponse(response, responseType);
    }

    protected HttpResponse<String> post(String path, Object body) {
        try {
            String jsonBody = mapper.writeValueAsString(body);
            return httpClient.post(path, jsonBody);
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

    protected <T> T put(String path, Object body, TypeReference<T> responseType) {
        HttpResponse<String> response = put(path, body);
        return handleResponse(response, responseType);
    }

    protected HttpResponse<String> put(String path, Object body) {
        try {
            String jsonBody = mapper.writeValueAsString(body);
            return httpClient.put(path, jsonBody);
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

    protected <T> T delete(String path, TypeReference<T> responseType) {
        HttpResponse<String> response = delete(path);
        return handleResponse(response, responseType);
    }

    protected HttpResponse<String> delete(String path) {
        try {
            return httpClient.delete(path);
        }
        catch (IOException e) {
            throw new NetworkException("Network error while accessing " + path, e);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NetworkException("Interrupted while accessing " + path, e);
        }
    }

    protected <T> T handleResponse(HttpResponse<String> response,  TypeReference<T> responseType) {
        if (response.statusCode() == 401)
            throw new UnauthorizedException("Unauthorized access to " + response.uri());

        if (!isSuccess(response))
            throw new ApiException("HTTP " + response.statusCode() + " while accessing " + response.uri(), response.statusCode());

        if (response.body() == null || response.body().trim().isEmpty())
            throw new DataFetchException("Empty response body from " + response.uri() + " when deserializing to " + responseType.getType().getTypeName());

        try {
            return mapper.readValue(response.body(), responseType);
        } catch (JsonProcessingException e) {
            throw new DataFetchException("Failed to deserialize response from " + response.uri() + " to " + responseType.getType().getTypeName(), e);
        }
    }
}