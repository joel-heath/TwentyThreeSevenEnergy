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
import uk.ac.soton.comp2300.group42.energyserver.mapper.HouseMembershipMapper;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.HouseMembership;
import uk.ac.soton.comp2300.group42.energyserver.model.Preferences;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseMembershipRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.HouseRepository;
import uk.ac.soton.comp2300.group42.energyserver.repository.PreferencesRepository;
import uk.ac.soton.comp2300.group42.house.CreateHouseRequest;
import uk.ac.soton.comp2300.group42.house.HouseResponse;
import uk.ac.soton.comp2300.group42.house.UpdateHouseRequest;
import uk.ac.soton.comp2300.group42.housemate.HousemateResponse;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.ac.soton.comp2300.group42.energyserver.util.TestUtils.assignId;
import static uk.ac.soton.comp2300.group42.energyserver.util.TestUtils.verifySaveAndCapture;

@ExtendWith(MockitoExtension.class)
class HouseServiceTest {

    @Mock
    private HouseRepository houseRepo;

    @Mock
    private HouseMembershipRepository membershipRepo;

    @Mock
    private PreferencesRepository preferencesRepo;

    @Mock
    private HouseAuthorizationManager authManager;

    @Spy
    private HouseMembershipMapper mapper = Mappers.getMapper(HouseMembershipMapper.class);

    @InjectMocks
    private HouseService houseService;

