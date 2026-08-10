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
import uk.ac.soton.comp2300.group42.activation.ActivationResponse;
import uk.ac.soton.comp2300.group42.activation.CreateActivationRequest;
import uk.ac.soton.comp2300.group42.activation.UpdateActivationRequest;
import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.energyserver.security.filter.JwtAuthFilter;
import uk.ac.soton.comp2300.group42.energyserver.service.ActivationService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActivationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ActivationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ActivationService activationService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private final Long HOUSE_ID = 1L;
    private final Long APPLIANCE_ID = 200L;
    private final Long ACTIVATION_ID = 100L;
    private final String BASE_URL = "/api/houses/{houseId}/activations";

    private final LocalTime TIME = LocalTime.of(14, 30, 0); // "14:30:00"
    private final LocalDate DATE = LocalDate.of(2026, 3, 6); // "2026-03-06"

    @Test
    @DisplayName("POST / - Should create activation and return 201 Created with all fields")
    @WithMockUser
    void createActivation_ShouldReturn201() throws Exception {
        CreateActivationRequest request = new CreateActivationRequest(
                APPLIANCE_ID, HOUSE_ID, ActivationType.RECURRING, TIME, null,
                true, false, true, false, true, false, false
        );
        
        ActivationResponse response = new ActivationResponse(
                ACTIVATION_ID, APPLIANCE_ID, HOUSE_ID, ActivationType.RECURRING, TIME, null,
                true, false, true, false, true, false, false
        );

        when(activationService.createActivation(eq(HOUSE_ID), any(CreateActivationRequest.class), any()))
                .thenReturn(response);

        mockMvc.perform(post(BASE_URL, HOUSE_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/activations/" + ACTIVATION_ID)))
                .andExpect(jsonPath("$.id").value(ACTIVATION_ID))
                .andExpect(jsonPath("$.applianceId").value(APPLIANCE_ID))
                .andExpect(jsonPath("$.houseId").value(HOUSE_ID))
                .andExpect(jsonPath("$.type").value("recurring"))
                .andExpect(jsonPath("$.activationTime").value("14:30:00"))
                .andExpect(jsonPath("$.activationDate").value(nullValue()))
                .andExpect(jsonPath("$.recursMonday").value(true))
                .andExpect(jsonPath("$.recursTuesday").value(false))
                .andExpect(jsonPath("$.recursWednesday").value(true))
                .andExpect(jsonPath("$.recursThursday").value(false))
                .andExpect(jsonPath("$.recursFriday").value(true))
                .andExpect(jsonPath("$.recursSaturday").value(false))
                .andExpect(jsonPath("$.recursSunday").value(false));

        verify(activationService).createActivation(eq(HOUSE_ID), any(CreateActivationRequest.class), any());
    }

    @Test
    @DisplayName("GET /{id} - Should return non-recurring activation details with all fields")
    @WithMockUser
    void getActivation_ShouldReturn200() throws Exception {
        ActivationResponse response = new ActivationResponse(
                ACTIVATION_ID, APPLIANCE_ID, HOUSE_ID, ActivationType.NON_RECURRING, TIME, DATE,
                null, null, null, null, null, null, null
        );

        when(activationService.getActivationById(eq(HOUSE_ID), eq(ACTIVATION_ID), any()))
                .thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/{id}", HOUSE_ID, ACTIVATION_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ACTIVATION_ID))
                .andExpect(jsonPath("$.applianceId").value(APPLIANCE_ID))
                .andExpect(jsonPath("$.houseId").value(HOUSE_ID))
                .andExpect(jsonPath("$.type").value("non_recurring")) 
                .andExpect(jsonPath("$.activationTime").value("14:30:00"))
                .andExpect(jsonPath("$.activationDate").value("2026-03-06"))
                .andExpect(jsonPath("$.recursMonday").value(nullValue()))
                .andExpect(jsonPath("$.recursTuesday").value(nullValue()))
                .andExpect(jsonPath("$.recursWednesday").value(nullValue()))
                .andExpect(jsonPath("$.recursThursday").value(nullValue()))
                .andExpect(jsonPath("$.recursFriday").value(nullValue()))
                .andExpect(jsonPath("$.recursSaturday").value(nullValue()))
                .andExpect(jsonPath("$.recursSunday").value(nullValue()));

        verify(activationService).getActivationById(eq(HOUSE_ID), eq(ACTIVATION_ID), any());
    }

    @Test
    @DisplayName("GET / - Should return list of activations and 200 OK")
    @WithMockUser
    void getAllActivations_ShouldReturn200() throws Exception {
        List<ActivationResponse> responses = List.of(
                new ActivationResponse(
                        ACTIVATION_ID, APPLIANCE_ID, HOUSE_ID, ActivationType.RECURRING, TIME, null,
                        true, false, false, false, false, false, false
                )
        );

        when(activationService.getActivationsByHouseId(eq(HOUSE_ID), any()))
                .thenReturn(responses);

        mockMvc.perform(get(BASE_URL, HOUSE_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(ACTIVATION_ID))
                .andExpect(jsonPath("$[0].applianceId").value(APPLIANCE_ID))
                .andExpect(jsonPath("$[0].houseId").value(HOUSE_ID))
                .andExpect(jsonPath("$[0].type").value("recurring"))
                .andExpect(jsonPath("$[0].activationTime").value("14:30:00"))
                .andExpect(jsonPath("$[0].activationDate").value(nullValue()))
                .andExpect(jsonPath("$[0].recursMonday").value(true))
                .andExpect(jsonPath("$[0].recursTuesday").value(false))
                .andExpect(jsonPath("$[0].recursWednesday").value(false))
                .andExpect(jsonPath("$[0].recursThursday").value(false))
                .andExpect(jsonPath("$[0].recursFriday").value(false))
                .andExpect(jsonPath("$[0].recursSaturday").value(false))
                .andExpect(jsonPath("$[0].recursSunday").value(false));

        verify(activationService).getActivationsByHouseId(eq(HOUSE_ID), any());
    }

    @Test
    @DisplayName("PUT /{id} - Should update activation and return 200 OK with all fields")
    @WithMockUser
    void updateActivation_ShouldReturn200() throws Exception {
        UpdateActivationRequest request = new UpdateActivationRequest(
                APPLIANCE_ID, HOUSE_ID, ActivationType.RECURRING, TIME, null,
                false, true, false, true, false, true, false
        );
        
        ActivationResponse response = new ActivationResponse(
                ACTIVATION_ID, APPLIANCE_ID, HOUSE_ID, ActivationType.RECURRING, TIME, null,
                false, true, false, true, false, true, false
        );

        when(activationService.updateActivation(eq(HOUSE_ID), eq(ACTIVATION_ID), any(UpdateActivationRequest.class), any()))
                .thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/{id}", HOUSE_ID, ACTIVATION_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ACTIVATION_ID))
                .andExpect(jsonPath("$.applianceId").value(APPLIANCE_ID))
                .andExpect(jsonPath("$.houseId").value(HOUSE_ID))
                .andExpect(jsonPath("$.type").value("recurring"))
                .andExpect(jsonPath("$.activationTime").value("14:30:00"))
                .andExpect(jsonPath("$.activationDate").value(nullValue()))
                .andExpect(jsonPath("$.recursMonday").value(false))
                .andExpect(jsonPath("$.recursTuesday").value(true))
                .andExpect(jsonPath("$.recursWednesday").value(false))
                .andExpect(jsonPath("$.recursThursday").value(true))
                .andExpect(jsonPath("$.recursFriday").value(false))
                .andExpect(jsonPath("$.recursSaturday").value(true))
                .andExpect(jsonPath("$.recursSunday").value(false));

        verify(activationService).updateActivation(eq(HOUSE_ID), eq(ACTIVATION_ID), any(UpdateActivationRequest.class), any());
    }

    @Test
    @DisplayName("DELETE /{id} - Should delete activation and return 204 No Content")
    @WithMockUser
    void deleteActivation_ShouldReturn204() throws Exception {
        doNothing().when(activationService).deleteActivation(eq(HOUSE_ID), eq(ACTIVATION_ID), any());

        mockMvc.perform(delete(BASE_URL + "/{id}", HOUSE_ID, ACTIVATION_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(activationService).deleteActivation(eq(HOUSE_ID), eq(ACTIVATION_ID), any());
    }
}