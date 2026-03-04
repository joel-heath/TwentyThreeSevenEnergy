package uk.ac.soton.comp2300.group42.energyserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uk.ac.soton.comp2300.group42.energyserver.service.AuthService;
import uk.ac.soton.comp2300.group42.preferences.PreferencesResponse;
import uk.ac.soton.comp2300.group42.preferences.UpdatePreferencesRequest;
import uk.ac.soton.comp2300.group42.user.DeleteUserRequest;
import uk.ac.soton.comp2300.group42.user.UpdateUserRequest;
import uk.ac.soton.comp2300.group42.user.ChangePasswordRequest;
import uk.ac.soton.comp2300.group42.user.UserResponse;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserService service;
    private final AuthService authService;

    public UserController(UserService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getCurrentUser(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(service.updateCurrentUser(user, request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(
            @AuthenticationPrincipal User user,
            @RequestBody DeleteUserRequest request) {
        service.deleteCurrentUser(user, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changeCurrentUserPassword(
            @AuthenticationPrincipal User user,
            @RequestBody ChangePasswordRequest request) {
        authService.changePassword(user, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/preferences")
    public ResponseEntity<PreferencesResponse> getCurrentUserPreferences(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getCurrentUserPreferences(user));
    }

    @PutMapping("/me/preferences")
    public ResponseEntity<PreferencesResponse> updateCurrentUserPreferences(
            @AuthenticationPrincipal User user,
            @RequestBody UpdatePreferencesRequest request) {
        return ResponseEntity.ok(service.updateCurrentUserPreferences(user, request));
    }
}