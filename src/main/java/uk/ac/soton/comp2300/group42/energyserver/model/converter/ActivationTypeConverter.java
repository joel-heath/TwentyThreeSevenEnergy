package uk.ac.soton.comp2300.group42.energyserver.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import uk.ac.soton.comp2300.group42.activation.ActivationType;

@Converter(autoApply = true)
public class ActivationTypeConverter implements AttributeConverter<ActivationType, String> {

    @Override
    public String convertToDatabaseColumn(ActivationType activationType) {
        if (activationType == null)
            return null;

        return activationType.getId();
    }

    @Override
    public ActivationType convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;

        return ActivationType.fromId(dbData);
    }
}