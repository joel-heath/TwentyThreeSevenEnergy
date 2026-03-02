package uk.ac.soton.comp2300.group42.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleTest {

    @Test
    void roleHierarchy_ShouldBeCorrect() {
        assertThat(Role.OWNER.getLevel()).isGreaterThan(Role.RESIDENT.getLevel());
        assertThat(Role.RESIDENT.getLevel()).isGreaterThan(Role.GUEST.getLevel());
    }

    @Test
    void fromId_ShouldReturnCorrectRole_WhenIdIsValid() {
        assertThat(Role.fromId("owner")).isEqualTo(Role.OWNER);
        assertThat(Role.fromId("resident")).isEqualTo(Role.RESIDENT);
        assertThat(Role.fromId("guest")).isEqualTo(Role.GUEST);
    }

    @Test
    void fromId_ShouldThrowException_WhenIdIsUnknown() {
        assertThatThrownBy(() -> Role.fromId("invalid_role"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown role id: invalid_role");
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void fromId_ShouldThrowException_WhenIdIsNull() {
        assertThatThrownBy(() -> Role.fromId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Role id cannot be null");
    }
}