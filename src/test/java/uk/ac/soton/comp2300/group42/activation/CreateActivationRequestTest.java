package uk.ac.soton.comp2300.group42.activation;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class CreateActivationRequestTest {

    // Happy paths

    @Test
    void validNonRecurringRequest_ShouldPassValidation(Validator validator) {
        var request = new CreateActivationRequest(
                1L, 2L, ActivationType.NON_RECURRING,
                LocalTime.of(14, 30, 0), LocalDate.of(2025, 12, 25),
                null, null, null, null, null, null, null
        );

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void validRecurringRequest_ShouldPassValidation(Validator validator) {
        var request = new CreateActivationRequest(
                1L, 2L, ActivationType.RECURRING,
                LocalTime.of(14, 30, 0), null,
                true, false, false, false, false, false, false
        );

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    // Unhappy paths

    @Test
    void nonRecurringWithNullDate_ShouldFailValidation(Validator validator) {
        var request = new CreateActivationRequest(
                1L, 2L, ActivationType.NON_RECURRING,
                LocalTime.of(14, 30, 0), null,
                null, null, null, null, null, null, null
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Activation date is required when type is NON_RECURRING");
    }

    @Test
    void recurringWithNonNullDate_ShouldFailValidation(Validator validator) {
        var request = new CreateActivationRequest(
                1L, 2L, ActivationType.RECURRING,
                LocalTime.of(14, 30, 0), LocalDate.of(2025, 12, 25),
                true, false, false, false, false, false, false
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Activation date must be null when type is RECURRING");
    }

    @Test
    void nonRecurringWithRecurrenceFlags_ShouldFailValidation(Validator validator) {
        var request = new CreateActivationRequest(
                1L, 2L, ActivationType.NON_RECURRING,
                LocalTime.of(14, 30, 0), LocalDate.of(2025, 12, 25),
                true, null, null, null, null, null, null
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("All recurrence flags must be null when type is NON_RECURRING");
    }

    @Test
    void recurringWithNullRecurrenceFlags_ShouldFailValidation(Validator validator) {
        var request = new CreateActivationRequest(
                1L, 2L, ActivationType.RECURRING,
                LocalTime.of(14, 30, 0), null,
                null, null, null, null, null, null, null
        );

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().equals("All recurrence flags must not be null when type is RECURRING"));
    }

    @Test
    void recurringWithNoDaysSelected_ShouldFailValidation(Validator validator) {
        var request = new CreateActivationRequest(
                1L, 2L, ActivationType.RECURRING,
                LocalTime.of(14, 30, 0), null,
                false, false, false, false, false, false, false // At least one must be true
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("At least one recurrence day must be true when type is RECURRING");
    }

    // JSON Contract

    @Test
    void nonRecurring_ShouldSerializeCorrectlyAndIgnoreValidationMethods(JacksonTester<CreateActivationRequest> tester) throws IOException {
        var request = new CreateActivationRequest(
                1L, 3L, ActivationType.NON_RECURRING,
                LocalTime.of(16, 45, 0), LocalDate.of(2025, 12, 25),
                null, null, null, null, null, null, null
        );

        var json = tester.write(request);

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
    void recurring_shouldSerializeCorrectlyAndIgnoreValidationMethods(JacksonTester<CreateActivationRequest> tester) throws IOException {
        var request = new CreateActivationRequest(
                2L, 3L, ActivationType.RECURRING,
                LocalTime.of(7, 15, 0), null,
                true, true, false, false, false, false, true
        );

        var json = tester.write(request);

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
    void nonRecurring_shouldDeserializeCorrectly(JacksonTester<CreateActivationRequest> tester) throws IOException {
        var payload = """
                {
                    "applianceId": 1,
                    "houseId": 3,
                    "type": "non_recurring",
                    "activationTime": "16:45:00",
                    "activationDate": "2025-12-25"
                }
                """;

        var request = tester.parseObject(payload);

        assertThat(request.applianceId()).isEqualTo(1);
        assertThat(request.houseId()).isEqualTo(3);
        assertThat(request.type()).isEqualTo(ActivationType.NON_RECURRING);
        assertThat(request.activationTime()).isEqualTo(LocalTime.of(16, 45, 0));
        assertThat(request.activationDate()).isEqualTo(LocalDate.of(2025, 12, 25));
        assertThat(request.recursMonday()).isNull();
    }

    @Test
    void recurring_shouldDeserializeCorrectly(JacksonTester<CreateActivationRequest> tester) throws IOException {
        var payload = """
                {
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

        var request = tester.parseObject(payload);

        assertThat(request.applianceId()).isEqualTo(2);
        assertThat(request.houseId()).isEqualTo(3);
        assertThat(request.type()).isEqualTo(ActivationType.RECURRING);
        assertThat(request.activationTime()).isEqualTo(LocalTime.of(7, 15, 0));
        assertThat(request.activationDate()).isNull();
        assertThat(request.recursMonday()).isTrue();
        assertThat(request.recursTuesday()).isTrue();
        assertThat(request.recursWednesday()).isFalse();
        assertThat(request.recursThursday()).isFalse();
        assertThat(request.recursFriday()).isFalse();
        assertThat(request.recursSaturday()).isFalse();
        assertThat(request.recursSunday()).isTrue();
    }
}