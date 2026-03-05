package uk.ac.soton.comp2300.group42.energyserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.exception.InvalidCredentialsException;
import uk.ac.soton.comp2300.group42.energyserver.exception.ResourceNotFoundException;
import uk.ac.soton.comp2300.group42.energyserver.mapper.PreferencesMapper;
import uk.ac.soton.comp2300.group42.energyserver.mapper.UserMapper;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.HouseMembership;
import uk.ac.soton.comp2300.group42.energyserver.model.Preferences;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseMembershipRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.PreferencesRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.UserRepository;
import uk.ac.soton.comp2300.group42.preferences.*;
import uk.ac.soton.comp2300.group42.user.DeleteUserRequest;
import uk.ac.soton.comp2300.group42.user.UpdateUserRequest;
import uk.ac.soton.comp2300.group42.user.UserResponse;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PreferencesRepository preferencesRepository;

    @Mock
    private HouseMembershipRepository houseMembershipRepository;

    @Mock
    private HouseService houseService;

    @Mock
    private AuthService authService;

    @Mock
    private HouseAuthorizationManager authManager;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Spy
    private PreferencesMapper preferencesMapper = Mappers.getMapper(PreferencesMapper.class);

    @InjectMocks
    private UserService userService;

    private User dummyUser;
    private Preferences dummyPreferences;
    private HouseMembership dummyMembership;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);
        dummyUser.setName("Batman");
        dummyUser.setEmail("bruce@tab23.net");

        House dummyHouse = new House();
        ReflectionTestUtils.setField(dummyHouse, "id", 10L);

        dummyMembership = new HouseMembership();
        dummyMembership.setHouse(dummyHouse);

        dummyPreferences = new Preferences();
        ReflectionTestUtils.setField(dummyPreferences, "id", 100L);
        dummyPreferences.setUser(dummyUser);
        dummyPreferences.setActiveHouse(dummyHouse);
        dummyPreferences.setTheme(Theme.DARK);
        dummyPreferences.setMode(Mode.ADVANCED);
        dummyPreferences.setColorVision(ColorVision.PROTAN);
        dummyPreferences.setLargeFont(true);
        dummyPreferences.setShareLocation(true);
        dummyPreferences.setEnergyGoal(8.24);
    }

    @Test
    void findAll_Success() {
        when(userRepository.findAll()).thenReturn(List.of(dummyUser));

        List<UserResponse> results = userService.findAll();

        assertThat(results).hasSize(1);
        UserResponse response = results.getFirst();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Batman");
        assertThat(response.email()).isEqualTo("bruce@tab23.net");

        verify(userMapper).toUserResponse(dummyUser);
    }

    @Test
    void findById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(dummyUser));

        UserResponse result = userService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Batman");

        verify(userMapper).toUserResponse(dummyUser);
    }

    @Test
    void findById_NotFound_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User with ID 999 not found");

        verifyNoInteractions(userMapper);
    }

    @Test
    void getCurrentUser_Success() {
        UserResponse result = userService.getCurrentUser(dummyUser);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Batman");
        assertThat(result.email()).isEqualTo("bruce@tab23.net");

        verify(userMapper).toUserResponse(dummyUser);
        verifyNoInteractions(userRepository);
    }

    @Test
    void updateCurrentUser_Success() {
        UpdateUserRequest request = new UpdateUserRequest("Superman", "clark@tab23.net");

        UserResponse result = userService.updateCurrentUser(dummyUser, request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Superman");
        assertThat(result.email()).isEqualTo("clark@tab23.net");
    }

    @Test
    void deleteCurrentUser_OwnsHouse_DeletesHouse() {
        DeleteUserRequest request = new DeleteUserRequest("very-secure-password");

        when(houseMembershipRepository.findByUserAndRole(dummyUser, Role.OWNER)).thenReturn(List.of(dummyMembership));

        userService.deleteCurrentUser(dummyUser, request);

        verify(authService).verifyPassword(eq(dummyUser), eq(request.password()), anyString());
        verify(houseMembershipRepository).findByUserAndRole(dummyUser, Role.OWNER);
        verify(houseService).deleteHouse(dummyMembership.getId(), dummyUser);
        verify(userRepository).delete(dummyUser);
    }

    @Test
    void deleteCurrentUser_OwnsHouse_DoesNotDeleteHouse() {
        DeleteUserRequest request = new DeleteUserRequest("very-secure-password");

        when(houseMembershipRepository.findByUserAndRole(dummyUser, Role.OWNER)).thenReturn(List.of());

        userService.deleteCurrentUser(dummyUser, request);

        verify(authService).verifyPassword(eq(dummyUser), eq(request.password()), anyString());
        verify(houseMembershipRepository).findByUserAndRole(dummyUser, Role.OWNER);
        verify(houseService, never()).deleteHouse(anyLong(), any());
        verify(userRepository).delete(dummyUser);
    }

    @Test
    void deleteCurrentUser_IncorrectPassword_ThrowsException() {
        DeleteUserRequest request = new DeleteUserRequest("wrong-password");

        doThrow(new InvalidCredentialsException("Incorrect password")).when(authService).verifyPassword(eq(dummyUser), eq(request.password()), anyString());

        assertThatThrownBy(() -> userService.deleteCurrentUser(dummyUser, request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Incorrect password");

        verify(userRepository, never()).delete(any());
    }

    @Test
    void getCurrentUserPreferences_Success() {
        when(preferencesRepository.findByUser(dummyUser)).thenReturn(dummyPreferences);

        PreferencesResponse result = userService.getCurrentUserPreferences(dummyUser);

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.activeHouseId()).isEqualTo(10L);
        assertThat(result.theme()).isEqualTo(Theme.DARK);
        assertThat(result.mode()).isEqualTo(Mode.ADVANCED);
        assertThat(result.vision()).isEqualTo(ColorVision.PROTAN);
        assertThat(result.largeFont()).isTrue();
        assertThat(result.shareLocation()).isTrue();
        assertThat(result.energyGoal()).isEqualTo(8.24);

        verify(preferencesMapper).toPreferencesResponse(dummyPreferences);
    }

    @Test
    void updateCurrentUserPreferences_Success() {
        UpdatePreferencesRequest request = new UpdatePreferencesRequest(
                1L, false, ColorVision.DEUTERAN, Theme.LIGHT, Mode.SIMPLE, true, 13.2, 10L
        );

        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(preferencesRepository.findByUser(dummyUser)).thenReturn(dummyPreferences);

        PreferencesResponse result = userService.updateCurrentUserPreferences(dummyUser, request);

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.activeHouseId()).isEqualTo(10L);
        assertThat(result.theme()).isEqualTo(Theme.LIGHT);
        assertThat(result.mode()).isEqualTo(Mode.SIMPLE);
        assertThat(result.vision()).isEqualTo(ColorVision.DEUTERAN);
        assertThat(result.largeFont()).isFalse();
        assertThat(result.shareLocation()).isTrue();
        assertThat(result.energyGoal()).isEqualTo(13.2);
    }
}