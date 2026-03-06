package uk.ac.soton.comp2300.group42.energyclient.presentation.util;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;

import static org.junit.jupiter.api.Assertions.*;

class ColorVisionManagerTest {

    @BeforeEach
    void setUp() {
        ColorVisionManager.unbind();
        ColorVisionManager.setVision(ColorVision.TYPICAL);
    }

    @AfterEach
    void tearDown() {
        ColorVisionManager.unbind();
        ColorVisionManager.setVision(ColorVision.TYPICAL);
    }

    @Test
    void bindUpdatesVisionAndListensToChanges() {
        SimpleObjectProperty<ColorVision> source = new SimpleObjectProperty<>(ColorVision.PROTAN);

        ColorVisionManager.bind(source);
        assertEquals(ColorVision.PROTAN, ColorVisionManager.getVision());

        source.set(ColorVision.TRITAN);
        assertEquals(ColorVision.TRITAN, ColorVisionManager.getVision());
    }

    @Test
    void bindThrowsOnNullSource() {
        assertThrows(NullPointerException.class, () -> ColorVisionManager.bind(null));
    }

    @Test
    void unbindStopsListeningToChanges() {
        SimpleObjectProperty<ColorVision> source = new SimpleObjectProperty<>(ColorVision.PROTAN);
        ColorVisionManager.bind(source);

        ColorVisionManager.unbind();
        source.set(ColorVision.TRITAN);

        assertEquals(ColorVision.PROTAN, ColorVisionManager.getVision());
    }

    @Test
    void setVisionUpdatesActiveVision() {
        ColorVisionManager.setVision(ColorVision.ACHROMA);
        assertEquals(ColorVision.ACHROMA, ColorVisionManager.getVision());
        assertEquals(ColorVision.ACHROMA, ColorVisionManager.visionProperty().get());
    }

    @Test
    void setVisionToNullDefaultsToTypical() {
        ColorVisionManager.setVision(ColorVision.ACHROMA);
        ColorVisionManager.setVision(null);

        assertEquals(ColorVision.TYPICAL, ColorVisionManager.getVision());
    }

    @Test
    void getGradientReturnsValidPaint() {
        Paint gradient = ColorVisionManager.getGradient();
        assertNotNull(gradient);
    }

    @Test
    void getGradientForNullVisionDefaultsToTypical() {
        Paint defaultGradient = ColorVisionManager.getGradientFor(ColorVision.TYPICAL);
        Paint nullVisionGradient = ColorVisionManager.getGradientFor(null);

        assertEquals(defaultGradient, nullVisionGradient);
    }

    @Test
    void getColorReturnsCorrectMappedColor() {
        ColorVisionManager.setVision(ColorVision.TYPICAL);
        Color expectedTypicalColor = Color.web("#D4EDDA");

        assertEquals(expectedTypicalColor, ColorVisionManager.getColor(ColorVisionManager.ColorRole.STATUS_CHEAP));

        Color expectedProtanColor = Color.web("#D9ECFA");
        assertEquals(expectedProtanColor, ColorVisionManager.getColor(ColorVision.PROTAN, ColorVisionManager.ColorRole.STATUS_CHEAP));
    }

    @Test
    void getColorThrowsOnNullRole() {
        assertThrows(NullPointerException.class, () -> ColorVisionManager.getColor(null));
        assertThrows(NullPointerException.class, () -> ColorVisionManager.getColor(ColorVision.TYPICAL, null));
    }

    @Test
    void getWebColorReturnsCorrectHexFormat() {
        ColorVisionManager.setVision(ColorVision.TYPICAL);

        String webColor = ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.STATUS_CHEAP);
        assertEquals("#D4EDDA", webColor);

        String protanWebColor = ColorVisionManager.getWebColor(ColorVision.PROTAN, ColorVisionManager.ColorRole.STATUS_CHEAP);
        assertEquals("#D9ECFA", protanWebColor);
    }

    @Test
    void palettesAreSharedBetweenProtanAndDeuteran() {
        Color protanColor = ColorVisionManager.getColor(ColorVision.PROTAN, ColorVisionManager.ColorRole.STATUS_AVERAGE);
        Color deuteranColor = ColorVisionManager.getColor(ColorVision.DEUTERAN, ColorVisionManager.ColorRole.STATUS_AVERAGE);

        assertEquals(protanColor, deuteranColor);
    }
}