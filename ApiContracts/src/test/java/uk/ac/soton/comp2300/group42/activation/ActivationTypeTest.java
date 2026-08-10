package uk.ac.soton.comp2300.group42.activation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActivationTypeTest {

    @Test
    void fromId_ShouldReturnCorrectActivationType_WhenIdIsValid() {
        assertThat(ActivationType.fromId("recurring")).isEqualTo(ActivationType.RECURRING);
        assertThat(ActivationType.fromId("non_recurring")).isEqualTo(ActivationType.NON_RECURRING);
    }

    @Test
    void fromId_ShouldThrowException_WhenIdIsUnknown() {
        assertThatThrownBy(() -> ActivationType.fromId("invalid_activation_type"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown activation type id: invalid_activation_type");
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void fromId_ShouldThrowException_WhenIdIsNull() {
        assertThatThrownBy(() -> ActivationType.fromId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Activation type id cannot be null");
    }
}