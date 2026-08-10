package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import org.mapstruct.Mapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Housemate;
import uk.ac.soton.comp2300.group42.house.CreateHouseRequest;
import uk.ac.soton.comp2300.group42.house.HouseResponse;
import uk.ac.soton.comp2300.group42.house.UpdateHouseRequest;
import uk.ac.soton.comp2300.group42.housemate.HousemateResponse;

@Mapper
public interface HouseMapper {
    House toHouse(HouseResponse response);
    Housemate toHousemate(HousemateResponse response);
    CreateHouseRequest toCreateHouseRequest(House house);
    UpdateHouseRequest toUpdateHouseRequest(House house);
}
