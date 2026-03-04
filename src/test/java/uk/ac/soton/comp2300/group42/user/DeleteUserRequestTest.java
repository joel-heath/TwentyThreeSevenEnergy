package uk.ac.soton.comp2300.group42.user;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class DeleteUserRequestTest {

    @Test
    void validRequest_ShouldPassValidation(Validator validator) {
        var request = new DeleteUserRequest("very-secure-password");

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankPassword_ShouldFailValidation(Validator validator) {
        var request = new DeleteUserRequest("   ");

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Password must not be blank");
    }

    @Test
    void shouldSerializeCorrectly(JacksonTester<DeleteUserRequest> tester) throws IOException {
        var request = new DeleteUserRequest("very-secure-password");

        var json = tester.write(request);

        assertThat(json).extractingJsonPathStringValue("@.password").isEqualTo("very-secure-password");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<DeleteUserRequest> tester) throws IOException {
        var payload = """
                {
                    "password": "very-secure-password"
                }
                """;

        var request = tester.parseObject(payload);

        assertThat(request.password()).isEqualTo("very-secure-password");
    }
}