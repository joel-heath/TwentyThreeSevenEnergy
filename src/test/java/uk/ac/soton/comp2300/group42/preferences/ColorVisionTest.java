package uk.ac.soton.comp2300.group42.preferences;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ColorVisionTest {

    @Test
    void fromId_ShouldReturnCorrectColorVision_WhenIdIsValid() {
        assertThat(ColorVision.fromId("typical")).isEqualTo(ColorVision.TYPICAL);
        assertThat(ColorVision.fromId("protanopia")).isEqualTo(ColorVision.PROTAN);
        assertThat(ColorVision.fromId("deuteranopia")).isEqualTo(ColorVision.DEUTERAN);
        assertThat(ColorVision.fromId("tritanopia")).isEqualTo(ColorVision.TRITAN);
        assertThat(ColorVision.fromId("achromatopsia")).isEqualTo(ColorVision.ACHROMA);
    }

    @Test
    void fromId_ShouldThrowException_WhenIdIsUnknown() {
        assertThatThrownBy(() -> ColorVision.fromId("invalid_color_vision"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown color vision id: invalid_color_vision");
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void fromId_ShouldThrowException_WhenIdIsNull() {
        assertThatThrownBy(() -> ColorVision.fromId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Color vision id cannot be null");
    }
}