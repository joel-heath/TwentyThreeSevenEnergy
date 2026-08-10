package uk.ac.soton.comp2300.group42.energyclient.presentation.util;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StyleClassUtilsTest {

    @Test
    void setClass_whenEnabled_addsClassWithoutDuplicates() {
        Pane node = new Pane();

        StyleClassUtils.setClass(node, "active", true);
        StyleClassUtils.setClass(node, "active", true);

        assertTrue(node.getStyleClass().contains("active"));
        assertEquals(1, Collections.frequency(node.getStyleClass(), "active"));
    }

    @Test
    void setClass_whenDisabled_removesClass() {
        Pane node = new Pane();
        node.getStyleClass().add("active");

        StyleClassUtils.setClass(node, "active", false);

        assertFalse(node.getStyleClass().contains("active"));
    }

    @Test
    void setClass_withInvalidArguments_doesNothing() {
        Pane node = new Pane();
        node.getStyleClass().add("base");

        StyleClassUtils.setClass(null, "active", true);
        StyleClassUtils.setClass(node, null, true);
        StyleClassUtils.setClass(node, "", true);
        StyleClassUtils.setClass(node, "   ", true);

        assertEquals(1, node.getStyleClass().size());
        assertTrue(node.getStyleClass().contains("base"));
    }

    @Test
    void bindBooleanClass_appliesInitialStateAndRespondsToChanges() {
        Pane node = new Pane();
        SimpleBooleanProperty source = new SimpleBooleanProperty(false);

        StyleClassUtils.bindBooleanClass(node, source, "enabled");
        assertFalse(node.getStyleClass().contains("enabled"));

        source.set(true);
        assertTrue(node.getStyleClass().contains("enabled"));

        source.set(false);
        assertFalse(node.getStyleClass().contains("enabled"));
    }

    @Test
    void bindBooleanClass_withNullArguments_isNoOp() {
        Pane node = new Pane();
        SimpleBooleanProperty source = new SimpleBooleanProperty(true);

        assertDoesNotThrow(() -> StyleClassUtils.bindBooleanClass(null, source, "enabled"));
        assertDoesNotThrow(() -> StyleClassUtils.bindBooleanClass(node, null, "enabled"));
    }

    @Test
    void bindExclusiveClass_appliesAndSwitchesManagedClasses() {
        Pane node = new Pane();
        node.getStyleClass().add("persistent");
        SimpleStringProperty source = new SimpleStringProperty("cheap");

        StyleClassUtils.bindExclusiveClass(node, source, "cheap", "average", "expensive");
        assertTrue(node.getStyleClass().contains("cheap"));
        assertTrue(node.getStyleClass().contains("persistent"));
        assertFalse(node.getStyleClass().contains("average"));
        assertFalse(node.getStyleClass().contains("expensive"));

        source.set("expensive");
        assertFalse(node.getStyleClass().contains("cheap"));
        assertTrue(node.getStyleClass().contains("expensive"));

        source.set("unknown");
        assertFalse(node.getStyleClass().contains("expensive"));
        assertFalse(node.getStyleClass().contains("average"));
        assertFalse(node.getStyleClass().contains("cheap"));

        source.set("");
        assertFalse(node.getStyleClass().contains("cheap"));

        source.set(null);
        assertFalse(node.getStyleClass().contains("cheap"));
        assertTrue(node.getStyleClass().contains("persistent"));
    }

    @Test
    void bindExclusiveClass_withNullArguments_isNoOp() {
        Pane node = new Pane();
        SimpleStringProperty source = new SimpleStringProperty("cheap");

        assertDoesNotThrow(() -> StyleClassUtils.bindExclusiveClass(null, source, "cheap"));
        assertDoesNotThrow(() -> StyleClassUtils.bindExclusiveClass(node, null, "cheap"));
        assertDoesNotThrow(() -> StyleClassUtils.bindExclusiveClass(node, source, (String[]) null));
    }
}
