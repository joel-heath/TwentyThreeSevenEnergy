package uk.ac.soton.comp2300.group42.appliance;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class CreateApplianceRequestTest {

    @Test
    void validRequest_ShouldPassValidation(Validator validator) {
        var request = new CreateApplianceRequest("Tumble Dryer");

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankName_ShouldFailValidation(Validator validator) {
        var request = new CreateApplianceRequest("   ");

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Appliance name must not be blank");
    }

    @Test
    void shouldSerializeCorrectly(JacksonTester<CreateApplianceRequest> tester) throws IOException {
        var request = new CreateApplianceRequest("Oven");

        var json = tester.write(request);

        assertThat(json).extractingJsonPathStringValue("@.name").isEqualTo("Oven");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<CreateApplianceRequest> tester) throws IOException {
        var payload = """
                {
                    "name": "Washing Machine"
                }
                """;

        var request = tester.parseObject(payload);

        assertThat(request.name()).isEqualTo("Washing Machine");
    }
}