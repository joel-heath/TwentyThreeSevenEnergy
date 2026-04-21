package uk.ac.soton.comp2300.group42.energyclient.presentation.util;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

import java.util.Set;

public final class StyleClassUtils {

    private StyleClassUtils() {}

    public static void setClass(Node node, String styleClass, boolean enabled) {
        if (node == null || styleClass == null || styleClass.isBlank()) {
            return;
        }

        var classes = node.getStyleClass();
        if (enabled) {
            if (!classes.contains(styleClass)) {
                classes.add(styleClass);
            }
        } else {
            classes.remove(styleClass);
        }
    }

    public static void bindBooleanClass(Node node, ObservableValue<Boolean> source, String styleClass) {
        if (node == null || source == null) {
            return;
        }

        setClass(node, styleClass, Boolean.TRUE.equals(source.getValue()));
        source.subscribe(value -> setClass(node, styleClass, Boolean.TRUE.equals(value)));
    }

    public static void bindExclusiveClass(Node node, ObservableValue<String> source, String... managedClasses) {
        if (node == null || source == null || managedClasses == null) {
            return;
        }

        Set<String> managed = Set.of(managedClasses);

        Runnable apply = () -> {
            String activeClass = source.getValue();
            var classes = node.getStyleClass();
            classes.removeIf(managed::contains);
            if (activeClass != null && !activeClass.isBlank() && managed.contains(activeClass)) {
                classes.add(activeClass);
            }
        };

        apply.run();
        source.subscribe(value -> apply.run());
    }
}
