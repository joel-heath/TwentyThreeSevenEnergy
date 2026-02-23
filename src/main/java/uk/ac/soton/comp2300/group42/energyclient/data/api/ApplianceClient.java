package uk.ac.soton.comp2300.group42.energyclient.data.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.appliance.ApplianceResponse;
import uk.ac.soton.comp2300.group42.energyclient.data.AuthenticatedHttpClient;

import java.util.List;
import java.util.Optional;

@Singleton
public class ApplianceClient {

    private final AuthenticatedHttpClient httpClient;
    private final ObjectMapper mapper;

    @Inject
    public ApplianceClient(AuthenticatedHttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    public Optional<ApplianceResponse> findById(Long id) {
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

        return Optional.of(new ApplianceResponse(id, "Dishwasher"));
    }

    public List<ApplianceResponse> findAll(Long houseId) {
        return List.of(
            new ApplianceResponse(1L, "Dishwasher"),
            new ApplianceResponse(2L, "Washing Machine"),
            new ApplianceResponse(3L, "Tumble Dryer"),
            new ApplianceResponse(4L, "Oven"));
    }

    public ApplianceResponse save(ApplianceResponse appliance) {
        return appliance;
    }

    public void delete(Long applianceId) { }

}
