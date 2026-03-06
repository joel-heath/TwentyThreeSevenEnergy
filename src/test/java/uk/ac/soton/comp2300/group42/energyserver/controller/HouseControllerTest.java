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
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyserver.security.filter.JwtAuthFilter;
import uk.ac.soton.comp2300.group42.energyserver.service.HouseService;
import uk.ac.soton.comp2300.group42.house.CreateHouseRequest;
import uk.ac.soton.comp2300.group42.house.HouseResponse;
import uk.ac.soton.comp2300.group42.house.UpdateHouseRequest;
import uk.ac.soton.comp2300.group42.housemate.HousemateResponse;

import java.time.ZoneId;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HouseController.class)
@AutoConfigureMockMvc(addFilters = false)
class HouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HouseService houseService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private final Long HOUSE_ID = 1L;
    private final Long USER_ID = 100L;
    private final String BASE_URL = "/api/houses";
    private final ZoneId TIMEZONE = ZoneId.of("Europe/London");

    @Test
    @DisplayName("POST / - Should create house and return 201 Created with Location header")
    @WithMockUser
    void createHouse_ShouldReturn201() throws Exception {
        CreateHouseRequest request = new CreateHouseRequest("My Smart Home", "123 Energy St", TIMEZONE);
        HouseResponse response = new HouseResponse(HOUSE_ID, "My Smart Home", "123 Energy St", TIMEZONE, Role.OWNER);

        when(houseService.createHouse(any(CreateHouseRequest.class), any()))
                .thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/houses/" + HOUSE_ID)))
                .andExpect(jsonPath("$.id").value(HOUSE_ID))
                .andExpect(jsonPath("$.name").value("My Smart Home"))
                .andExpect(jsonPath("$.address").value("123 Energy St"));

        verify(houseService).createHouse(any(CreateHouseRequest.class), any());
    }

    @Test
    @DisplayName("GET /{houseId} - Should return house details and 200 OK")
    @WithMockUser
    void getHouse_ShouldReturn200() throws Exception {
        HouseResponse response = new HouseResponse(HOUSE_ID, "My Smart Home", "123 Energy St", TIMEZONE, Role.OWNER);

        when(houseService.getHouseById(eq(HOUSE_ID), any()))
                .thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/{houseId}", HOUSE_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(HOUSE_ID))
                .andExpect(jsonPath("$.name").value("My Smart Home"));

        verify(houseService).getHouseById(eq(HOUSE_ID), any());
    }

    @Test
    @DisplayName("PUT /{houseId} - Should update house and return 200 OK")
    @WithMockUser
    void updateHouse_ShouldReturn200() throws Exception {
        UpdateHouseRequest request = new UpdateHouseRequest("Updated Home", "456 Solar Ave", TIMEZONE);
        HouseResponse response = new HouseResponse(HOUSE_ID, "Updated Home", "456 Solar Ave", TIMEZONE, Role.OWNER);

        when(houseService.updateHouse(eq(HOUSE_ID), any(UpdateHouseRequest.class), any()))
                .thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/{houseId}", HOUSE_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(HOUSE_ID))
                .andExpect(jsonPath("$.name").value("Updated Home"))
                .andExpect(jsonPath("$.address").value("456 Solar Ave"));

        verify(houseService).updateHouse(eq(HOUSE_ID), any(UpdateHouseRequest.class), any());
    }

    @Test
    @DisplayName("DELETE /{houseId} - Should delete house and return 204 No Content")
    @WithMockUser
    void deleteHouse_ShouldReturn204() throws Exception {
        doNothing().when(houseService).deleteHouse(eq(HOUSE_ID), any());

        mockMvc.perform(delete(BASE_URL + "/{houseId}", HOUSE_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(houseService).deleteHouse(eq(HOUSE_ID), any());
    }

    @Test
    @DisplayName("GET /me - Should return user's houses and 200 OK")
    @WithMockUser
    void getMyHouses_ShouldReturn200() throws Exception {
        List<HouseResponse> responses = List.of(
                new HouseResponse(HOUSE_ID, "Main House", "123 Energy St", TIMEZONE, Role.OWNER),
                new HouseResponse(2L, "Vacation Home", "Beach Road", TIMEZONE, Role.OWNER)
        );

        when(houseService.getHousesByUser(any()))
                .thenReturn(responses);

        mockMvc.perform(get(BASE_URL + "/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Main House"))
                .andExpect(jsonPath("$[1].name").value("Vacation Home"));

        verify(houseService).getHousesByUser(any());
    }

    @Test
    @DisplayName("GET /{houseId}/me - Should return specific user housemate info and 200 OK")
    @WithMockUser
    void getMyHousemateInfo_ShouldReturn200() throws Exception {
        HousemateResponse response = new HousemateResponse(USER_ID, HOUSE_ID, "Alice", "alice@example.com", Role.OWNER);

        when(houseService.getUserAsHousemate(eq(HOUSE_ID), any()))
                .thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/{houseId}/me", HOUSE_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        verify(houseService).getUserAsHousemate(eq(HOUSE_ID), any());
    }

    @Test
    @DisplayName("GET /{houseId}/housemates - Should return all housemates and 200 OK")
    @WithMockUser
    void getHousemates_ShouldReturn200() throws Exception {
        List<HousemateResponse> responses = List.of(
                new HousemateResponse(USER_ID, HOUSE_ID, "Alice", "alice@example.com", Role.OWNER),
                new HousemateResponse(101L, HOUSE_ID, "Bob", "bob@example.com", Role.OWNER)
        );

        when(houseService.getHousemates(eq(HOUSE_ID), any()))
                .thenReturn(responses);

        mockMvc.perform(get(BASE_URL + "/{houseId}/housemates", HOUSE_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].name").value("Bob"));

        verify(houseService).getHousemates(eq(HOUSE_ID), any());
    }
}