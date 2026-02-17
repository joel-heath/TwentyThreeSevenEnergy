package uk.ac.soton.comp2300.group42.energyserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uk.ac.soton.comp2300.group42.energyserver.dto.LoginRequest;
import uk.ac.soton.comp2300.group42.energyserver.dto.RegistrationRequest;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.UserRepository;
import uk.ac.soton.comp2300.group42.energyserver.security.JwtUtils;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
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
        Optional<User> user = userRepository.findByEmail(request.email());

        if (user.isPresent() && passwordEncoder.matches(request.password(), user.get().getPassword())) {
            String accessToken = jwtUtils.generateAccessToken(user.get());
            String refreshToken = jwtUtils.generateRefreshToken(user.get());
            return ResponseEntity.ok(Map.of("accessToken", accessToken, "refreshToken", refreshToken));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        try {
            Long id = jwtUtils.extractUserId(refreshToken);
            User user = userRepository.findById(id).orElse(null);
            if (user == null)
                return ResponseEntity.status(401).body("Invalid refresh token");
            // Should verify refresh token exists in DB to allow revocation
            if (jwtUtils.isTokenValid(refreshToken, user)) {
                String newAccessToken = jwtUtils.generateAccessToken(user);
                return ResponseEntity.ok(Map.of("accessToken", newAccessToken, "refreshToken", refreshToken));
            }
        } catch (Exception e) {
            // Token invalid
        }
        return ResponseEntity.status(401).body("Invalid refresh token");
    }
}