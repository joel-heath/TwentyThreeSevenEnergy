package uk.ac.soton.comp2300.group42.user;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class UpdateUserRequestTest {

    @Test
    void validRequest_ShouldPassValidation(Validator validator) {
        var request = new UpdateUserRequest("David", "david@bethlehem.judah");

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankName_ShouldFailValidation(Validator validator) {
        var request = new UpdateUserRequest("   ", "david@bethlehem.judah");

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Name must not be blank");
    }

    @Test
    void blankAddress_ShouldFailValidation(Validator validator) {
        var request = new UpdateUserRequest("David", "    ");

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email must not be blank");
    }

    @Test
    void shouldSerializeCorrectly(JacksonTester<UpdateUserRequest> tester) throws IOException {
        var request = new UpdateUserRequest("Saul", "saul@gibea.benjamin");

        var json = tester.write(request);

        assertThat(json).extractingJsonPathStringValue("@.name").isEqualTo("Saul");
        assertThat(json).extractingJsonPathStringValue("@.email").isEqualTo("saul@gibea.benjamin");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<UpdateUserRequest> tester) throws IOException {
        var payload = """
                {
                    "name": "Saul",
                    "email": "saul@gibea.benjamin"
                }
                """;

        var request = tester.parseObject(payload);

        assertThat(request.name()).isEqualTo("Saul");
        assertThat(request.email()).isEqualTo("saul@gibea.benjamin");
    }
}