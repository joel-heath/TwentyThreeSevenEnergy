package uk.ac.soton.comp2300.group42.energyserver.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import uk.ac.soton.comp2300.group42.preferences.Theme;

@Converter(autoApply = true)
public class ThemeConverter implements AttributeConverter<Theme, String> {

    @Override
    public String convertToDatabaseColumn(Theme theme) {
        if (theme == null)
            return null;

        return theme.getId();
    }

    @Override
    public Theme convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;

        return Theme.fromId(dbData);
    }
}