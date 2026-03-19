package uk.ac.soton.comp2300.group42.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnergyCategoryTest {

    @Test
    void fromId_ShouldReturnCorrectRole_WhenIdIsValid() {
        assertThat(EnergyCategory.fromId("electricity")).isEqualTo(EnergyCategory.ELECTRICITY);
        assertThat(EnergyCategory.fromId("gas")).isEqualTo(EnergyCategory.GAS);
        assertThat(EnergyCategory.fromId("other")).isEqualTo(EnergyCategory.OTHER);
    }

    @Test
    void fromId_ShouldThrowException_WhenIdIsUnknown() {
        assertThatThrownBy(() -> EnergyCategory.fromId("invalid_category"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown energy category id: invalid_category");
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void fromId_ShouldThrowException_WhenIdIsNull() {
        assertThatThrownBy(() -> EnergyCategory.fromId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Energy category id cannot be null");
    }
}
