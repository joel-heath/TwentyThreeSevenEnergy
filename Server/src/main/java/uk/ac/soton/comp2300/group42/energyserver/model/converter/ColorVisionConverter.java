package uk.ac.soton.comp2300.group42.energyserver.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;

@Converter(autoApply = true)
public class ColorVisionConverter implements AttributeConverter<ColorVision, String> {

    @Override
    public String convertToDatabaseColumn(ColorVision vision) {
        if (vision == null)
            return null;

        return vision.getId();
    }

    @Override
    public ColorVision convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;

        return ColorVision.fromId(dbData);
    }
}