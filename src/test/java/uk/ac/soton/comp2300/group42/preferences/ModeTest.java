package uk.ac.soton.comp2300.group42.preferences;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModeTest {

    @Test
    void fromId_ShouldReturnCorrectMode_WhenIdIsValid() {
        assertThat(Mode.fromId("simple")).isEqualTo(Mode.SIMPLE);
        assertThat(Mode.fromId("advanced")).isEqualTo(Mode.ADVANCED);
    }

    @Test
    void fromId_ShouldThrowException_WhenIdIsUnknown() {
        assertThatThrownBy(() -> Mode.fromId("invalid_mode"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown mode id: invalid_mode");
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void fromId_ShouldThrowException_WhenIdIsNull() {
        assertThatThrownBy(() -> Mode.fromId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Mode id cannot be null");
    }
}