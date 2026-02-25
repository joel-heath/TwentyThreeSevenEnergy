package uk.ac.soton.comp2300.group42.energyclient.ui.util;

import java.util.Objects;
import java.util.function.Consumer;

public class ModelUtils {
    public static <T> void updateIfChanged(T viewModelValue, T domainModelValue, Consumer<T> viewModelSetter) {
        if (!Objects.equals(viewModelValue, domainModelValue))
            viewModelSetter.accept(domainModelValue);
    }

    public static <ID, T> void updateIfChanged(ID viewModelId, ID domainModelId, T domainModelValue, Consumer<T> viewModelSetter) {
        if (!Objects.equals(viewModelId, domainModelId))
            viewModelSetter.accept(domainModelValue);
    }
}
