package uk.ac.soton.comp2300.group42.energyserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.exception.AccessDeniedException;
import uk.ac.soton.comp2300.group42.energyserver.exception.ResourceNotFoundException;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.HouseMembership;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseMembershipRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HouseAuthorizationManagerTest {

    @Mock
    private HouseRepository houseRepo;

    @Mock
    private HouseMembershipRepository membershipRepo;

    @InjectMocks
    private HouseAuthorizationManager authManager;

    private User dummyUser;
    private House dummyHouse;
    private HouseMembership dummyMembership;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);

        dummyHouse = new House();
        ReflectionTestUtils.setField(dummyHouse, "id", 10L);

        dummyMembership = new HouseMembership();
        ReflectionTestUtils.setField(dummyMembership, "id", 100L);
        dummyMembership.setUser(dummyUser);
        dummyMembership.setHouse(dummyHouse);
    }

    @Test
    void authorize_ExactRoleMatch_Success() {
        dummyMembership.setRole(Role.RESIDENT);

        when(houseRepo.findById(10L)).thenReturn(Optional.of(dummyHouse));
        when(membershipRepo.findByUserAndHouse(dummyUser, dummyHouse)).thenReturn(Optional.of(dummyMembership));

        HouseMembership result = authManager.authorize(10L, dummyUser, Role.RESIDENT);

        assertThat(result).isEqualTo(dummyMembership);
    }

    @Test
    void authorize_HigherRoleThanRequired_Success() {
        dummyMembership.setRole(Role.OWNER);

        when(houseRepo.findById(10L)).thenReturn(Optional.of(dummyHouse));
        when(membershipRepo.findByUserAndHouse(dummyUser, dummyHouse)).thenReturn(Optional.of(dummyMembership));

        HouseMembership result = authManager.authorize(10L, dummyUser, Role.GUEST);

        assertThat(result).isEqualTo(dummyMembership);
    }

    @Test
    void authorize_InsufficientRole_ThrowsException() {
        dummyMembership.setRole(Role.GUEST);

        when(houseRepo.findById(10L)).thenReturn(Optional.of(dummyHouse));
        when(membershipRepo.findByUserAndHouse(dummyUser, dummyHouse)).thenReturn(Optional.of(dummyMembership));

        assertThatThrownBy(() -> authManager.authorize(10L, dummyUser, Role.OWNER))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("User does not have required permissions to access this resource");
    }

    @Test
    void authorize_HouseNotFound_ThrowsException() {
        when(houseRepo.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authManager.authorize(10L, dummyUser, Role.GUEST))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("House with ID 10 not found");

        verifyNoInteractions(membershipRepo);
    }

    @Test
    void authorize_UserNotMemberOfHouse_ThrowsException() {
        when(houseRepo.findById(10L)).thenReturn(Optional.of(dummyHouse));
        when(membershipRepo.findByUserAndHouse(dummyUser, dummyHouse)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authManager.authorize(10L, dummyUser, Role.GUEST))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("User is not a member of this house");
    }
}