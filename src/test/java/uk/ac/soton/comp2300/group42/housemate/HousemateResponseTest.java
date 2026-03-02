package uk.ac.soton.comp2300.group42.housemate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class HousemateResponseTest {

    @Test
    void shouldSerializeCorrectly(JacksonTester<HousemateResponse> tester) throws IOException {
        var response = new HousemateResponse(
                15L,
                22L,
                "Silas",
                "silas@antioch.tr",
                Role.RESIDENT
        );

        var json = tester.write(response);

        assertThat(json).extractingJsonPathNumberValue("@.userId").isEqualTo(15);
        assertThat(json).extractingJsonPathNumberValue("@.houseId").isEqualTo(22);
        assertThat(json).extractingJsonPathStringValue("@.name").isEqualTo("Silas");
        assertThat(json).extractingJsonPathStringValue("@.email").isEqualTo("silas@antioch.tr");
        assertThat(json).extractingJsonPathStringValue("@.role").isEqualTo("resident");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<HousemateResponse> tester) throws IOException {
        var payload = """
                {
                    "userId": 16,
                    "houseId": 1,
                    "name": "Timothy",
                    "email": "timothy@lystra.tr",
                    "role": "guest"
                }
                """;

        var response = tester.parseObject(payload);

        assertThat(response.userId()).isEqualTo(16L);
        assertThat(response.houseId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Timothy");
        assertThat(response.email()).isEqualTo("timothy@lystra.tr");
        assertThat(response.role()).isEqualTo(Role.GUEST);
    }
}