package uk.ac.soton.comp2300.group42.user;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class LoginRequestTest {

    @Test
    void validRequest_ShouldPassValidation(Validator validator) {
        var request = new LoginRequest("shadrach@judah.il", "RefuseToBow!1");

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankEmail_ShouldFailValidation(Validator validator) {
        var request = new LoginRequest("   ", "SevenTimesHotter!");

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Email is required"));
    }

    @Test
    void invalidEmailFormat_ShouldFailValidation(Validator validator) {
        var request = new LoginRequest("not-nebuchadnezzar's-email", "ThreeMen!1");

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Must be a valid email address");
    }

    @Test
    void blankPassword_ShouldFailValidation(Validator validator) {
        var request = new LoginRequest("abednego@judah.il", "   ");

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Password is required"));
    }

    @Test
    void shouldSerializeCorrectly(JacksonTester<LoginRequest> tester) throws IOException {
        var request = new LoginRequest("shadrach@judah.il", "Not!TodayN3b");

        var json = tester.write(request);

        assertThat(json).extractingJsonPathStringValue("@.email").isEqualTo("shadrach@judah.il");
        assertThat(json).extractingJsonPathStringValue("@.password").isEqualTo("Not!TodayN3b");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<LoginRequest> tester) throws IOException {
        var payload = """
                {
                    "email": "meshach@judah.il",
                    "password": "Faith_Over_Fire!7"
                }
                """;

        var request = tester.parseObject(payload);

        assertThat(request.email()).isEqualTo("meshach@judah.il");
        assertThat(request.password()).isEqualTo("Faith_Over_Fire!7");
    }
}
