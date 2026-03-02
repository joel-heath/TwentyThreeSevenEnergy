package uk.ac.soton.comp2300.group42.appliance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class ApplianceResponseTest {

    @Test
    void shouldSerializeCorrectly(JacksonTester<ApplianceResponse> tester) throws IOException {
        var response = new ApplianceResponse(1L, 1L, "Washing Machine");

        var json = tester.write(response);

        assertThat(json).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(json).extractingJsonPathNumberValue("@.houseId").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("@.name").isEqualTo("Washing Machine");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<ApplianceResponse> tester) throws IOException {
        var payload = """
                {
                    "id": 2,
                    "houseId": 1,
                    "name": "Dishwasher"
                }
                """;

        var response = tester.parseObject(payload);

        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.houseId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Dishwasher");
    }
}