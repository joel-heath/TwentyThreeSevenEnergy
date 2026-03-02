package uk.ac.soton.comp2300.group42.house;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class HouseResponseTest {

    @Test
    void shouldSerializeCorrectly(JacksonTester<HouseResponse> tester) throws IOException {
        var response = new HouseResponse(
                1L,
                "Primary House",
                "123 Main St",
                ZoneId.of("Europe/London"),
                Role.OWNER
        );

        var json = tester.write(response);

        assertThat(json).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("@.name").isEqualTo("Primary House");
        assertThat(json).extractingJsonPathStringValue("@.address").isEqualTo("123 Main St");
        assertThat(json).extractingJsonPathStringValue("@.timezone").isEqualTo("Europe/London");
        assertThat(json).extractingJsonPathStringValue("@.role").isEqualTo(Role.OWNER.getId());
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<HouseResponse> tester) throws IOException {
        var jsonContent = """
                {
                    "id": 1,
                    "name": "Primary House",
                    "address": "123 Main St",
                    "timezone": "Europe/London",
                    "role": "owner"
                }
                """;

        var response = tester.parse(jsonContent).getObject();

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Primary House");
        assertThat(response.address()).isEqualTo("123 Main St");
        assertThat(response.timezone()).isEqualTo(ZoneId.of("Europe/London"));
        assertThat(response.role()).isEqualTo(Role.OWNER);
    }
}