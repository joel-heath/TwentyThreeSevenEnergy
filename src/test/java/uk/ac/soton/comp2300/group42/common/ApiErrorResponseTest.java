package uk.ac.soton.comp2300.group42.common;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class ApiErrorResponseTest {

    @Test
    void shouldSerializeCorrectly(JacksonTester<ApiErrorResponse> tester) throws IOException {
        var response = new ApiErrorResponse(
                Instant.parse("2025-12-25T13:30:00Z"),
                404,
                "Not Found",
                "House with ID 6 not found",
                "/api/houses/6"
        );

        var json = tester.write(response);

        assertThat(json).extractingJsonPathStringValue("@.timestamp").isEqualTo("2025-12-25T13:30:00Z");
        assertThat(json).extractingJsonPathNumberValue("@.status").isEqualTo(404);
        assertThat(json).extractingJsonPathStringValue("@.error").isEqualTo("Not Found");
        assertThat(json).extractingJsonPathStringValue("@.message").isEqualTo("House with ID 6 not found");
        assertThat(json).extractingJsonPathStringValue("@.path").isEqualTo("/api/houses/6");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<ApiErrorResponse> tester) throws IOException {
        var payload = """
                {
                    "timestamp": "2025-12-25T13:30:00Z",
                    "status": 404,
                    "error": "Not Found",
                    "message": "House with ID 6 not found",
                    "path": "/api/houses/6"
                }
                """;

        var response = tester.parseObject(payload);

        assertThat(response.timestamp()).isEqualTo(Instant.parse("2025-12-25T13:30:00Z"));
        assertThat(response.status()).isEqualTo(404);
        assertThat(response.error()).isEqualTo("Not Found");
        assertThat(response.message()).isEqualTo("House with ID 6 not found");
        assertThat(response.path()).isEqualTo("/api/houses/6");
    }
}