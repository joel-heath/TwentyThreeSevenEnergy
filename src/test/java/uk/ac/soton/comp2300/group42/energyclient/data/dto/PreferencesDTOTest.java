package uk.ac.soton.comp2300.group42.energyclient.data.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Mode;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Theme;

import static org.junit.jupiter.api.Assertions.*;

public class PreferencesDTOTest {
    PreferencesDTO defaults;
    PreferencesDTO custom;

    @BeforeEach void setUp() {
        defaults = new PreferencesDTO();
        custom = new PreferencesDTO(
                true,
                ColorVision.DEUTERAN,
                Theme.LIGHT_CONTRAST,
                Mode.ADVANCED,
                true,
                5.2);
    }

    @Test void testGetters() {
        assertTrue(custom.getLargeFont());
        assertEquals(ColorVision.DEUTERAN, custom.getVision());
        assertEquals(Theme.LIGHT_CONTRAST, custom.getTheme());
        assertEquals(Mode.ADVANCED, custom.getMode());
        assertTrue(custom.getShareLocation());
        assertEquals(5.2, custom.getEnergyGoal());
    }

    @Test void testSetters() {
        defaults.setLargeFont(true);
        defaults.setVision(ColorVision.ACHROMA);
        defaults.setTheme(Theme.DARK);
        defaults.setMode(Mode.ADVANCED);
        defaults.setShareLocation(true);
        defaults.setEnergyGoal(8.7);

        assertTrue(defaults.getLargeFont());
        assertEquals(ColorVision.ACHROMA, defaults.getVision());
        assertEquals(Theme.DARK, defaults.getTheme());
        assertEquals(Mode.ADVANCED, defaults.getMode());
        assertTrue(defaults.getShareLocation());
        assertEquals(8.7, defaults.getEnergyGoal());
    }

    @Test void testDefault() {
        assertFalse(defaults.getLargeFont());
        assertEquals(ColorVision.TYPICAL, defaults.getVision());
        assertEquals(Theme.LIGHT, defaults.getTheme());
        assertEquals(Mode.SIMPLE, defaults.getMode());
        assertFalse(defaults.getShareLocation());
        assertEquals(1, defaults.getEnergyGoal());
    }
}
