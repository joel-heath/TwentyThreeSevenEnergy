package uk.ac.soton.comp2300.group42.energyserver.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;
import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.Preferences;
import uk.ac.soton.comp2300.group42.energyserver.model.User;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.PreferencesResponse;
import uk.ac.soton.comp2300.group42.preferences.Theme;

import static org.assertj.core.api.Assertions.assertThat;

class PreferencesMapperTest {

    private PreferencesMapper underTest;

    @BeforeEach
    void setUp() { underTest = Mappers.getMapper(PreferencesMapper.class); }

    @Test
    void shouldMapAllFieldsCorrectly() {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        House house = new House();
        ReflectionTestUtils.setField(house, "id", 10L);

        Preferences preferences = new Preferences();
        preferences.setUser(user);
        preferences.setLargeFont(true);
        preferences.setColorVision(ColorVision.PROTAN);
        preferences.setTheme(Theme.DARK);
        preferences.setMode(Mode.ADVANCED);
        preferences.setShareLocation(false);
        preferences.setEnergyGoal(1.5); // £1.50
        preferences.setActiveHouse(house);

        PreferencesResponse result = underTest.toPreferencesResponse(preferences);

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.largeFont()).isEqualTo(true);
        assertThat(result.vision()).isEqualTo(ColorVision.PROTAN);
        assertThat(result.theme()).isEqualTo(Theme.DARK);
        assertThat(result.mode()).isEqualTo(Mode.ADVANCED);
        assertThat(result.shareLocation()).isEqualTo(false);
        assertThat(result.energyGoal()).isEqualTo(1.5);
        assertThat(result.activeHouseId()).isEqualTo(10L);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        PreferencesResponse result = underTest.toPreferencesResponse(null);

        assertThat(result).isNull();
    }
}
