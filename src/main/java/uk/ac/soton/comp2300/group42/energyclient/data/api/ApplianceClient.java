package uk.ac.soton.comp2300.group42.energyclient.data.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.soton.comp2300.group42.energyclient.data.AuthenticatedHttpClient;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ApplianceDTO;

import java.util.List;
import java.util.Optional;

public class ApplianceClient {

    private final AuthenticatedHttpClient httpClient;
    private final ObjectMapper mapper;

    public ApplianceClient(AuthenticatedHttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    public Optional<ApplianceDTO> findById(Long id) {
        /* Actual implementation will look like this:
        try {
            var response = httpClient.get("appliance/" + id);
            if (response.statusCode() != 200)
                return Optional.empty();

            var json = response.body();
            var appliance = mapper.readValue(json, ApplianceDTO.class);
            return Optional.of(appliance);
        }
        catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
        */

        return Optional.of(new ApplianceDTO(id, "Dishwasher"));
    }

    public List<ApplianceDTO> findAll(Long houseId) {
        return List.of(
            new ApplianceDTO(1L, "Dishwasher"),
            new ApplianceDTO(2L, "Washing Machine"),
            new ApplianceDTO(3L, "Tumble Dryer"),
            new ApplianceDTO(4L, "Oven"));
    }

    public ApplianceDTO save(ApplianceDTO appliance) {
        return appliance;
    }

    public void delete(ApplianceDTO appliance) { }

}
