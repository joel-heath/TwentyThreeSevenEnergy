package uk.ac.soton.comp2300.group42.energyclient.data.backend;


import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.common.ApiErrorResponse;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.*;

import java.io.IOException;
import java.net.http.HttpResponse;

public abstract class BaseApiClient {

    private final AuthenticatedHttpClient httpClient;
    private final JsonMapper mapper;

    protected BaseApiClient(AuthenticatedHttpClient httpClient, @BackendMapper JsonMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
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
        catch (JacksonException e) {
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
        catch (JacksonException e) {
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

    protected HttpResponse<String> delete(String path, Object body) {
        try {
            String jsonBody = mapper.writeValueAsString(body);
            return httpClient.delete(path, jsonBody);
        }
        catch (JacksonException e) {
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
        throwIfNotSuccess(response);

        if (response.body() == null || response.body().trim().isEmpty())
            throw new DataFetchException("Empty response body from " + response.uri() + " when deserializing to " + responseType.getType().getTypeName());

        try {
            return mapper.readValue(response.body(), responseType);
        }
        catch (JacksonException e) {
            throw new DataFetchException("Failed to deserialize response from " + response.uri() + " to " + responseType.getType().getTypeName(), e);
        }
    }

    protected void throwIfNotSuccess(HttpResponse<String> response) {
        int status = response.statusCode();

        if (status >= 200 && status < 300)
            return;

        ApiErrorResponse error;
        try {
            error = mapper.readValue(response.body(), ApiErrorResponse.class);
        }
        catch (JacksonException e) {
            throw new DataFetchException("Failed to deserialize error response from " + response.uri(), e);
        }

        switch (status) {
            case 400 -> throw new BadRequestException(error.timestamp(), error.error(), error.message(), error.path());
            case 401 -> throw new UnauthorizedException(error.timestamp(), error.error(), error.message(), error.path());
            case 403 -> throw new ForbiddenException(error.timestamp(), error.error(), error.message(), error.path());
            case 404 -> throw new NotFoundException(error.timestamp(), error.error(), error.message(), error.path());
            case 409 -> throw new ConflictException(error.timestamp(), error.error(), error.message(), error.path());
            case 500 -> throw new InternalServerErrorException(error.timestamp(), error.error(), error.message(), error.path());
            default -> throw new ApiException(error.timestamp(), status, error.error(), error.message(), error.path());
        }
    }
}