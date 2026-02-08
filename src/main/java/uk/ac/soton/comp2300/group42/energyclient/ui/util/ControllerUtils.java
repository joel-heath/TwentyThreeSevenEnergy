package uk.ac.soton.comp2300.group42.energyclient.ui.util;

import javafx.util.StringConverter;
import java.util.function.Function;

public class ControllerUtils {

    public static  <T> StringConverter<T> createConverter(Function<T, String> nameExtractor) {
        return new StringConverter<>() {
            @Override
            public String toString(T object) {
                return object == null ? "" : nameExtractor.apply(object);
            }

            @Override
            public T fromString(String string) {
                return null; // Not needed for non-editable ComboBoxes
            }
        };
    }
}
