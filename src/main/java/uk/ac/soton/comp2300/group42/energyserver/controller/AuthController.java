package uk.ac.soton.comp2300.group42.energyserver.controller;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uk.ac.soton.comp2300.group42.energyserver.dto.AuthResponse;
import uk.ac.soton.comp2300.group42.energyserver.dto.LoginRequest;
import uk.ac.soton.comp2300.group42.energyserver.dto.RegistrationRequest;
import uk.ac.soton.comp2300.group42.energyserver.model.RefreshToken;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.UserRepository;
import uk.ac.soton.comp2300.group42.energyserver.security.JwtUtils;
import uk.ac.soton.comp2300.group42.energyserver.service.RefreshTokenService;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils,
                          RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent())
            return ResponseEntity.badRequest().body("Email already exists");

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.email());

        if (optionalUser.isEmpty())
            return ResponseEntity.status(401).body("Invalid credentials");

        User user = optionalUser.get();

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            return ResponseEntity.status(401).body("Invalid credentials");

        String accessToken = jwtUtils.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody AuthResponse request) {
        final String requestRefreshToken = request.refreshToken();

        var response = refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> ResponseEntity.ok(new AuthResponse(
                        jwtUtils.generateAccessToken(user),
                        requestRefreshToken)));

        return response.isPresent() ? response.get() : ResponseEntity.status(401).body("Invalid refresh token");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody AuthResponse request) {
        if (request.refreshToken() == null || request.refreshToken().isEmpty()) {
            return ResponseEntity.ok("Log out successful");
        }

        refreshTokenService.deleteByToken(request.refreshToken());

        return ResponseEntity.ok("Log out successful");
    }

    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAll(@RequestBody AuthResponse request) {
        if (request.refreshToken() == null || request.refreshToken().isEmpty()) {
            return ResponseEntity.ok("Log out successful");
        }

        refreshTokenService.findByToken(request.refreshToken())
                .map(RefreshToken::getUser)
                .map(User::getId)
                .ifPresent(refreshTokenService::deleteByUserId);

        return ResponseEntity.ok("Log out successful");
    }
}