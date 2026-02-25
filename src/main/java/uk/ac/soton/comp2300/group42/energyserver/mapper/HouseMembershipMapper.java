package uk.ac.soton.comp2300.group42.energyserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.ac.soton.comp2300.group42.energyserver.model.HouseMembership;
import uk.ac.soton.comp2300.group42.house.HouseResponse;
import uk.ac.soton.comp2300.group42.housemate.HousemateResponse;

@Mapper(componentModel = "spring")
public interface HouseMembershipMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "house.id", target = "houseId")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.email", target = "email")
    HousemateResponse toHousemateResponse(HouseMembership membership);

    @Mapping(source = "house.id", target = "id")
    @Mapping(source = "house.address", target = "address")
    @Mapping(source = "house.timezone", target = "timezone")
    @Mapping(source = "houseNickname", target = "name")
    HouseResponse toHouseResponse(HouseMembership membership);
}