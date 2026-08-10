package uk.ac.soton.comp2300.group42.energyserver.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import uk.ac.soton.comp2300.group42.preferences.Mode;

@Converter(autoApply = true)
public class ModeConverter implements AttributeConverter<Mode, String> {

    @Override
    public String convertToDatabaseColumn(Mode mode) {
        if (mode == null)
            return null;

        return mode.getId();
    }

    @Override
    public Mode convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;

        return Mode.fromId(dbData);
    }
}