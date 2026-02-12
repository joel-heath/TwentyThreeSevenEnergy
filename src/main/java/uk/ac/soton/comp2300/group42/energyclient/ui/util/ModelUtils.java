package uk.ac.soton.comp2300.group42.energyclient.ui.util;

import java.util.Objects;
import java.util.function.Consumer;

public class ModelUtils {
    public static <T> void updateIfChanged(T modelValue, T dtoValue, Consumer<T> modelSetter) {
        if (!Objects.equals(modelValue, dtoValue))
            modelSetter.accept(dtoValue);
    }
}
