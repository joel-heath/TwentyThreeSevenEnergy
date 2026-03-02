package uk.ac.soton.comp2300.group42.housemate;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class InviteHousemateRequestTest {

    @Test
    void validRequest_ShouldPassValidation(Validator validator) {
        var request = new InviteHousemateRequest("boaz@bethlehem.il", Role.RESIDENT);

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void blankEmail_ShouldFailValidation(Validator validator) {
        var request = new InviteHousemateRequest("   ", Role.RESIDENT);

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("Email is required"));
    }

    @Test
    void invalidEmailFormat_ShouldFailValidation(Validator validator) {
        var request = new InviteHousemateRequest("this_is_not_an_email", Role.RESIDENT);

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Must be a valid email address");
    }

    @Test
    void nullRole_ShouldFailValidation(Validator validator) {
        var request = new InviteHousemateRequest("oprah@moab.jo", null);

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Role is required");
    }

    @Test
    void shouldSerializeCorrectly(JacksonTester<InviteHousemateRequest> tester) throws IOException {
        var request = new InviteHousemateRequest("ruth@bethlehem.il", Role.OWNER);

        var json = tester.write(request);

        assertThat(json).extractingJsonPathStringValue("@.email").isEqualTo("ruth@bethlehem.il");
        assertThat(json).extractingJsonPathStringValue("@.role").isEqualTo("owner");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<InviteHousemateRequest> tester) throws IOException {
        var payload = """
                {
                    "email": "naomi@moab.jo",
                    "role": "resident"
                }
                """;

        var request = tester.parseObject(payload);

        assertThat(request.email()).isEqualTo("naomi@moab.jo");
        assertThat(request.role()).isEqualTo(Role.RESIDENT);
    }
}