    private User dummyUser;
    private House dummyHouse;
    private HouseMembership dummyMembership;
    private Preferences dummyPreferences;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);

        dummyHouse = new House();
        ReflectionTestUtils.setField(dummyHouse, "id", 10L);
        dummyHouse.setAddress("123 Test Ave");
        dummyHouse.setTimezone(ZoneId.of("Europe/London"));

        dummyMembership = new HouseMembership();
        ReflectionTestUtils.setField(dummyMembership, "id", 100L);
        dummyMembership.setUser(dummyUser);
        dummyMembership.setHouse(dummyHouse);
        dummyMembership.setRole(Role.OWNER);
        dummyMembership.setHouseNickname("My House");

        dummyPreferences = new Preferences();
        ReflectionTestUtils.setField(dummyPreferences, "id", 1000L);
        dummyPreferences.setUser(dummyUser);
        dummyPreferences.setActiveHouse(dummyHouse);
    }

    @Test
    void createHouse_Success() {
        CreateHouseRequest request = new CreateHouseRequest("Beach House", "456 Sand Rd", ZoneId.of("Europe/London"));

        when(houseRepo.save(any(House.class))).thenAnswer(house -> assignId(house, 20L));
        when(membershipRepo.save(any(HouseMembership.class))).thenAnswer(membership -> assignId(membership, 200L));

        HouseResponse result = houseService.createHouse(request, dummyUser);

        House house = verifySaveAndCapture(houseRepo, House.class);
        HouseMembership membership = verifySaveAndCapture(membershipRepo, HouseMembership.class);

        assertThat(house.getId()).isEqualTo(20L);
        assertThat(house.getAddress()).isEqualTo("456 Sand Rd");
        assertThat(house.getTimezone()).isEqualTo(ZoneId.of("Europe/London"));

        assertThat(membership.getUser()).isEqualTo(dummyUser);
        assertThat(membership.getHouse().getId()).isEqualTo(20L);
        assertThat(membership.getRole()).isEqualTo(Role.OWNER);
        assertThat(membership.getHouseNickname()).isEqualTo("Beach House");

        assertThat(result.id()).isEqualTo(20L);
        assertThat(result.address()).isEqualTo("456 Sand Rd");
        assertThat(result.timezone()).isEqualTo(ZoneId.of("Europe/London"));
        assertThat(result.name()).isEqualTo("Beach House");
        assertThat(result.role()).isEqualTo(Role.OWNER);
    }

    @Test
    void getHouseById_Success() {
        when(authManager.authorize(10L, dummyUser, Role.OWNER)).thenReturn(dummyMembership);

        HouseResponse result = houseService.getHouseById(10L, dummyUser);

        assertThat(result).isNotNull();
        verify(mapper).toHouseResponse(dummyMembership);
    }

    @Test
    void updateHouse_AsOwner_UpdatesAllFields() {
        UpdateHouseRequest request = new UpdateHouseRequest("Updated Name", "Updated Address", ZoneId.of("UTC"));
        when(authManager.authorize(10L, dummyUser, Role.OWNER)).thenReturn(dummyMembership);

        houseService.updateHouse(10L, request, dummyUser);

        assertThat(dummyHouse.getAddress()).isEqualTo("Updated Address");
        assertThat(dummyHouse.getTimezone()).isEqualTo(ZoneId.of("UTC"));
        assertThat(dummyMembership.getHouseNickname()).isEqualTo("Updated Name");
    }

    @Test
    void updateHouse_NotOwner_UpdatesOnlyNickname() {
        dummyMembership.setRole(Role.GUEST);
        UpdateHouseRequest request = new UpdateHouseRequest("Updated Name", "Updated Address", ZoneId.of("UTC"));
        when(authManager.authorize(10L, dummyUser, Role.OWNER)).thenReturn(dummyMembership);

        houseService.updateHouse(10L, request, dummyUser);

        assertThat(dummyHouse.getAddress()).isEqualTo("123 Test Ave");
        assertThat(dummyHouse.getTimezone()).isEqualTo(ZoneId.of("Europe/London"));
        assertThat(dummyMembership.getHouseNickname()).isEqualTo("Updated Name");
    }

    @Test
    void deleteHouse_UserHasOtherHouses_SwitchesActiveHouse() {
        House otherHouse = new House();
        ReflectionTestUtils.setField(otherHouse, "id", 20L);
        
        HouseMembership otherMembership = new HouseMembership();
        otherMembership.setHouse(otherHouse);

        when(authManager.authorize(10L, dummyUser, Role.OWNER)).thenReturn(dummyMembership);
        when(preferencesRepo.findByActiveHouse(dummyHouse)).thenReturn(List.of(dummyPreferences));
        when(membershipRepo.findByUserAndHouseNot(dummyUser, dummyHouse)).thenReturn(List.of(otherMembership));

        houseService.deleteHouse(10L, dummyUser);

        assertThat(dummyPreferences.getActiveHouse()).isEqualTo(otherHouse);
        verify(houseRepo).delete(dummyHouse);
        verify(houseRepo, never()).save(any());
    }

    @Test
    void deleteHouse_UserHasNoOtherHouses_CreatesDefaultHouse() {
        when(authManager.authorize(10L, dummyUser, Role.OWNER)).thenReturn(dummyMembership);
        when(preferencesRepo.findByActiveHouse(dummyHouse)).thenReturn(List.of(dummyPreferences));
        when(membershipRepo.findByUserAndHouseNot(dummyUser, dummyHouse)).thenReturn(Collections.emptyList());
        
        when(houseRepo.save(any(House.class))).thenAnswer(house -> assignId(house, 20L));
        when(membershipRepo.save(any(HouseMembership.class))).thenAnswer(membership -> assignId(membership, 200L));

        houseService.deleteHouse(10L, dummyUser);

        House house = verifySaveAndCapture(houseRepo, House.class);
        HouseMembership membership = verifySaveAndCapture(membershipRepo, HouseMembership.class);

        assertThat(house.getAddress()).isEqualTo("No Address Set");
        assertThat(membership.getHouseNickname()).isEqualTo("Default House");
        assertThat(membership.getHouse()).isEqualTo(house);
        assertThat(dummyPreferences.getActiveHouse()).isEqualTo(house);
        verify(houseRepo).delete(dummyHouse);
    }

    @Test
    void getHousesByUser_Success() {
        when(membershipRepo.findByUser(dummyUser)).thenReturn(List.of(dummyMembership));

        List<HouseResponse> results = houseService.getHousesByUser(dummyUser);

        assertThat(results).hasSize(1);
        verify(mapper).toHouseResponse(dummyMembership);
    }

    @Test
    void getUserAsHousemate_Success() {
        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);

        HousemateResponse result = houseService.getUserAsHousemate(10L, dummyUser);

        assertThat(result).isNotNull();
        verify(mapper).toHousemateResponse(dummyMembership);
    }

    @Test
    void getHousemates_Success() {
        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(membershipRepo.findByHouse(dummyHouse)).thenReturn(List.of(dummyMembership));

        List<HousemateResponse> results = houseService.getHousemates(10L, dummyUser);

        assertThat(results).hasSize(1);
        verify(mapper).toHousemateResponse(dummyMembership);
    }
}