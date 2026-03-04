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
import uk.ac.soton.comp2300.group42.appliance.ApplianceResponse;
import uk.ac.soton.comp2300.group42.appliance.CreateApplianceRequest;
import uk.ac.soton.comp2300.group42.appliance.UpdateApplianceRequest;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.exception.ResourceNotFoundException;
import uk.ac.soton.comp2300.group42.energyserver.mapper.ApplianceMapper;
import uk.ac.soton.comp2300.group42.energyserver.model.Appliance;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.HouseMembership;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.ApplianceRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.ac.soton.comp2300.group42.energyserver.util.TestUtils.assignId;
import static uk.ac.soton.comp2300.group42.energyserver.util.TestUtils.verifySaveAndCapture;

@ExtendWith(MockitoExtension.class)
class ApplianceServiceTest {

    @Mock
    private ApplianceRepository applianceRepo;

    @Mock
    private HouseAuthorizationManager authManager;

    @Spy
    private ApplianceMapper mapper = Mappers.getMapper(ApplianceMapper.class);

    @InjectMocks
    private ApplianceService applianceService;

    private User dummyUser;
    private House dummyHouse;
    private HouseMembership dummyMembership;
    private Appliance dummyAppliance;

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
        dummyAppliance.setName("Dishwasher");
        dummyAppliance.setHouse(dummyHouse);
    }

    @Test
    void createAppliance_Success() {
        Long houseId = 10L;
        CreateApplianceRequest request = new CreateApplianceRequest("Washing Machine");

        when(authManager.authorize(houseId, dummyUser, Role.RESIDENT)).thenReturn(dummyMembership);
        when(applianceRepo.save(any(Appliance.class))).thenAnswer(app -> assignId(app, 200L));

        ApplianceResponse result = applianceService.createAppliance(houseId, request, dummyUser);

        Appliance saved = verifySaveAndCapture(applianceRepo, Appliance.class);

        assertThat(saved.getId()).isEqualTo(200L);
        assertThat(saved.getName()).isEqualTo("Washing Machine");
        assertThat(saved.getHouse()).isEqualTo(dummyHouse);

        assertThat(result.id()).isEqualTo(200L);
        assertThat(result.name()).isEqualTo("Washing Machine");
        assertThat(result.houseId()).isEqualTo(dummyHouse.getId());
    }

    @Test
    void getApplianceById_Success() {
        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(applianceRepo.findById(100L)).thenReturn(Optional.of(dummyAppliance));

        ApplianceResponse result = applianceService.getApplianceById(10L, 100L, dummyUser);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(100L);
    }

    @Test
    void getApplianceById_InvalidId_ThrowsException() {
        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(applianceRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applianceService.getApplianceById(10L, 999L, dummyUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Appliance with ID 999 not found");

        verifyNoInteractions(mapper);
    }

    @Test
    void getAppliancesByHouseId_Success() {
        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(applianceRepo.findAllByHouse(dummyHouse)).thenReturn(List.of(dummyAppliance));

        List<ApplianceResponse> results = applianceService.getAppliancesByHouseId(10L, dummyUser);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().id()).isEqualTo(100L);
    }

    @Test
    void updateAppliance_Success() {
        UpdateApplianceRequest request = new UpdateApplianceRequest("Dishwasher XTreme with HyperWash");

        when(authManager.authorize(10L, dummyUser, Role.RESIDENT)).thenReturn(dummyMembership);
        when(applianceRepo.findById(100L)).thenReturn(Optional.of(dummyAppliance));

        ApplianceResponse result = applianceService.updateAppliance(10L, 100L, request, dummyUser);

        assertThat(dummyAppliance.getName()).isEqualTo("Dishwasher XTreme with HyperWash");
        assertThat(result.name()).isEqualTo("Dishwasher XTreme with HyperWash");
    }

    @Test
    void updateAppliance_InvalidId_ThrowsException() {
        UpdateApplianceRequest request = new UpdateApplianceRequest("Dishwasher XTreme with HyperWash");

        when(authManager.authorize(10L, dummyUser, Role.RESIDENT)).thenReturn(dummyMembership);
        when(applianceRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applianceService.updateAppliance(10L, 999L, request, dummyUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Appliance with ID 999 not found");

        verifyNoInteractions(mapper);
    }

    @Test
    void deleteAppliance_Success() {
        when(authManager.authorize(10L, dummyUser, Role.RESIDENT)).thenReturn(dummyMembership);
        when(applianceRepo.findById(100L)).thenReturn(Optional.of(dummyAppliance));

        applianceService.deleteAppliance(10L, 100L, dummyUser);

        verify(applianceRepo).delete(dummyAppliance);
    }

    @Test
    void deleteAppliance_InvalidId_ThrowsException() {
        when(authManager.authorize(10L, dummyUser, Role.RESIDENT)).thenReturn(dummyMembership);
        when(applianceRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applianceService.deleteAppliance(10L, 999L, dummyUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Appliance with ID 999 not found");

        verify(applianceRepo, never()).delete(any());
    }
}