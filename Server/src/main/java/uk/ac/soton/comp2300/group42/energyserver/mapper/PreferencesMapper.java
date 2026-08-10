package uk.ac.soton.comp2300.group42.energyserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.ac.soton.comp2300.group42.energyserver.model.Preferences;
import uk.ac.soton.comp2300.group42.preferences.PreferencesResponse;

@Mapper(componentModel = "spring")
public interface PreferencesMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "activeHouse.id", target = "activeHouseId")
    @Mapping(source = "colorVision", target = "vision")
    PreferencesResponse toPreferencesResponse(Preferences preferences);
}