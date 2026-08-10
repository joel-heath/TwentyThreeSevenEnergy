package uk.ac.soton.comp2300.group42.energyserver.service;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.mapper.PreferencesMapper;
import uk.ac.soton.comp2300.group42.energyserver.mapper.UserMapper;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseMembershipRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.PreferencesRepository;
import uk.ac.soton.comp2300.group42.preferences.PreferencesResponse;
import uk.ac.soton.comp2300.group42.preferences.UpdatePreferencesRequest;
import uk.ac.soton.comp2300.group42.user.DeleteUserRequest;
import uk.ac.soton.comp2300.group42.user.UpdateUserRequest;
import uk.ac.soton.comp2300.group42.user.UserResponse;
import uk.ac.soton.comp2300.group42.energyserver.exception.ResourceNotFoundException;
import uk.ac.soton.comp2300.group42.energyserver.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PreferencesRepository preferencesRepository;
    private final HouseMembershipRepository houseMembershipRepository;
    private final HouseService houseService;
    private final AuthService authService;
    private final HouseAuthorizationManager authManager;
    private final UserMapper userMapper;
    private final PreferencesMapper preferencesMapper;

    public UserService(UserRepository userRepository,
                       PreferencesRepository preferencesRepository,
                       HouseMembershipRepository houseMembershipRepository,
                       HouseService houseService,
                       AuthService authService,
                       HouseAuthorizationManager authManager,
                       UserMapper userMapper,
                       PreferencesMapper preferencesMapper) {
        this.userRepository = userRepository;
        this.preferencesRepository = preferencesRepository;
        this.houseMembershipRepository = houseMembershipRepository;
        this.houseService = houseService;
        this.authService = authService;
        this.authManager = authManager;
        this.userMapper = userMapper;
        this.preferencesMapper = preferencesMapper;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(User user) {
        return userMapper.toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public PreferencesResponse getCurrentUserPreferences(User user) {
        return preferencesMapper.toPreferencesResponse(preferencesRepository.findByUser(user));
    }

    @Transactional
    public UserResponse updateCurrentUser(User user, UpdateUserRequest request) {
        user.setName(request.name());
        user.setEmail(request.email());

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Transactional
    public void deleteCurrentUser(User user, DeleteUserRequest request) {
        authService.verifyPassword(user, request.password(), "Incorrect password");

        var memberships = houseMembershipRepository.findByUserAndRole(user, Role.OWNER);
        for (var membership : memberships)
            houseService.deleteHouse(membership.getId(), user);

        userRepository.delete(user);
    }

    @Transactional
    public PreferencesResponse updateCurrentUserPreferences(User user, UpdatePreferencesRequest request) {
        var house = authManager.authorize(request.activeHouseId(), user, Role.GUEST).getHouse();
        var preferences = preferencesRepository.findByUser(user);

        preferences.setLargeFont(request.largeFont());
        preferences.setColorVision(request.vision());
        preferences.setTheme(request.theme());
        preferences.setMode(request.mode());
        preferences.setShareLocation(request.shareLocation());
        preferences.setEnergyGoal(request.energyGoal());
        preferences.setActiveHouse(house);

        return preferencesMapper.toPreferencesResponse(preferences);
    }
}
