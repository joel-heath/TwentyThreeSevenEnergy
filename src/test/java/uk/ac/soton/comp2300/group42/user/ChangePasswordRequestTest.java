package uk.ac.soton.comp2300.group42.user;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class ChangePasswordRequestTest {

    @Test
    void validRequest_ShouldPassValidation(Validator validator) {
        var request = new ChangePasswordRequest("not-very-secure-password", "very-secure-password");

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankOldPassword_ShouldFailValidation(Validator validator) {
        var request = new ChangePasswordRequest("   ", "very-secure-password");

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Old password must not be blank");
    }

    @Test
    void blankNewPassword_ShouldFailValidation(Validator validator) {
        var request = new ChangePasswordRequest("not-very-secure-password", "   ");

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("New password must not be blank");
    }

    @Test
    void shouldSerializeCorrectly(JacksonTester<ChangePasswordRequest> tester) throws IOException {
        var request = new ChangePasswordRequest("not-very-secure-password", "very-secure-password");

        var json = tester.write(request);

        assertThat(json).extractingJsonPathStringValue("@.oldPassword").isEqualTo("not-very-secure-password");
        assertThat(json).extractingJsonPathStringValue("@.newPassword").isEqualTo("very-secure-password");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<ChangePasswordRequest> tester) throws IOException {
        var payload = """
                {
                    "oldPassword": "not-very-secure-password",
                    "newPassword": "very-secure-password"
                }
                """;

        var request = tester.parseObject(payload);

        assertThat(request.oldPassword()).isEqualTo("not-very-secure-password");
        assertThat(request.newPassword()).isEqualTo("very-secure-password");
    }
}