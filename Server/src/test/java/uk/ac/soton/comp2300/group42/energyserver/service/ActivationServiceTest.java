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
import uk.ac.soton.comp2300.group42.activation.ActivationResponse;
import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.activation.CreateActivationRequest;
import uk.ac.soton.comp2300.group42.activation.UpdateActivationRequest;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.exception.ResourceNotFoundException;
import uk.ac.soton.comp2300.group42.energyserver.mapper.ActivationMapper;
import uk.ac.soton.comp2300.group42.energyserver.model.*;
import uk.ac.soton.comp2300.group42.energyserver.repository.ActivationRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.ApplianceRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.ac.soton.comp2300.group42.energyserver.util.TestUtils.assignId;

@ExtendWith(MockitoExtension.class)
class ActivationServiceTest {

    @Mock
    private ActivationRepository activationRepo;

    @Mock
    private ApplianceRepository applianceRepo;

    @Mock
    private HouseAuthorizationManager authManager;

    @Spy
    private ActivationMapper mapper = Mappers.getMapper(ActivationMapper.class);

    @InjectMocks
    private ActivationService activationService;

    private User dummyUser;
    private House dummyHouse;
    private HouseMembership dummyMembership;
    private Appliance dummyAppliance;
    private Activation dummyActivation;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);

        dummyHouse = new House();
        ReflectionTestUtils.setField(dummyHouse, "id", 10L);

        dummyMembership = new HouseMembership();
        dummyMembership.setHouse(dummyHouse);

        dummyAppliance = new Appliance();
        ReflectionTestUtils.setField(dummyAppliance, "id", 100L);
        dummyAppliance.setHouse(dummyHouse);

        dummyActivation = new Activation();
        ReflectionTestUtils.setField(dummyActivation, "id", 1000L);
        dummyActivation.setAppliance(dummyAppliance);
        dummyActivation.setActivationTime(LocalTime.of(20, 0));
    }

    @Test
    void createActivation_NonRecurring_Success() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        CreateActivationRequest request = new CreateActivationRequest(
                100L, 10L, ActivationType.NON_RECURRING, LocalTime.of(14, 30), tomorrow,
                null, null, null, null, null, null, null
        );

        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(applianceRepo.findById(100L)).thenReturn(Optional.of(dummyAppliance));
        when(activationRepo.save(any(Activation.class))).thenAnswer(act -> assignId(act, 2000L));

        ActivationResponse result = activationService.createActivation(10L, request, dummyUser);

        verify(activationRepo).save(any(Activation.class));
        assertThat(result.applianceId()).isEqualTo(dummyAppliance.getId());
        assertThat(result.houseId()).isEqualTo(dummyHouse.getId());
        assertThat(result.type()).isEqualTo(ActivationType.NON_RECURRING);
        assertThat(result.activationTime()).isEqualTo(LocalTime.of(14, 30));
        assertThat(result.activationDate()).isEqualTo(tomorrow);
        assertThat(result.recursMonday()).isNull();
        assertThat(result.recursTuesday()).isNull();
        assertThat(result.recursWednesday()).isNull();
        assertThat(result.recursThursday()).isNull();
        assertThat(result.recursFriday()).isNull();
        assertThat(result.recursSaturday()).isNull();
        assertThat(result.recursSunday()).isNull();
    }

    @Test
    void createActivation_Recurring_Success() {
        CreateActivationRequest request = new CreateActivationRequest(
                100L, 10L, ActivationType.RECURRING, LocalTime.of(14, 30), null,
                true, false, true, false, false, false, false
        );

        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(applianceRepo.findById(100L)).thenReturn(Optional.of(dummyAppliance));
        when(activationRepo.save(any(Activation.class))).thenAnswer(act -> assignId(act, 2000L));

        ActivationResponse result = activationService.createActivation(10L, request, dummyUser);

        verify(activationRepo).save(any(Activation.class));
        assertThat(result.applianceId()).isEqualTo(dummyAppliance.getId());
        assertThat(result.houseId()).isEqualTo(dummyHouse.getId());
        assertThat(result.type()).isEqualTo(ActivationType.RECURRING);
        assertThat(result.activationTime()).isEqualTo(LocalTime.of(14, 30));
        assertThat(result.activationDate()).isNull();
        assertThat(result.recursMonday()).isTrue();
        assertThat(result.recursTuesday()).isFalse();
        assertThat(result.recursWednesday()).isTrue();
        assertThat(result.recursThursday()).isFalse();
        assertThat(result.recursFriday()).isFalse();
        assertThat(result.recursSaturday()).isFalse();
        assertThat(result.recursSunday()).isFalse();
    }

    @Test
    void createActivation_ApplianceNotInHouse_ThrowsException() {
        House otherHouse = new House();
        ReflectionTestUtils.setField(otherHouse, "id", 20L);
        dummyAppliance.setHouse(otherHouse);

        CreateActivationRequest request = new CreateActivationRequest(
                100L, 10L, ActivationType.NON_RECURRING, LocalTime.now(), LocalDate.now(),
                null, null, null, null, null, null, null
        );

        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(applianceRepo.findById(100L)).thenReturn(Optional.of(dummyAppliance));

        assertThatThrownBy(() -> activationService.createActivation(10L, request, dummyUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found in this house");

        verify(activationRepo, never()).save(any());
    }


    @Test
    void getActivationById_Success() {
        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(activationRepo.findById(1000L)).thenReturn(Optional.of(dummyActivation));

        ActivationResponse result = activationService.getActivationById(10L, 1000L, dummyUser);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1000L);
    }

    @Test
    void getActivationById_InvalidId_ThrowsException() {
        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(activationRepo.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activationService.getActivationById(10L, 9999L, dummyUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Activation with ID 9999 not found");

        verifyNoInteractions(mapper);
    }

    @Test
    void getActivationsByHouseId_Success() {
        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(activationRepo.findByAppliance_House(dummyHouse)).thenReturn(List.of(dummyActivation));

        List<ActivationResponse> results = activationService.getActivationsByHouseId(10L, dummyUser);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().id()).isEqualTo(1000L);
    }


    @Test
    void updateActivation_NonRecurring_Success() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        Appliance otherAppliance = new Appliance();
        ReflectionTestUtils.setField(otherAppliance, "id", 200L);
        otherAppliance.setHouse(dummyHouse);

        dummyActivation.setType(ActivationType.NON_RECURRING);
        dummyActivation.setActivationDate(LocalDate.now());

        UpdateActivationRequest request = new UpdateActivationRequest(
                200L, 10L, ActivationType.NON_RECURRING, LocalTime.of(8, 0), tomorrow,
                null, null, null, null, null, null, null
        );

        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(applianceRepo.findById(200L)).thenReturn(Optional.of(otherAppliance));
        when(activationRepo.findById(1000L)).thenReturn(Optional.of(dummyActivation));

        activationService.updateActivation(10L, 1000L, request, dummyUser);

        assertThat(dummyActivation.getId()).isEqualTo(1000L);
        assertThat(dummyActivation.getAppliance()).isEqualTo(otherAppliance);
        assertThat(dummyActivation.getType()).isEqualTo(ActivationType.NON_RECURRING);
        assertThat(dummyActivation.getActivationTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(dummyActivation.getActivationDate()).isEqualTo(tomorrow);
        assertThat(dummyActivation.getRecursMonday()).isNull();
        assertThat(dummyActivation.getRecursTuesday()).isNull();
        assertThat(dummyActivation.getRecursWednesday()).isNull();
        assertThat(dummyActivation.getRecursThursday()).isNull();
        assertThat(dummyActivation.getRecursFriday()).isNull();
        assertThat(dummyActivation.getRecursSaturday()).isNull();
        assertThat(dummyActivation.getRecursSunday()).isNull();
    }

    @Test
    void updateActivation_Recurring_Success() {
        Appliance otherAppliance = new Appliance();
        ReflectionTestUtils.setField(otherAppliance, "id", 200L);
        otherAppliance.setHouse(dummyHouse);

        dummyActivation.setType(ActivationType.RECURRING);
        dummyActivation.setRecursMonday(true);
        dummyActivation.setRecursTuesday(false);
        dummyActivation.setRecursWednesday(true);
        dummyActivation.setRecursThursday(false);
        dummyActivation.setRecursFriday(false);
        dummyActivation.setRecursSaturday(false);
        dummyActivation.setRecursSunday(true);

        UpdateActivationRequest request = new UpdateActivationRequest(
                200L, 10L, ActivationType.RECURRING, LocalTime.of(8, 0), null,
                false, true, false, true, true, true, true
        );

        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(applianceRepo.findById(200L)).thenReturn(Optional.of(otherAppliance));
        when(activationRepo.findById(1000L)).thenReturn(Optional.of(dummyActivation));

        activationService.updateActivation(10L, 1000L, request, dummyUser);

        assertThat(dummyActivation.getId()).isEqualTo(1000L);
        assertThat(dummyActivation.getAppliance()).isEqualTo(otherAppliance);
        assertThat(dummyActivation.getType()).isEqualTo(ActivationType.RECURRING);
        assertThat(dummyActivation.getActivationTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(dummyActivation.getActivationDate()).isNull();
        assertThat(dummyActivation.getRecursMonday()).isFalse();
        assertThat(dummyActivation.getRecursTuesday()).isTrue();
        assertThat(dummyActivation.getRecursWednesday()).isFalse();
        assertThat(dummyActivation.getRecursThursday()).isTrue();
        assertThat(dummyActivation.getRecursFriday()).isTrue();
        assertThat(dummyActivation.getRecursSaturday()).isTrue();
        assertThat(dummyActivation.getRecursSunday()).isTrue();
    }

    @Test
    void updateActivation_ChangeToRecurring_Success() {
        Appliance otherAppliance = new Appliance();
        ReflectionTestUtils.setField(otherAppliance, "id", 200L);
        otherAppliance.setHouse(dummyHouse);

        dummyActivation.setType(ActivationType.NON_RECURRING);
        dummyActivation.setActivationDate(LocalDate.now());

        UpdateActivationRequest request = new UpdateActivationRequest(
                200L, 10L, ActivationType.RECURRING, LocalTime.of(8, 0), null,
                true, false, true, false, false, true, false
        );

        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(applianceRepo.findById(200L)).thenReturn(Optional.of(otherAppliance));
        when(activationRepo.findById(1000L)).thenReturn(Optional.of(dummyActivation));

        activationService.updateActivation(10L, 1000L, request, dummyUser);

        assertThat(dummyActivation.getId()).isEqualTo(1000L);
        assertThat(dummyActivation.getAppliance()).isEqualTo(otherAppliance);
        assertThat(dummyActivation.getType()).isEqualTo(ActivationType.RECURRING);
        assertThat(dummyActivation.getActivationTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(dummyActivation.getActivationDate()).isNull();
        assertThat(dummyActivation.getRecursMonday()).isTrue();
        assertThat(dummyActivation.getRecursTuesday()).isFalse();
        assertThat(dummyActivation.getRecursWednesday()).isTrue();
        assertThat(dummyActivation.getRecursThursday()).isFalse();
        assertThat(dummyActivation.getRecursFriday()).isFalse();
        assertThat(dummyActivation.getRecursSaturday()).isTrue();
        assertThat(dummyActivation.getRecursSunday()).isFalse();
    }

    @Test
    void updateActivation_ChangeToNonRecurring_Success() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        Appliance otherAppliance = new Appliance();
        ReflectionTestUtils.setField(otherAppliance, "id", 200L);
        otherAppliance.setHouse(dummyHouse);

        dummyActivation.setType(ActivationType.RECURRING);
        dummyActivation.setRecursMonday(true);
        dummyActivation.setRecursTuesday(false);
        dummyActivation.setRecursWednesday(true);
        dummyActivation.setRecursThursday(false);
        dummyActivation.setRecursFriday(false);
        dummyActivation.setRecursSaturday(true);
        dummyActivation.setRecursSunday(false);

        UpdateActivationRequest request = new UpdateActivationRequest(
                200L, 10L, ActivationType.NON_RECURRING, LocalTime.of(8, 0), tomorrow,
                true, false, true, false, false, true, false
        );

        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(applianceRepo.findById(200L)).thenReturn(Optional.of(otherAppliance));
        when(activationRepo.findById(1000L)).thenReturn(Optional.of(dummyActivation));

        activationService.updateActivation(10L, 1000L, request, dummyUser);

        assertThat(dummyActivation.getId()).isEqualTo(1000L);
        assertThat(dummyActivation.getAppliance()).isEqualTo(otherAppliance);
        assertThat(dummyActivation.getType()).isEqualTo(ActivationType.NON_RECURRING);
        assertThat(dummyActivation.getActivationTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(dummyActivation.getActivationDate()).isEqualTo(tomorrow);
        assertThat(dummyActivation.getRecursMonday()).isNull();
        assertThat(dummyActivation.getRecursTuesday()).isNull();
        assertThat(dummyActivation.getRecursWednesday()).isNull();
        assertThat(dummyActivation.getRecursThursday()).isNull();
        assertThat(dummyActivation.getRecursFriday()).isNull();
        assertThat(dummyActivation.getRecursSaturday()).isNull();
        assertThat(dummyActivation.getRecursSunday()).isNull();
    }

    @Test
    void updateActivation_ApplianceNotInHouse_ThrowsException() {
        House otherHouse = new House();
        ReflectionTestUtils.setField(otherHouse, "id", 20L);

        Appliance otherAppliance = new Appliance();
        ReflectionTestUtils.setField(otherAppliance, "id", 200L);
        otherAppliance.setHouse(otherHouse);

        UpdateActivationRequest request = new UpdateActivationRequest(
                200L, 10L, ActivationType.NON_RECURRING, LocalTime.now(), LocalDate.now(),
                null, null, null, null, null, null, null
        );

        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(applianceRepo.findById(200L)).thenReturn(Optional.of(otherAppliance));

        assertThatThrownBy(() -> activationService.updateActivation(10L, 1000L, request, dummyUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found in this house");

        verify(activationRepo, never()).save(any());
    }

    @Test
    void updateActivation_InvalidId_ThrowsException() {
        UpdateActivationRequest request = new UpdateActivationRequest(
                100L, 10L, ActivationType.NON_RECURRING, LocalTime.now(), LocalDate.now(),
                null, null, null, null, null, null, null
        );

        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(applianceRepo.findById(100L)).thenReturn(Optional.of(dummyAppliance));
        when(activationRepo.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activationService.updateActivation(10L, 9999L, request, dummyUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Activation with ID 9999 not found in this house");

        verify(activationRepo, never()).save(any());
    }

    @Test
    void deleteActivation_Success() {
        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(activationRepo.findById(1000L)).thenReturn(Optional.of(dummyActivation));

        activationService.deleteActivation(10L, 1000L, dummyUser);

        verify(activationRepo).delete(dummyActivation);
    }

    @Test
    void deleteActivation_InvalidId_ThrowsException() {
        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(activationRepo.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activationService.deleteActivation(10L, 9999L, dummyUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Activation with ID 9999 not found in this house");

        verify(activationRepo, never()).delete(any());
    }
}