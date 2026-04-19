package uk.ac.soton.comp2300.group42.energyserver.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.energyserver.security.filter.JwtAuthFilter;
import uk.ac.soton.comp2300.group42.energyserver.service.MetricService;
import uk.ac.soton.comp2300.group42.metric.MetricResponse;
import uk.ac.soton.comp2300.group42.metric.SaveMetricRequest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MetricController.class)
@AutoConfigureMockMvc(addFilters = false)
class MetricControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MetricService metricService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private final Long HOUSE_ID = 1L;
    private final Long METRIC_ID = 100L;
    private final String BASE_URL = "/api/houses/{houseId}/metrics";

    @Test
    @DisplayName("GET /{id} - Should return metric details and 200 OK")
    @WithMockUser
    void getMetric_ShouldReturn200() throws Exception {
        LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        MetricResponse response = new MetricResponse(METRIC_ID, HOUSE_ID, today, 15.5, 100.0, EnergyCategory.ELECTRICITY);

        when(metricService.getMetricById(eq(HOUSE_ID), eq(METRIC_ID), any()))
                .thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/{id}", HOUSE_ID, METRIC_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(METRIC_ID))
                .andExpect(jsonPath("$.houseId").value(HOUSE_ID))
                .andExpect(jsonPath("$.energyUsed").value(15.5))
                .andExpect(jsonPath("$.dateTime").value(today.toString()));

        verify(metricService).getMetricById(eq(HOUSE_ID), eq(METRIC_ID), any());
    }

    @Test
    @DisplayName("GET / - Should return list of metrics and 200 OK")
    @WithMockUser
    void getAllMetrics_ShouldReturn200() throws Exception {
        LocalDateTime today = LocalDateTime.now();
        List<MetricResponse> responses = List.of(
                new MetricResponse(100L, HOUSE_ID, today.minusDays(1), 12.0, 100.0, EnergyCategory.OTHER),
                new MetricResponse(101L, HOUSE_ID, today, 15.5, 150.0, EnergyCategory.ELECTRICITY)
        );

        when(metricService.getMetricsByHouseId(eq(HOUSE_ID), any()))
                .thenReturn(responses);

        mockMvc.perform(get(BASE_URL, HOUSE_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].energyUsed").value(12.0))
                .andExpect(jsonPath("$[1].energyUsed").value(15.5));

        verify(metricService).getMetricsByHouseId(eq(HOUSE_ID), any());
    }

    @Test
    @DisplayName("GET / - Should return list of metrics by category and 200 OK")
    @WithMockUser
    void getAllMetricsByCategory_ShouldReturn200() throws Exception {
        LocalDateTime today = LocalDateTime.now();
        List<MetricResponse> responses = List.of(
                new MetricResponse(100L, HOUSE_ID, today.minusDays(1), 12.0, 120.0, EnergyCategory.OTHER),
                new MetricResponse(101L, HOUSE_ID, today, 15.5, 135.0, EnergyCategory.OTHER)
        );

        when(metricService.getMetricsByHouseAndCategory(eq(HOUSE_ID), eq(EnergyCategory.OTHER), any()))
                .thenReturn(responses);

        mockMvc.perform(get(BASE_URL, HOUSE_ID)
                        .param("category", "OTHER")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].energyUsed").value(12.0))
                .andExpect(jsonPath("$[1].energyUsed").value(15.5));

        verify(metricService).getMetricsByHouseAndCategory(eq(HOUSE_ID), eq(EnergyCategory.OTHER), any());
    }

    @Test
    @DisplayName("POST /seed-test-data - Should save metric and return 200 OK")
    @WithMockUser
    void seedData_ShouldReturn200() throws Exception {
        SaveMetricRequest request = new SaveMetricRequest(25.0, 200.0, EnergyCategory.OTHER);
        LocalDateTime today = LocalDateTime.now();
        MetricResponse response = new MetricResponse(METRIC_ID, HOUSE_ID, today, 25.0, 200.0, EnergyCategory.OTHER);

        when(metricService.saveMetric(eq(HOUSE_ID), any(LocalDateTime.class), any(SaveMetricRequest.class), any()))
                .thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/seed-test-data", HOUSE_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(METRIC_ID))
                .andExpect(jsonPath("$.energyUsed").value(25.0));

        verify(metricService).saveMetric(eq(HOUSE_ID), any(LocalDateTime.class), any(SaveMetricRequest.class), any());
    }
}