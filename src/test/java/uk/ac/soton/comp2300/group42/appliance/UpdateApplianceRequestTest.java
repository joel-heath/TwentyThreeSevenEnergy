package uk.ac.soton.comp2300.group42.appliance;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class UpdateApplianceRequestTest {

    @Test
    void validRequest_ShouldPassValidation(Validator validator) {
        var request = new UpdateApplianceRequest("Tumble Dryer");

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankName_ShouldFailValidation(Validator validator) {
        var request = new UpdateApplianceRequest("   ");

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Appliance name must not be blank");
    }

    @Test
    void shouldSerializeCorrectly(JacksonTester<UpdateApplianceRequest> tester) throws IOException {
        var request = new UpdateApplianceRequest("Oven");

        var json = tester.write(request);

        assertThat(json).extractingJsonPathStringValue("@.name").isEqualTo("Oven");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<UpdateApplianceRequest> tester) throws IOException {
        var payload = """
                {
                    "name": "Washing Machine"
                }
                """;

        var request = tester.parseObject(payload);

        assertThat(request.name()).isEqualTo("Washing Machine");
    }
}