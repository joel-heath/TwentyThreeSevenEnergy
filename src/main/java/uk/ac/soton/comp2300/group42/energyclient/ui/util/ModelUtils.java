package uk.ac.soton.comp2300.group42.energyclient.ui.util;

import java.util.function.Consumer;

public class ModelUtils {
    public static <T> void updateIfChanged(T modelValue, T dtoValue, Consumer<T> modelSetter) {
        if (!modelValue.equals(dtoValue)) {
            modelSetter.accept(modelValue);
        }
    }
}
