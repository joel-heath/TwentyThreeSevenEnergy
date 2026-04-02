package uk.ac.soton.comp2300.group42.metric;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class MetricResponseTest {

    @Test
    void shouldSerializeCorrectly(JacksonTester<MetricResponse> tester) throws IOException {
        var response = new MetricResponse(
                5L,
                10L,
                LocalDateTime.of(LocalDate.of(2025, 12, 25), LocalTime.of(15, 30)),
                32.89,
                EnergyCategory.OTHER
        );

        var json = tester.write(response);

        assertThat(json).extractingJsonPathNumberValue("@.id").isEqualTo(5);
        assertThat(json).extractingJsonPathNumberValue("@.houseId").isEqualTo(10);
        assertThat(json).extractingJsonPathStringValue("@.dateTime").isEqualTo("2025-12-25T15:30:00");
        assertThat(json).extractingJsonPathNumberValue("@.energyUsed").isEqualTo(32.89);
        assertThat(json).extractingJsonPathStringValue("@.category").isEqualTo("other");
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<MetricResponse> tester) throws IOException {
        var payload = """
                {
                    "id": 2,
                    "houseId": 8,
                    "dateTime": "2025-04-20T16:20:00",
                    "energyUsed": 60.24,
                    "category": "electricity"
                }
                """;

        var response = tester.parseObject(payload);

        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.houseId()).isEqualTo(8L);
        assertThat(response.dateTime()).isEqualTo(LocalDateTime.of(LocalDate.of(2025, 4, 20), LocalTime.of(16, 20)));
        assertThat(response.energyUsed()).isEqualTo(60.24);
        assertThat(response.category()).isEqualTo(EnergyCategory.ELECTRICITY);
    }
}