package uk.ac.soton.comp2300.group42.energyserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.HouseMembership;
import uk.ac.soton.comp2300.group42.house.HouseResponse;

@Mapper(componentModel = "spring")
public interface HouseMapper {

    @Mapping(source = "house.id", target = "id")
    HouseResponse toHouseResponse(House house, HouseMembership membership);
}