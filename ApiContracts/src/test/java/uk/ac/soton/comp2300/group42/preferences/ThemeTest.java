package uk.ac.soton.comp2300.group42.preferences;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ThemeTest {

    @Test
    void fromId_ShouldReturnCorrectTheme_WhenIdIsValid() {
        assertThat(Theme.fromId("light")).isEqualTo(Theme.LIGHT);
        assertThat(Theme.fromId("dark")).isEqualTo(Theme.DARK);
        assertThat(Theme.fromId("light_high_contrast")).isEqualTo(Theme.LIGHT_CONTRAST);
        assertThat(Theme.fromId("dark_high_contrast")).isEqualTo(Theme.DARK_CONTRAST);
    }

    @Test
    void fromId_ShouldThrowException_WhenIdIsUnknown() {
        assertThatThrownBy(() -> Theme.fromId("invalid_theme"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown theme id: invalid_theme");
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void fromId_ShouldThrowException_WhenIdIsNull() {
        assertThatThrownBy(() -> Theme.fromId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Theme id cannot be null");
    }
}