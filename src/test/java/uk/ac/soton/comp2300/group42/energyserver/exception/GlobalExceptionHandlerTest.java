package uk.ac.soton.comp2300.group42.energyserver.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import uk.ac.soton.comp2300.group42.common.ApiErrorResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest mockRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        mockRequest = mock(WebRequest.class);
        when(mockRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    void handleUserAlreadyExists_ShouldReturn409() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("User with email ksi@primehydration.uk already exists");

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleUserExists(ex, mockRequest);
        ApiErrorResponse error = response.getBody();

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(error);
        assertTrue(error.message().contains("User with email ksi@primehydration.uk already exists"));
    }

    @Test
    void handleInvalidCredentials_ShouldReturn401() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Bad password");

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleInvalidCredentials(ex, mockRequest);
        ApiErrorResponse error = response.getBody();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(error);
        assertTrue(error.message().contains("Bad password"));
    }

    @Test
    void handleTokenRefresh_ShouldReturn401() {
        TokenRefreshException ex = new TokenRefreshException("Invalid refresh token");

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleTokenRefresh(ex, mockRequest);
        ApiErrorResponse error = response.getBody();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(error);
        assertTrue(error.message().contains("Invalid refresh token"));
    }

    @Test
    void handleAccessDenied_ShouldReturn403() {
        AccessDeniedException ex = new AccessDeniedException("User is not a member of this house");

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleAccessDenied(ex, mockRequest);
        ApiErrorResponse error = response.getBody();

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(error);
        assertTrue(error.message().contains("User is not a member of this house"));
    }

    @Test
    void handleResourceNotFound_ShouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("House with ID 1 not found");

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleResourceNotFound(ex, mockRequest);
        ApiErrorResponse error = response.getBody();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(error);
        assertTrue(error.message().contains("House with ID 1 not found"));
    }

    @Test
    void handleResourceConflict_ShouldReturn409() {
        ResourceAlreadyExistsException ex = new ResourceAlreadyExistsException("User already exists");

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleResourceConflict(ex, mockRequest);
        ApiErrorResponse error = response.getBody();

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(error);
        assertTrue(error.message().contains("User already exists"));
    }

    @Test
    void handleSpringSecurityAuthorizationDenied_ShouldReturn403() {
        AuthorizationDeniedException ex = new AuthorizationDeniedException("User is not authorized to access this resource");

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleSpringSecurityAuthorizationDenied(ex, mockRequest);
        ApiErrorResponse error = response.getBody();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(error);
        assertTrue(error.message().contains("Unauthorized: Valid token required to access this resource."));
    }

    @Test
    void handleSpringSecurityAccessDenied_ShouldReturn403() {
        org.springframework.security.access.AccessDeniedException ex = new org.springframework.security.access.AccessDeniedException("User does not have permission to perform this action");

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleSpringSecurityAccessDenied(ex, mockRequest);
        ApiErrorResponse error = response.getBody();

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(error);
        assertTrue(error.message().contains("Forbidden: You do not have permission to perform this action."));
    }

    @Test
    void handleSpringArgumentTypeMismatch_ShouldReturn400() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getValue()).thenReturn("abc");
        when(ex.getName()).thenReturn("houseId");
        doReturn(Long.class).when(ex).getRequiredType();

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleSpringArgumentTypeMismatch(ex, mockRequest);
        ApiErrorResponse error = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(error);
        assertTrue(error.message().contains("Invalid parameter: 'abc' is not a valid value for 'houseId'. Expected type: Long"));
    }

    @Test
    void handleSpringValidationExceptions_ShouldReturn400() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("userDto", "email", "must be a well-formed email address");
        FieldError fieldError2 = new FieldError("userDto", "password", "must not be null");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleSpringValidationExceptions(ex, mockRequest);
        ApiErrorResponse error = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(error);
        assertTrue(error.message().contains("Validation failed:"));
        assertTrue(error.message().contains("email: must be a well-formed email address"));
        assertTrue(error.message().contains("password: must not be null"));
    }

    @Test
    void handleGlobalException_ShouldReturn500() {
        NullPointerException unexpectedException = new NullPointerException("Something went horribly wrong");

        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleGlobalException(unexpectedException, mockRequest);
        ApiErrorResponse error = response.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(error);
        assertTrue(error.message().contains("An unexpected error occurred"));
        assertFalse(error.message().contains("Something went horribly wrong"));
    }
}