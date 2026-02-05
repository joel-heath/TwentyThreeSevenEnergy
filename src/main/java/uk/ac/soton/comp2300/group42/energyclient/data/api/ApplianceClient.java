package uk.ac.soton.comp2300.group42.energyclient.data.api;

import uk.ac.soton.comp2300.group42.energyclient.data.dto.ApplianceDTO;
import java.util.List;
import java.util.Optional;

// TODO: In Sprint 2, use a HttpClient to call the server
// Currently contains hard-coded testing data

public class ApplianceClient {

    public Optional<ApplianceDTO> findById(int id) {
        return Optional.of(new ApplianceDTO(id, "Dishwasher"));
    }

    public List<ApplianceDTO> findAll() {
        return List.of(
            new ApplianceDTO(1, "Dishwasher"),
            new ApplianceDTO(2, "Washing Machine"),
            new ApplianceDTO(3, "Tumble Dryer"),
            new ApplianceDTO(4, "Oven"));
    }

    public ApplianceDTO save(ApplianceDTO appliance) {
        return appliance;
    }

    public void delete(ApplianceDTO appliance) { }

}
