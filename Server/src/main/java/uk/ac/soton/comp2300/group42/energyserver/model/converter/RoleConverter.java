package uk.ac.soton.comp2300.group42.energyserver.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import uk.ac.soton.comp2300.group42.common.Role;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null)
            return null;

        return role.getId();
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;

        return Role.fromId(dbData);
    }
}