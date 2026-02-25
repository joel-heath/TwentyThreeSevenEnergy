package uk.ac.soton.comp2300.group42.energyclient.presentation.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.Mode;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.Theme;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.PreferencesDTO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PreferencesModelTest {
    PreferencesDTO dto;
    PreferencesModel model;

    @Mock HouseModel mockHouse1;
    @Mock HouseModel mockHouse2;

    @BeforeEach void setUp() {
        dto = new PreferencesDTO(
                true,
                ColorVision.DEUTERAN,
                Theme.LIGHT_CONTRAST,
                Mode.ADVANCED,
                true,
                5.2,
                1L);

        model = new PreferencesModel(dto, mockHouse1);
    }

    @Test void testGetters() {
        when(mockHouse1.getId()).thenReturn(1L);

        assertEquals(dto.getLargeFont(), model.getLargeFont(), "Large Font should match DTO");
        assertEquals(dto.getVision(), model.getVision(), "Color Vision should match DTO");
        assertEquals(dto.getTheme(), model.getTheme(), "Theme should match DTO");
        assertEquals(dto.getMode(), model.getMode(), "Mode should match DTO");
        assertEquals(dto.getShareLocation(), model.getShareLocation(), "Share Location should match DTO");
        assertEquals(dto.getEnergyGoal(), model.getEnergyGoal(), 0.001, "Energy Goal should match DTO");
        assertEquals(dto.getActiveHouseId(), model.getActiveHouse().getId(), "Active House ID should match DTO");
        assertEquals(mockHouse1, model.getActiveHouse(), "Active House should match House given in constructor");
    }

    @Test void testSetters() {
        when(mockHouse2.getId()).thenReturn(2L);

        model.setLargeFont(false);
        model.setVision(ColorVision.ACHROMA);
        model.setTheme(Theme.DARK);
        model.setMode(Mode.SIMPLE);
        model.setShareLocation(false);
        model.setEnergyGoal(8.7);
        model.setActiveHouse(mockHouse2);

        assertFalse(model.getLargeFont());
        assertEquals(ColorVision.ACHROMA, model.getVision());
        assertEquals(Theme.DARK, model.getTheme());
        assertEquals(Mode.SIMPLE, model.getMode());
        assertFalse(model.getShareLocation());
        assertEquals(8.7, model.getEnergyGoal());
        assertEquals(mockHouse2, model.getActiveHouse());
        assertEquals(mockHouse2.getId(), model.getActiveHouse().getId());
    }

    @Test void testProperties() {
        assertNotNull(model.largeFontProperty());
        assertNotNull(model.visionProperty());
        assertNotNull(model.themeProperty());
        assertNotNull(model.modeProperty());
        assertNotNull(model.shareLocationProperty());
        assertNotNull(model.energyGoalProperty());
        assertNotNull(model.activeHouseProperty());
    }

    @Test void testCommit() {
        when(mockHouse2.getId()).thenReturn(2L);

        model.setLargeFont(true);
        model.setVision(ColorVision.PROTAN);
        model.setTheme(Theme.DARK);
        model.setMode(Mode.ADVANCED);
        model.setShareLocation(true);
        model.setEnergyGoal(4.5);
        model.setActiveHouse(mockHouse2);

        PreferencesDTO result = model.commit();

        assertTrue(result.getLargeFont(), "DTO Large Font should be updated");
        assertEquals(ColorVision.PROTAN, result.getVision(), "DTO Color Vision should be updated");
        assertEquals(Theme.DARK, result.getTheme(), "DTO Theme should be updated");
        assertEquals(Mode.ADVANCED, result.getMode(), "DTO Mode should be updated");
        assertTrue(result.getShareLocation(), "DTO Share Location should be updated");
        assertEquals(4.5, result.getEnergyGoal(), 0.001, "DTO Energy Goal should be updated");
        assertEquals(mockHouse2.getId(), result.getActiveHouseId(), "DTO Active House ID should be updated");
    }

    @Test void testUpdateFrom() {
        when(mockHouse2.getId()).thenReturn(2L);

        PreferencesDTO newDto = new PreferencesDTO(
            true,
            ColorVision.DEUTERAN,
            Theme.DARK_CONTRAST,
            Mode.ADVANCED,
            true,
            9.1,
                2L);

        model.updateFrom(newDto, mockHouse2);

        assertTrue(model.getLargeFont(), "Model LargeFont should be updated from new DTO");
        assertEquals(ColorVision.DEUTERAN, model.getVision(), "Model Vision should be updated from new DTO");
        assertEquals(Theme.DARK_CONTRAST, model.getTheme(), "Model Theme should be updated from new DTO");
        assertEquals(Mode.ADVANCED, model.getMode(), "Model Mode should be updated from new DTO");
        assertTrue(model.getShareLocation(), "Model ShareLocation should be updated from new DTO");
        assertEquals(9.1, model.getEnergyGoal(), 0.001, "Model EnergyGoal should be updated from new DTO");
        assertEquals(mockHouse2, model.getActiveHouse(), "Model Active House should be updated to from House model");
        assertEquals(mockHouse2.getId(), model.getActiveHouse().getId(), "Model Active House ID should be updated from new DTO");
    }
}
