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
        var request = new ChangePasswordRequest("Current!Pass1", "New!Pass2");

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankOldPassword_ShouldFailValidation(Validator validator) {
        var request = new ChangePasswordRequest("   ", "New!Pass2");

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Old password must not be blank"));
    }

    @Test
    void blankNewPassword_ShouldFailValidation(Validator validator) {
        var request = new ChangePasswordRequest("Current!Pass1", "   ");

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("New password must not be blank"));
    }

    @Test
    void weakNewPassword_ShouldFailValidation(Validator validator) {
        var request = new ChangePasswordRequest("Current!Pass1", "lowercase!");

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo(PasswordValidation.PASSWORD_QUALITY_MESSAGE);
    }

    @Test
    void shouldSerializeCorrectly(JacksonTester<ChangePasswordRequest> tester) throws IOException {
        var request = new ChangePasswordRequest("Current!Pass1", "New!Pass2");

        var json = tester.write(request);

        assertThat(json).extractingJsonPathStringValue("@.oldPassword").isEqualTo("Current!Pass1");
        assertThat(json).extractingJsonPathStringValue("@.newPassword").isEqualTo("New!Pass2");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<ChangePasswordRequest> tester) throws IOException {
        var payload = """
                {
                    "oldPassword": "Current!Pass1",
                    "newPassword": "New!Pass2"
                }
                """;

        var request = tester.parseObject(payload);

        assertThat(request.oldPassword()).isEqualTo("Current!Pass1");
        assertThat(request.newPassword()).isEqualTo("New!Pass2");
    }
}
