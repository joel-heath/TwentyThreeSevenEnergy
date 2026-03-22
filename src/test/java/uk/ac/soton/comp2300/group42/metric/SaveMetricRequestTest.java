package uk.ac.soton.comp2300.group42.metric;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class SaveMetricRequestTest {

    @Test
    void validRequest_ShouldPassValidation(Validator validator) {
        SaveMetricRequest request = new SaveMetricRequest(42.5, EnergyCategory.ELECTRICITY);

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void nullEnergyUsed_ShouldFailValidation(Validator validator) {
        SaveMetricRequest request = new SaveMetricRequest(null, EnergyCategory.ELECTRICITY);

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Energy used must not be null");
    }

    @Test
    void negativeEnergyUsed_ShouldFailValidation(Validator validator) {
        SaveMetricRequest request = new SaveMetricRequest(-5.0, EnergyCategory.ELECTRICITY);

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Energy used must be a non-negative number");
    }

    @Test
    void shouldSerializeCorrectly(JacksonTester<SaveMetricRequest> tester) throws IOException {
        var request = new SaveMetricRequest(122.35, EnergyCategory.GAS);

        var json = tester.write(request);

        assertThat(json).extractingJsonPathNumberValue("@.energyUsed").isEqualTo(122.35);
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<SaveMetricRequest> tester) throws IOException {
        var payload = """
                {
                    "energyUsed": 122.35,
                    "category": "gas"
                }
                """;

        var request = tester.parseObject(payload);

        assertThat(request.energyUsed()).isEqualTo(122.35);
        assertThat(request.category()).isEqualTo(EnergyCategory.GAS);
    }
}