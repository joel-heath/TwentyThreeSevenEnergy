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
import uk.ac.soton.comp2300.group42.energyserver.exception.ResourceNotFoundException;
import uk.ac.soton.comp2300.group42.energyserver.mapper.MetricMapper;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.HouseMembership;
import uk.ac.soton.comp2300.group42.energyserver.model.Metric;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.energyserver.repository.MetricRepository;
import uk.ac.soton.comp2300.group42.metric.MetricResponse;
import uk.ac.soton.comp2300.group42.metric.SaveMetricRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.ac.soton.comp2300.group42.energyserver.util.TestUtils.assignId;
import static uk.ac.soton.comp2300.group42.energyserver.util.TestUtils.verifySaveAndCapture;

@ExtendWith(MockitoExtension.class)
class MetricServiceTest {

    @Mock
    private MetricRepository metricRepo;

    @Mock
    private HouseAuthorizationManager authManager;

    @Spy
    private MetricMapper mapper = Mappers.getMapper(MetricMapper.class);

    @InjectMocks
    private MetricService metricService;

    private User dummyUser;
    private House dummyHouse;
    private HouseMembership dummyMembership;
    private Metric dummyMetric;

    @BeforeEach
    void setUp() {
        dummyUser = new User();
        ReflectionTestUtils.setField(dummyUser, "id", 1L);

        dummyHouse = new House();
        ReflectionTestUtils.setField(dummyHouse, "id", 10L);

        dummyMembership = new HouseMembership();
        dummyMembership.setUser(dummyUser);
        dummyMembership.setHouse(dummyHouse);
        dummyMembership.setRole(Role.RESIDENT);

        dummyMetric = new Metric();
        ReflectionTestUtils.setField(dummyMetric, "id", 100L);
        dummyMetric.setHouse(dummyHouse);
        dummyMetric.setDate(LocalDate.of(2023, 10, 1));
        dummyMetric.setEnergyUsed(15.5);
    }

    @Test
    void getMetricById_Success() {
        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(metricRepo.findById(100L)).thenReturn(Optional.of(dummyMetric));

        MetricResponse result = metricService.getMetricById(10L, 100L, dummyUser);

        assertThat(result).isNotNull();
        verify(mapper).toMetricResponse(dummyMetric);
    }

    @Test
    void getMetricById_NotFound_ThrowsException() {
        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(metricRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> metricService.getMetricById(10L, 999L, dummyUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Metric with ID 999 not found");

        verifyNoInteractions(mapper);
    }

    @Test
    void getMetricsByHouseId_Success() {
        when(authManager.authorize(10L, dummyUser, Role.GUEST)).thenReturn(dummyMembership);
        when(metricRepo.findAllByHouse(dummyHouse)).thenReturn(List.of(dummyMetric));

        List<MetricResponse> results = metricService.getMetricsByHouseId(10L, dummyUser);

        assertThat(results).hasSize(1);
        verify(mapper).toMetricResponse(dummyMetric);
    }

    @Test
    void saveMetric_Success() {
        LocalDate date = LocalDate.of(2025, 12, 25);
        SaveMetricRequest request = new SaveMetricRequest(20.0);

        when(authManager.authorize(10L, dummyUser, Role.RESIDENT)).thenReturn(dummyMembership);
        when(metricRepo.save(any(Metric.class))).thenAnswer(m -> assignId(m, 200L));

        MetricResponse result = metricService.saveMetric(10L, date, request, dummyUser);

        Metric savedMetric = verifySaveAndCapture(metricRepo, Metric.class);

        assertThat(savedMetric.getId()).isEqualTo(200L);
        assertThat(savedMetric.getHouse()).isEqualTo(dummyHouse);
        assertThat(savedMetric.getDate()).isEqualTo(date);
        assertThat(savedMetric.getEnergyUsed()).isEqualTo(20.0);

        assertThat(result.id()).isEqualTo(200L);
        assertThat(result.houseId()).isEqualTo(10L);
        assertThat(result.date()).isEqualTo(date);
        assertThat(result.energyUsed()).isEqualTo(20.0);
    }
}