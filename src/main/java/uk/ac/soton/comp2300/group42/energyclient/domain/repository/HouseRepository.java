package uk.ac.soton.comp2300.group42.energyclient.domain.repository;

import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;

import java.util.List;

public interface HouseRepository {
    House add();
    House add(House house);
    House get(Long houseId);
    List<House> getCurrentUserHouses();
    House update(House house);
    void delete(Long houseId);
    Housemate getCurrentUserAsHousemate(Long houseId);
    List<Housemate> getHousemates(Long houseId);
}
