package uk.ac.soton.comp2300.group42.activation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class ActivationResponseTest {

    @Test
    void nonRecurring_ShouldSerializeCorrectlyAndIgnoreValidationMethods(JacksonTester<ActivationResponse> tester) throws IOException {
        var response = new ActivationResponse(
                27L, 1L, 3L, ActivationType.NON_RECURRING,
                LocalTime.of(16, 45, 0), LocalDate.of(2025, 12, 25),
                null, null, null, null, null, null, null
        );

        var json = tester.write(response);

        assertThat(json).extractingJsonPathNumberValue("@.id").isEqualTo(27);
        assertThat(json).extractingJsonPathNumberValue("@.applianceId").isEqualTo(1);
        assertThat(json).extractingJsonPathNumberValue("@.houseId").isEqualTo(3);
        assertThat(json).extractingJsonPathStringValue("@.type").isEqualTo("non_recurring");
        assertThat(json).extractingJsonPathStringValue("@.activationTime").isEqualTo("16:45:00");
        assertThat(json).extractingJsonPathStringValue("@.activationDate").isEqualTo("2025-12-25");
        assertThat(json).extractingJsonPathBooleanValue("@.recursMonday").isNull();
        assertThat(json).extractingJsonPathBooleanValue("@.recursTuesday").isNull();
        assertThat(json).extractingJsonPathBooleanValue("@.recursWednesday").isNull();
        assertThat(json).extractingJsonPathBooleanValue("@.recursThursday").isNull();
        assertThat(json).extractingJsonPathBooleanValue("@.recursFriday").isNull();
        assertThat(json).extractingJsonPathBooleanValue("@.recursSaturday").isNull();
        assertThat(json).extractingJsonPathBooleanValue("@.recursSunday").isNull();

        assertThat(json).doesNotHaveJsonPath("@.dateNonNullWhenNonRecurring");
        assertThat(json).doesNotHaveJsonPath("@.dateNullWhenRecurring");
        assertThat(json).doesNotHaveJsonPath("@.recurrenceDaysNullWhenNonRecurring");
        assertThat(json).doesNotHaveJsonPath("@.recurrenceDaysNonNullWhenRecurring");
        assertThat(json).doesNotHaveJsonPath("@.atLeastOneRecurrenceDayWhenRecurring");
    }

    @Test
    void recurring_shouldSerializeCorrectlyAndIgnoreValidationMethods(JacksonTester<ActivationResponse> tester) throws IOException {
        var response = new ActivationResponse(
                27L, 2L, 3L, ActivationType.RECURRING,
                LocalTime.of(7, 15, 0), null,
                true, true, false, false, false, false, true
        );

        var json = tester.write(response);

        assertThat(json).extractingJsonPathNumberValue("@.id").isEqualTo(27);
        assertThat(json).extractingJsonPathNumberValue("@.applianceId").isEqualTo(2);
        assertThat(json).extractingJsonPathNumberValue("@.houseId").isEqualTo(3);
        assertThat(json).extractingJsonPathStringValue("@.type").isEqualTo("recurring");
        assertThat(json).extractingJsonPathStringValue("@.activationTime").isEqualTo("07:15:00");
        assertThat(json).extractingJsonPathStringValue("@.activationDate").isNull();
        assertThat(json).extractingJsonPathBooleanValue("@.recursMonday").isTrue();
        assertThat(json).extractingJsonPathBooleanValue("@.recursTuesday").isTrue();
        assertThat(json).extractingJsonPathBooleanValue("@.recursWednesday").isFalse();
        assertThat(json).extractingJsonPathBooleanValue("@.recursThursday").isFalse();
        assertThat(json).extractingJsonPathBooleanValue("@.recursFriday").isFalse();
        assertThat(json).extractingJsonPathBooleanValue("@.recursSaturday").isFalse();
        assertThat(json).extractingJsonPathBooleanValue("@.recursSunday").isTrue();

        assertThat(json).doesNotHaveJsonPath("@.dateNonNullWhenNonRecurring");
        assertThat(json).doesNotHaveJsonPath("@.dateNullWhenRecurring");
        assertThat(json).doesNotHaveJsonPath("@.recurrenceDaysNullWhenNonRecurring");
        assertThat(json).doesNotHaveJsonPath("@.recurrenceDaysNonNullWhenRecurring");
        assertThat(json).doesNotHaveJsonPath("@.atLeastOneRecurrenceDayWhenRecurring");
    }

    @Test
    void nonRecurring_shouldDeserializeCorrectly(JacksonTester<ActivationResponse> tester) throws IOException {
        var payload = """
                {
                    "id": 27,
                    "applianceId": 1,
                    "houseId": 3,
                    "type": "non_recurring",
                    "activationTime": "16:45:00",
                    "activationDate": "2025-12-25"
                }
                """;

        var response = tester.parseObject(payload);

        assertThat(response.id()).isEqualTo(27);
        assertThat(response.applianceId()).isEqualTo(1);
        assertThat(response.houseId()).isEqualTo(3);
        assertThat(response.type()).isEqualTo(ActivationType.NON_RECURRING);
        assertThat(response.activationTime()).isEqualTo(LocalTime.of(16, 45, 0));
        assertThat(response.activationDate()).isEqualTo(LocalDate.of(2025, 12, 25));
        assertThat(response.recursMonday()).isNull();
    }

    @Test
    void recurring_shouldDeserializeCorrectly(JacksonTester<ActivationResponse> tester) throws IOException {
        var payload = """
                {
                    "id": 27,
                    "applianceId": 2,
                    "houseId": 3,
                    "type": "recurring",
                    "activationTime": "07:15:00",
                    "recursMonday": true,
                    "recursTuesday": true,
                    "recursWednesday": false,
                    "recursThursday": false,
                    "recursFriday": false,
                    "recursSaturday": false,
                    "recursSunday": true
                }
                """;

        var response = tester.parseObject(payload);

        assertThat(response.id()).isEqualTo(27);
        assertThat(response.applianceId()).isEqualTo(2);
        assertThat(response.houseId()).isEqualTo(3);
        assertThat(response.type()).isEqualTo(ActivationType.RECURRING);
        assertThat(response.activationTime()).isEqualTo(LocalTime.of(7, 15, 0));
        assertThat(response.activationDate()).isNull();
        assertThat(response.recursMonday()).isTrue();
        assertThat(response.recursTuesday()).isTrue();
        assertThat(response.recursWednesday()).isFalse();
        assertThat(response.recursThursday()).isFalse();
        assertThat(response.recursFriday()).isFalse();
        assertThat(response.recursSaturday()).isFalse();
        assertThat(response.recursSunday()).isTrue();
    }
}