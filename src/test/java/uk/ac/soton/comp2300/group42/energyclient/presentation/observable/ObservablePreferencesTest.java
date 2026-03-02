package uk.ac.soton.comp2300.group42.energyclient.presentation.observable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ObservablePreferencesTest {

    Preferences domain;
    ObservablePreferences presentation;

    @Mock ObservableHouse mockHouse1;
    @Mock ObservableHouse mockHouse2;

    @BeforeEach void setUp() {
        domain = new Preferences(
                10L,
                true,
                ColorVision.DEUTERAN,
                Theme.LIGHT_CONTRAST,
                Mode.ADVANCED,
                true,
                5.2,
                1L
        );

        presentation = new ObservablePreferences(domain, mockHouse1);
    }

    @Test void testGetters() {
        when(mockHouse1.getId()).thenReturn(1L);

        assertEquals(domain.largeFont(), presentation.getLargeFont(), "Large Font should match domain model");
        assertEquals(domain.vision(), presentation.getVision(), "Color Vision should match domain model");
        assertEquals(domain.theme(), presentation.getTheme(), "Theme should match domain model");
        assertEquals(domain.mode(), presentation.getMode(), "Mode should match domain model");
        assertEquals(domain.shareLocation(), presentation.getShareLocation(), "Share Location should match domain model");
        assertEquals(domain.energyGoal(), presentation.getEnergyGoal(), 0.001, "Energy Goal should match domain model");
        assertEquals(domain.activeHouseId(), presentation.getActiveHouse().getId(), "Active House ID should match domain model");
        assertEquals(mockHouse1, presentation.getActiveHouse(), "Active House should match House given in constructor");
    }

    @Test void testSetters() {
        when(mockHouse2.getId()).thenReturn(2L);

        presentation.setLargeFont(false);
        presentation.setVision(ColorVision.ACHROMA);
        presentation.setTheme(Theme.DARK);
        presentation.setMode(Mode.SIMPLE);
        presentation.setShareLocation(false);
        presentation.setEnergyGoal(8.7);
        presentation.setActiveHouse(mockHouse2);

        assertFalse(presentation.getLargeFont(), "Large Font should be updated after setter");
        assertEquals(ColorVision.ACHROMA, presentation.getVision(), "Color Vision should be updated after setter");
        assertEquals(Theme.DARK, presentation.getTheme(), "Theme should be updated after setter");
        assertEquals(Mode.SIMPLE, presentation.getMode(), "Mode should be updated after setter");
        assertFalse(presentation.getShareLocation(), "Share Location should be updated after setter");
        assertEquals(8.7, presentation.getEnergyGoal(), 0.001, "Energy Goal should be updated after setter");
        assertEquals(mockHouse2, presentation.getActiveHouse(), "Active House should be updated after setter");
        assertEquals(mockHouse2.getId(), presentation.getActiveHouse().getId(), "Active House ID should be updated after setter");
    }

    @Test void testProperties() {
        assertNotNull(presentation.largeFontProperty(), "Large Font property should not be null");
        assertNotNull(presentation.visionProperty(), "Color Vision property should not be null");
        assertNotNull(presentation.themeProperty(), "Theme property should not be null");
        assertNotNull(presentation.modeProperty(), "Mode property should not be null");
        assertNotNull(presentation.shareLocationProperty(), "Share Location property should not be null");
        assertNotNull(presentation.energyGoalProperty(), "Energy Goal property should not be null");
        assertNotNull(presentation.activeHouseProperty(), "Active House property should not be null");
    }

    @Test void testCommit() {
        when(mockHouse2.getId()).thenReturn(2L);

        presentation.setLargeFont(true);
        presentation.setVision(ColorVision.PROTAN);
        presentation.setTheme(Theme.DARK);
        presentation.setMode(Mode.ADVANCED);
        presentation.setShareLocation(true);
        presentation.setEnergyGoal(4.5);
        presentation.setActiveHouse(mockHouse2);

        Preferences result = presentation.commit(2L);

        assertEquals(2L, result.userId(), "Domain model ID should be correct");
        assertTrue(result.largeFont(), "Domain model Large Font should be correct");
        assertEquals(ColorVision.PROTAN, result.vision(), "Domain model Color Vision should be correct");
        assertEquals(Theme.DARK, result.theme(), "Domain model Theme should be correct");
        assertEquals(Mode.ADVANCED, result.mode(), "Domain model Mode should be correct");
        assertTrue(result.shareLocation(), "Domain model Share Location should be correct");
        assertEquals(4.5, result.energyGoal(), 0.001, "Domain model Energy Goal should be correct");
        assertEquals(mockHouse2.getId(), result.activeHouseId(), "Domain model Active House ID should be correct");
    }

    @Test void testUpdateFrom() {
        when(mockHouse2.getId()).thenReturn(2L);

        Preferences newDomain = new Preferences(
            10L,
            true,
            ColorVision.DEUTERAN,
            Theme.DARK_CONTRAST,
            Mode.ADVANCED,
            true,
            9.1,
            2L
        );

        presentation.updateFrom(newDomain, mockHouse2);

        assertTrue(presentation.getLargeFont(), "Large Font should be updated from domain model");
        assertEquals(ColorVision.DEUTERAN, presentation.getVision(), "Vision should be updated from domain model");
        assertEquals(Theme.DARK_CONTRAST, presentation.getTheme(), "Theme should be updated from domain model");
        assertEquals(Mode.ADVANCED, presentation.getMode(), "Mode should be updated from domain model");
        assertTrue(presentation.getShareLocation(), "Share Location should be updated from domain model");
        assertEquals(9.1, presentation.getEnergyGoal(), 0.001, "Energy Goal should be updated from domain model");
        assertEquals(mockHouse2, presentation.getActiveHouse(), "Active House should be updated");
        assertEquals(mockHouse2.getId(), presentation.getActiveHouse().getId(), "Active House ID should be updated from domain model");
    }
}
