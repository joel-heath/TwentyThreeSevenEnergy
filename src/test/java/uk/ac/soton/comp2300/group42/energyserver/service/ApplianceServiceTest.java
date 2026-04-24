package uk.ac.soton.comp2300.group42.energyserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

import java.util.Collections;
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
    private final Long HOUSE_ID = 10L;
    private final Long APPLIANCE_ID = 100L;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);

        dummyHouse = new House();
        ReflectionTestUtils.setField(dummyHouse, "id", HOUSE_ID);

        dummyMembership = new HouseMembership();
        dummyMembership.setHouse(dummyHouse);

        dummyAppliance = new Appliance();
        ReflectionTestUtils.setField(dummyAppliance, "id", APPLIANCE_ID);
        dummyAppliance.setName("Dishwasher");
        dummyAppliance.setHouse(dummyHouse);
    }

    @Nested
    @DisplayName("createAppliance Tests")
    class CreateApplianceTests {
        @Test
        @DisplayName("Success - Should save and return response")
        void success() {
            CreateApplianceRequest request = new CreateApplianceRequest("Washing Machine");

            when(authManager.authorize(HOUSE_ID, dummyUser, Role.RESIDENT)).thenReturn(dummyMembership);
            when(applianceRepo.save(any(Appliance.class))).thenAnswer(app -> assignId(app, 200L));

            ApplianceResponse result = applianceService.createAppliance(HOUSE_ID, request, dummyUser);

            Appliance saved = verifySaveAndCapture(applianceRepo, Appliance.class);
            assertThat(saved.getName()).isEqualTo("Washing Machine");
            assertThat(saved.getHouse()).isEqualTo(dummyHouse);
            assertThat(result.id()).isEqualTo(200L);
        }

        @Test
        @DisplayName("Failure - Unauthorized user should throw exception")
        void unauthorized_ThrowsException() {
            CreateApplianceRequest request = new CreateApplianceRequest("Washing Machine");
            when(authManager.authorize(anyLong(), any(), eq(Role.RESIDENT)))
                    .thenThrow(new RuntimeException("Access Denied"));

            assertThatThrownBy(() -> applianceService.createAppliance(HOUSE_ID, request, dummyUser))
                    .isInstanceOf(RuntimeException.class);

            verifyNoInteractions(applianceRepo);
        }
    }

    @Nested
    @DisplayName("getApplianceById Tests")
    class GetApplianceByIdTests {
        @Test
        @DisplayName("Success - Should return found appliance")
        void success() {
            when(authManager.authorize(HOUSE_ID, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
            when(applianceRepo.findById(APPLIANCE_ID)).thenReturn(Optional.of(dummyAppliance));

            ApplianceResponse result = applianceService.getApplianceById(HOUSE_ID, APPLIANCE_ID, dummyUser);

            assertThat(result.id()).isEqualTo(APPLIANCE_ID);
            verify(applianceRepo).findById(APPLIANCE_ID);
        }

        @Test
        @DisplayName("Failure - ResourceNotFoundException for invalid ID")
        void notFound_ThrowsException() {
            when(authManager.authorize(HOUSE_ID, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
            when(applianceRepo.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applianceService.getApplianceById(HOUSE_ID, 999L, dummyUser))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Failure - Authorization failure should block execution")
        void unauthorized_ThrowsException() {
            when(authManager.authorize(anyLong(), any(), any()))
                    .thenThrow(new RuntimeException("Access Denied"));

            assertThatThrownBy(() -> applianceService.getApplianceById(HOUSE_ID, APPLIANCE_ID, dummyUser))
                    .isInstanceOf(RuntimeException.class);

            verifyNoInteractions(applianceRepo);
        }
    }

    @Nested
    @DisplayName("getAppliancesByHouseId Tests")
    class GetAppliancesByHouseIdTests {
        @Test
        @DisplayName("Success - Should return list of appliances")
        void success_ReturnsList() {
            when(authManager.authorize(HOUSE_ID, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
            when(applianceRepo.findAllByHouse(dummyHouse)).thenReturn(List.of(dummyAppliance));

            List<ApplianceResponse> results = applianceService.getAppliancesByHouseId(HOUSE_ID, dummyUser);

            assertThat(results).hasSize(1);
            assertThat(results.get(0).id()).isEqualTo(APPLIANCE_ID);
        }

        @Test
        @DisplayName("Success - Should return empty list when no appliances exist")
        void success_EmptyList() {
            when(authManager.authorize(HOUSE_ID, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
            when(applianceRepo.findAllByHouse(dummyHouse)).thenReturn(Collections.emptyList());

            List<ApplianceResponse> results = applianceService.getAppliancesByHouseId(HOUSE_ID, dummyUser);

            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateAppliance Tests")
    class UpdateApplianceTests {
        @Test
        @DisplayName("Success - Should update name and return response")
        void success() {
            UpdateApplianceRequest request = new UpdateApplianceRequest("Smart Dishwasher");
            when(authManager.authorize(HOUSE_ID, dummyUser, Role.RESIDENT)).thenReturn(dummyMembership);
            when(applianceRepo.findById(APPLIANCE_ID)).thenReturn(Optional.of(dummyAppliance));

            ApplianceResponse result = applianceService.updateAppliance(HOUSE_ID, APPLIANCE_ID, request, dummyUser);

            assertThat(dummyAppliance.getName()).isEqualTo("Smart Dishwasher");
            assertThat(result.name()).isEqualTo("Smart Dishwasher");
        }

        @Test
        @DisplayName("Failure - Unauthorized user cannot update")
        void unauthorized_ThrowsException() {
            UpdateApplianceRequest request = new UpdateApplianceRequest("New Name");
            when(authManager.authorize(anyLong(), any(), eq(Role.RESIDENT)))
                    .thenThrow(new RuntimeException("Access Denied"));

            assertThatThrownBy(() -> applianceService.updateAppliance(HOUSE_ID, APPLIANCE_ID, request, dummyUser))
                    .isInstanceOf(RuntimeException.class);

            verify(applianceRepo, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("deleteAppliance Tests")
    class DeleteApplianceTests {
        @Test
        @DisplayName("Success - Should delete existing appliance")
        void success() {
            when(authManager.authorize(HOUSE_ID, dummyUser, Role.RESIDENT)).thenReturn(dummyMembership);
            when(applianceRepo.findById(APPLIANCE_ID)).thenReturn(Optional.of(dummyAppliance));

            applianceService.deleteAppliance(HOUSE_ID, APPLIANCE_ID, dummyUser);

            verify(applianceRepo).delete(dummyAppliance);
        }

        @Test
        @DisplayName("Failure - Should throw ResourceNotFoundException if ID doesn't exist")
        void notFound_ThrowsException() {
            when(authManager.authorize(HOUSE_ID, dummyUser, Role.RESIDENT)).thenReturn(dummyMembership);
            when(applianceRepo.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applianceService.deleteAppliance(HOUSE_ID, 999L, dummyUser))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(applianceRepo, never()).delete(any());
        }
    }
}