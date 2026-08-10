package uk.ac.soton.comp2300.group42.energyserver.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.model.*;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseMembershipRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.PreferencesRepository;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;
import uk.ac.soton.comp2300.group42.user.AuthResponse;
import uk.ac.soton.comp2300.group42.user.ChangePasswordRequest;
import uk.ac.soton.comp2300.group42.user.LoginRequest;
import uk.ac.soton.comp2300.group42.user.RegistrationRequest;
import uk.ac.soton.comp2300.group42.energyserver.exception.InvalidCredentialsException;
import uk.ac.soton.comp2300.group42.energyserver.exception.TokenRefreshException;
import uk.ac.soton.comp2300.group42.energyserver.exception.UserAlreadyExistsException;
import uk.ac.soton.comp2300.group42.energyserver.repository.UserRepository;
import uk.ac.soton.comp2300.group42.energyserver.security.filter.JwtUtils;

import java.time.ZoneId;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final HouseRepository houseRepo;
    private final HouseMembershipRepository membershipRepo;
    private final PreferencesRepository preferencesRepo;

    public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, RefreshTokenService refreshTokenService, HouseRepository houseRepo, HouseMembershipRepository membershipRepo, PreferencesRepository preferencesRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.houseRepo = houseRepo;
        this.membershipRepo = membershipRepo;
        this.preferencesRepo = preferencesRepo;
    }

    @Transactional
    public AuthResponse register(RegistrationRequest request) {
        if (userRepo.findByEmail(request.email()).isPresent())
            throw new UserAlreadyExistsException("Email already exists");

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepo.save(user);

        House house = new House();
        house.setAddress("No address set");
        house.setTimezone(ZoneId.of("UTC"));
        houseRepo.save(house);

        HouseMembership membership = new HouseMembership();
        membership.setUser(user);
        membership.setHouse(house);
        membership.setRole(Role.OWNER);
        membership.setHouseNickname("Primary House");
        membershipRepo.save(membership);

        Preferences preferences = new Preferences();
        preferences.setUser(user);
        preferences.setActiveHouse(house);
        preferences.setLargeFont(false);
        preferences.setColorVision(ColorVision.TYPICAL);
        preferences.setTheme(Theme.LIGHT);
        preferences.setMode(Mode.SIMPLE);
        preferences.setShareLocation(false);
        preferences.setEnergyGoal(1.0);
        preferencesRepo.save(preferences);

        String accessToken = jwtUtils.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepo.findByEmail(request.email())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        String accessToken = jwtUtils.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Transactional(noRollbackFor = TokenRefreshException.class)
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

    public void logout(String token) {
        refreshTokenService.deleteByToken(token);
    }

    public void logoutAll(Long userId) {
        refreshTokenService.deleteByUserId(userId);
    }

    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        verifyPassword(user, request.oldPassword(), "Current password is incorrect");

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        userRepo.save(user);
    }

    @Transactional(readOnly = true)
    public void verifyPassword(User user, String password, String errorMessage) {
        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new InvalidCredentialsException(errorMessage);
    }
}