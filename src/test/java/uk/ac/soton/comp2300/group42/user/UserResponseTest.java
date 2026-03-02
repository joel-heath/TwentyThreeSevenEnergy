package uk.ac.soton.comp2300.group42.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class UserResponseTest {

    @Test
    void shouldSerializeCorrectly(JacksonTester<UserResponse> tester) throws IOException {
        var response = new UserResponse(16L, "Joseph", "joseph@nazareth.il");

        var json = tester.write(response);

        assertThat(json).extractingJsonPathNumberValue("@.id").isEqualTo(16);
        assertThat(json).extractingJsonPathStringValue("@.name").isEqualTo("Joseph");
        assertThat(json).extractingJsonPathStringValue("@.email").isEqualTo("joseph@nazareth.il");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<UserResponse> tester) throws IOException {
        var payload = """
                {
                    "id": 52,
                    "name": "Mary",
                    "email": "mary@nazareth.il"
                }
                """;

        var response = tester.parseObject(payload);

        assertThat(response.id()).isEqualTo(52L);
        assertThat(response.name()).isEqualTo("Mary");
        assertThat(response.email()).isEqualTo("mary@nazareth.il");
    }
}