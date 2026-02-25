package uk.ac.soton.comp2300.group42.energyclient.data.mapper;

import org.mapstruct.Mapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.User;
import uk.ac.soton.comp2300.group42.preferences.PreferencesResponse;
import uk.ac.soton.comp2300.group42.user.UserResponse;

@Mapper
public interface UserMapper {
    User toUser(UserResponse response);
    Preferences toPreferences(PreferencesResponse response);
}
