package uk.ac.soton.comp2300.group42.energyserver.service;

import org.springframework.stereotype.Service;

import uk.ac.soton.comp2300.group42.energyserver.mapper.UserMapper;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.user.UserResponse;
import uk.ac.soton.comp2300.group42.energyserver.exception.ResourceNotFoundException;
import uk.ac.soton.comp2300.group42.energyserver.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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
}
