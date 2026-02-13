package uk.ac.soton.comp2300.group42.energyclient.ui.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Mode;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Theme;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.PreferencesDTO;

import static org.junit.jupiter.api.Assertions.*;

public class PreferencesModelTest {
    PreferencesDTO dto;
    PreferencesModel model;
    @Mock HouseModel mockHouse;

    @BeforeEach void setUp() {
        dto = new PreferencesDTO(
                true,
                ColorVision.DEUTERAN,
                Theme.LIGHT_CONTRAST,
                Mode.ADVANCED,
                true,
                5.2,
                -1L);

        model = new PreferencesModel(dto, mockHouse);
    }

    @Test void testGetters() {
        assertEquals(model.getLargeFont(), dto.getLargeFont(), "Large Font should match DTO");
        assertEquals(model.getVision(), dto.getVision(), "Color Vision should match DTO");
        assertEquals(model.getTheme(), dto.getTheme(), "Theme should match DTO");
        assertEquals(model.getMode(), dto.getMode(), "Mode should match DTO");
        assertEquals(model.getShareLocation(), dto.getShareLocation(), "Share Location should match DTO");
        assertEquals(model.getEnergyGoal(), dto.getEnergyGoal(), 0.001, "Energy Goal should match DTO");
    }

    @Test void testSetters() {
        model.setLargeFont(false);
        model.setVision(ColorVision.ACHROMA);
        model.setTheme(Theme.DARK);
        model.setMode(Mode.SIMPLE);
        model.setShareLocation(false);
        model.setEnergyGoal(8.7);

        assertFalse(model.getLargeFont());
        assertEquals(ColorVision.ACHROMA, model.getVision());
        assertEquals(Theme.DARK, model.getTheme());
        assertEquals(Mode.SIMPLE, model.getMode());
        assertFalse(model.getShareLocation());
        assertEquals(8.7, model.getEnergyGoal());
    }

    @Test void testProperties() {
        assertNotNull(model.largeFontProperty());
        assertNotNull(model.visionProperty());
        assertNotNull(model.themeProperty());
        assertNotNull(model.modeProperty());
        assertNotNull(model.shareLocationProperty());
        assertNotNull(model.energyGoalProperty());
    }

    @Test void testCommit() {
        model.setLargeFont(true);
        model.setVision(ColorVision.PROTAN);
        model.setTheme(Theme.DARK);
        model.setMode(Mode.ADVANCED);
        model.setShareLocation(true);
        model.setEnergyGoal(4.5);

        PreferencesDTO result = model.commit();

        assertTrue(result.getLargeFont(), "DTO Large Font should be updated");
        assertEquals(ColorVision.PROTAN, result.getVision(), "DTO Color Vision should be updated");
        assertEquals(Theme.DARK, result.getTheme(), "DTO Theme should be updated");
        assertEquals(Mode.ADVANCED, result.getMode(), "DTO Mode should be updated");
        assertTrue(result.getShareLocation(), "DTO Share Location should be updated");
        assertEquals(4.5, result.getEnergyGoal(), 0.001, "DTO Energy Goal should be updated");
    }

    @Test void testUpdateFrom() {
        PreferencesDTO newDto = new PreferencesDTO(
            true,
            ColorVision.DEUTERAN,
            Theme.DARK_CONTRAST,
            Mode.ADVANCED,
            true,
            9.1,
                -1L);

        model.updateFrom(newDto, mockHouse);

        assertTrue(model.getLargeFont(), "Model LargeFont should be updated from new DTO");
        assertEquals(ColorVision.DEUTERAN, model.getVision(), "Model Vision should be updated from new DTO");
        assertEquals(Theme.DARK_CONTRAST, model.getTheme(), "Model Theme should be updated from new DTO");
        assertEquals(Mode.ADVANCED, model.getMode(), "Model Mode should be updated from new DTO");
        assertTrue(model.getShareLocation(), "Model ShareLocation should be updated from new DTO");
        assertEquals(9.1, model.getEnergyGoal(), 0.001, "Model EnergyGoal should be updated from new DTO");
    }
}
