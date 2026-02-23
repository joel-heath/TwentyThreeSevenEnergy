package uk.ac.soton.comp2300.group42.energyclient.domain.repository;

import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;

import java.util.List;
import java.util.Optional;

public interface HouseRepository {
    List<House> findHousesForCurrentUser();
    Optional<House> findHouseById(Long houseId);
    List<Housemate> findAllByHouseId(Long houseId);
    Optional<Housemate> findCurrentUserByHouseId(Long houseId);
    House createDefaultHouse();
}
