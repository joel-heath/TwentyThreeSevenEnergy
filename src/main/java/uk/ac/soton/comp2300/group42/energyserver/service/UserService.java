package uk.ac.soton.comp2300.group42.energyserver.service;

import org.springframework.stereotype.Service;

import uk.ac.soton.comp2300.group42.energyserver.mapper.PreferencesMapper;
import uk.ac.soton.comp2300.group42.energyserver.mapper.UserMapper;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.PreferencesRepository;
import uk.ac.soton.comp2300.group42.preferences.PreferencesResponse;
import uk.ac.soton.comp2300.group42.user.UserResponse;
import uk.ac.soton.comp2300.group42.energyserver.exception.ResourceNotFoundException;
import uk.ac.soton.comp2300.group42.energyserver.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PreferencesRepository preferencesRepository;
    private final UserMapper userMapper;
    private final PreferencesMapper preferencesMapper;

    public UserService(UserRepository userRepository, PreferencesRepository preferencesRepository, UserMapper userMapper, PreferencesMapper preferencesMapper) {
        this.userRepository = userRepository;
        this.preferencesRepository = preferencesRepository;
        this.userMapper = userMapper;
        this.preferencesMapper = preferencesMapper;
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public UserResponse findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));
    }

    public UserResponse getCurrentUser(User user) {
        return userMapper.toUserResponse(user);
    }

    public PreferencesResponse getCurrentUserPreferences(User user) {
        return preferencesMapper.toPreferencesResponse(preferencesRepository.findByUser(user));
    }
}
