package uk.ac.soton.comp2300.group42.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class AuthResponseTest {

    @Test
    void shouldSerializeCorrectly(JacksonTester<AuthResponse> tester) throws IOException {
        var response = new AuthResponse("access-token", "refresh-token");

        var json = tester.write(response);

        assertThat(json).extractingJsonPathStringValue("@.accessToken").isEqualTo("access-token");
        assertThat(json).extractingJsonPathStringValue("@.refreshToken").isEqualTo("refresh-token");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<AuthResponse> tester) throws IOException {
        var payload = """
                {
                    "accessToken": "access-token",
                    "refreshToken": "refresh-token"
                }
                """;

        var response = tester.parseObject(payload);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
    }
}