package uk.ac.soton.comp2300.group42.energyserver.mapper;

import org.mapstruct.Mapper;

import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.user.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(User user);
}