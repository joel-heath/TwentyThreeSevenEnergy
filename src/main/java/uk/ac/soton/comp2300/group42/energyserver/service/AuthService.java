package uk.ac.soton.comp2300.group42.energyserver.service;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uk.ac.soton.comp2300.group42.energyserver.dto.AuthResponse;
import uk.ac.soton.comp2300.group42.energyserver.dto.LoginRequest;
import uk.ac.soton.comp2300.group42.energyserver.dto.RegistrationRequest;
import uk.ac.soton.comp2300.group42.energyserver.exception.InvalidCredentialsException;
import uk.ac.soton.comp2300.group42.energyserver.exception.TokenRefreshException;
import uk.ac.soton.comp2300.group42.energyserver.exception.UserAlreadyExistsException;
import uk.ac.soton.comp2300.group42.energyserver.model.RefreshToken;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.UserRepository;
import uk.ac.soton.comp2300.group42.energyserver.security.JwtUtils;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    public void register(RegistrationRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent())
            throw new UserAlreadyExistsException("Email already exists");

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        String accessToken = jwtUtils.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    public AuthResponse refresh(String requestRefreshToken) {
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtUtils.generateAccessToken(user);
                    return new AuthResponse(newAccessToken, requestRefreshToken);
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token is not in database"));
    }

    @Transactional
    public void logout(String token) {
        refreshTokenService.deleteByToken(token);
    }

    @Transactional
    public void logoutAll(Long userId) {
        refreshTokenService.deleteByUserId(userId);
    }
}