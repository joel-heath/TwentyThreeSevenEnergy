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
import uk.ac.soton.comp2300.group42.appliance.ApplianceResponse;
import uk.ac.soton.comp2300.group42.appliance.CreateApplianceRequest;
import uk.ac.soton.comp2300.group42.appliance.UpdateApplianceRequest;
import uk.ac.soton.comp2300.group42.energyserver.security.filter.JwtAuthFilter;
import uk.ac.soton.comp2300.group42.energyserver.service.ApplianceService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApplianceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApplianceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ApplianceService applianceService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private final Long HOUSE_ID = 1L;
    private final Long APPLIANCE_ID = 100L;
    private final String BASE_URL = "/api/houses/{houseId}/appliances";

    @Test
    @DisplayName("POST / - Should create appliance and return 201 Created with Location header")
    @WithMockUser
    void createAppliance_ShouldReturn201() throws Exception {
        CreateApplianceRequest request = new CreateApplianceRequest("Washing Machine");
        ApplianceResponse response = new ApplianceResponse(APPLIANCE_ID, HOUSE_ID, "Washing Machine");

        when(applianceService.createAppliance(eq(HOUSE_ID), any(CreateApplianceRequest.class), any()))
                .thenReturn(response);

        mockMvc.perform(post(BASE_URL, HOUSE_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/appliances/" + APPLIANCE_ID)))
                .andExpect(jsonPath("$.id").value(APPLIANCE_ID))
                .andExpect(jsonPath("$.name").value("Washing Machine"));

        verify(applianceService).createAppliance(eq(HOUSE_ID), any(CreateApplianceRequest.class), any());
    }

    @Test
    @DisplayName("GET /{id} - Should return appliance details and 200 OK")
    @WithMockUser
    void getAppliance_ShouldReturn200() throws Exception {
        ApplianceResponse response = new ApplianceResponse(APPLIANCE_ID, HOUSE_ID, "Fridge");

        when(applianceService.getApplianceById(eq(HOUSE_ID), eq(APPLIANCE_ID), any()))
                .thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/{id}", HOUSE_ID, APPLIANCE_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(APPLIANCE_ID))
                .andExpect(jsonPath("$.name").value("Fridge"));

        verify(applianceService).getApplianceById(eq(HOUSE_ID), eq(APPLIANCE_ID), any());
    }

    @Test
    @DisplayName("GET / - Should return list of appliances and 200 OK")
    @WithMockUser
    void getAllAppliances_ShouldReturn200() throws Exception {
        List<ApplianceResponse> responses = List.of(
                new ApplianceResponse(100L, HOUSE_ID, "Fridge"),
                new ApplianceResponse(101L, HOUSE_ID, "TV")
        );

        when(applianceService.getAppliancesByHouseId(eq(HOUSE_ID), any()))
                .thenReturn(responses);

        mockMvc.perform(get(BASE_URL, HOUSE_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Fridge"))
                .andExpect(jsonPath("$[1].name").value("TV"));

        verify(applianceService).getAppliancesByHouseId(eq(HOUSE_ID), any());
    }

    @Test
    @DisplayName("PUT /{id} - Should update appliance and return 200 OK")
    @WithMockUser
    void updateAppliance_ShouldReturn200() throws Exception {
        UpdateApplianceRequest request = new UpdateApplianceRequest("Smart Fridge");
        ApplianceResponse response = new ApplianceResponse(APPLIANCE_ID, HOUSE_ID, "Smart Fridge");

        when(applianceService.updateAppliance(eq(HOUSE_ID), eq(APPLIANCE_ID), any(UpdateApplianceRequest.class), any()))
                .thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/{id}", HOUSE_ID, APPLIANCE_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Smart Fridge"));

        verify(applianceService).updateAppliance(eq(HOUSE_ID), eq(APPLIANCE_ID), any(UpdateApplianceRequest.class), any());
    }

    @Test
    @DisplayName("DELETE /{id} - Should delete appliance and return 204 No Content")
    @WithMockUser
    void deleteAppliance_ShouldReturn204() throws Exception {
        doNothing().when(applianceService).deleteAppliance(eq(HOUSE_ID), eq(APPLIANCE_ID), any());

        mockMvc.perform(delete(BASE_URL + "/{id}", HOUSE_ID, APPLIANCE_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(applianceService).deleteAppliance(eq(HOUSE_ID), eq(APPLIANCE_ID), any());
    }
}