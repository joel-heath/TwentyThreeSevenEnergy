package uk.ac.soton.comp2300.group42.metric;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class MetricResponseTest {

    @Test
    void shouldSerializeCorrectly(JacksonTester<MetricResponse> tester) throws IOException {
        var response = new MetricResponse(
                5L,
                10L,
                LocalDate.of(2025, 12, 25),
                32.89
        );

        var json = tester.write(response);

        assertThat(json).extractingJsonPathNumberValue("@.id").isEqualTo(5);
        assertThat(json).extractingJsonPathNumberValue("@.houseId").isEqualTo(10);
        assertThat(json).extractingJsonPathStringValue("@.date").isEqualTo("2025-12-25");
        assertThat(json).extractingJsonPathNumberValue("@.energyUsed").isEqualTo(32.89);
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<MetricResponse> tester) throws IOException {
        var payload = """
                {
                    "id": 2,
                    "houseId": 8,
                    "date": "2025-04-20",
                    "energyUsed": 60.24
                }
                """;

        var response = tester.parseObject(payload);

        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.houseId()).isEqualTo(8L);
        assertThat(response.date()).isEqualTo(LocalDate.of(2025, 4, 20));
        assertThat(response.energyUsed()).isEqualTo(60.24);
    }
}