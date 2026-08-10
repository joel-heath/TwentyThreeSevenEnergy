package uk.ac.soton.comp2300.group42.house;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class CreateHouseRequestTest {

    @Test
    void validRequest_ShouldPassValidation(Validator validator) {
        var request = new CreateHouseRequest(
                "Primary House",
                "123 Main St",
                ZoneId.of("Europe/London")
        );

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankName_ShouldFailValidation(Validator validator) {
        var request = new CreateHouseRequest(
                "  \t\r\n  ",
                "123 Main St",
                ZoneId.of("Europe/London")
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("House name must not be blank");
    }

    @Test
    void blankAddress_ShouldFailValidation(Validator validator) {
        var request = new CreateHouseRequest(
                "Primary House",
                "  \t\r\n  ",
                ZoneId.of("Europe/London")
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("House address must not be blank");
    }

    @Test
    void nullTimezone_ShouldFailValidation(Validator validator) {
        var request = new CreateHouseRequest(
                "Primary House",
                "123 Main St",
                null
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Timezone must not be null");
    }

    @Test
    void shouldSerializeCorrectly(JacksonTester<CreateHouseRequest> tester) throws IOException {
        var request = new CreateHouseRequest(
                "Primary House",
                "123 Main St",
                ZoneId.of("Europe/London")
        );

        var json = tester.write(request);

        assertThat(json).extractingJsonPathStringValue("@.name").isEqualTo("Primary House");
        assertThat(json).extractingJsonPathStringValue("@.address").isEqualTo("123 Main St");
        assertThat(json).extractingJsonPathStringValue("@.timezone").isEqualTo("Europe/London");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<CreateHouseRequest> tester) throws IOException {
        var payload = """
                {
                    "name": "Primary House",
                    "address": "123 Main St",
                    "timezone": "Europe/London"
                }
                """;

        var request = tester.parseObject(payload);

        assertThat(request.name()).isEqualTo("Primary House");
        assertThat(request.address()).isEqualTo("123 Main St");
        assertThat(request.timezone()).isEqualTo(ZoneId.of("Europe/London"));
    }
}