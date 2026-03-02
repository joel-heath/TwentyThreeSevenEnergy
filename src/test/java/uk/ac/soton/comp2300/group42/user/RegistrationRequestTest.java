package uk.ac.soton.comp2300.group42.user;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class RegistrationRequestTest {

    @Test
    void validRequest_ShouldPassValidation(Validator validator) {
        var request = new RegistrationRequest("Peter", "peter@bethsaida.il", "on_this_rock");

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankName_ShouldFailValidation(Validator validator) {
        var request = new RegistrationRequest("   ", "andrew@bethsaida.il", "fisher-of-men123");

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Name is required");
    }

    @Test
    void blankEmail_ShouldFailValidation(Validator validator) {
        var request = new RegistrationRequest("John", "   ", "1had@revelation");

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Email is required"));
    }

    @Test
    void invalidEmailFormat_ShouldFailValidation(Validator validator) {
        var request = new RegistrationRequest("Thomas", "invalid-email", "noway");

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Must be a valid email address");
    }

    @Test
    void blankPassword_ShouldFailValidation(Validator validator) {
        var request = new RegistrationRequest("Philip", "philip@bethsaida.il", "   ");

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Password is required");
    }

    @Test
    void shouldSerializeCorrectly(JacksonTester<RegistrationRequest> tester) throws IOException {
        var request = new RegistrationRequest("Matthew", "matthew@capernaum.il", "GiveMeYourTax");

        var json = tester.write(request);

        assertThat(json).extractingJsonPathStringValue("@.name").isEqualTo("Matthew");
        assertThat(json).extractingJsonPathStringValue("@.email").isEqualTo("matthew@capernaum.il");
        assertThat(json).extractingJsonPathStringValue("@.password").isEqualTo("GiveMeYourTax");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<RegistrationRequest> tester) throws IOException {
        var payload = """
                {
                    "name": "Judas",
                    "email": "judas@keiroth.il",
                    "password": "silver30"
                }
                """;

        var request = tester.parseObject(payload);

        assertThat(request.name()).isEqualTo("Judas");
        assertThat(request.email()).isEqualTo("judas@keiroth.il");
        assertThat(request.password()).isEqualTo("silver30");
    }
}