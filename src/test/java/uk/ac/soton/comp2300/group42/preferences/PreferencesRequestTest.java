package uk.ac.soton.comp2300.group42.preferences;

import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class PreferencesRequestTest {

    @Test
    void validRequest_ShouldPassValidation(Validator validator) {
        var request = new PreferencesRequest(
                7L,
                true,
                ColorVision.DEUTERAN,
                Theme.DARK,
                Mode.ADVANCED,
                true,
                150.5,
                42L
        );

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void nullUserId_ShouldFailValidation(Validator validator) {
        var request = new PreferencesRequest(
                null, true, ColorVision.DEUTERAN, Theme.DARK, Mode.ADVANCED, true, 150.5, 42L
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("User ID must not be null");
    }

    @Test
    void nullLargeFont_ShouldFailValidation(Validator validator) {
        var request = new PreferencesRequest(
                7L, null, ColorVision.DEUTERAN, Theme.DARK, Mode.ADVANCED, true, 150.5, 42L
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Large font preference must not be null");
    }

    @Test
    void nullVision_ShouldFailValidation(Validator validator) {
        var request = new PreferencesRequest(
                7L, true, null, Theme.DARK, Mode.ADVANCED, true, 150.5, 42L
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Color vision preference must not be null");
    }

    @Test
    void nullTheme_ShouldFailValidation(Validator validator) {
        var request = new PreferencesRequest(
                7L, true, ColorVision.DEUTERAN, null, Mode.ADVANCED, true, 150.5, 42L
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Theme preference must not be null");
    }

    @Test
    void nullMode_ShouldFailValidation(Validator validator) {
        var request = new PreferencesRequest(
                7L, true, ColorVision.DEUTERAN, Theme.DARK, null, true, 150.5, 42L
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("UI mode preference must not be null");
    }

    @Test
    void nullShareLocation_ShouldFailValidation(Validator validator) {
        var request = new PreferencesRequest(
                7L, true, ColorVision.DEUTERAN, Theme.DARK, Mode.ADVANCED, null, 150.5, 42L
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Location sharing preference must not be null");
    }

    @Test
    void nullEnergyGoal_ShouldFailValidation(Validator validator) {
        var request = new PreferencesRequest(
                7L, true, ColorVision.DEUTERAN, Theme.DARK, Mode.ADVANCED, true, null, 42L
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Energy goal must not be null");
    }

    @Test
    void negativeEnergyGoal_ShouldFailValidation(Validator validator) {
        var request = new PreferencesRequest(
                7L, true, ColorVision.DEUTERAN, Theme.DARK, Mode.ADVANCED, true, -13.2, 42L
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Energy goal must be a non-negative number");
    }

    @Test
    void nullActiveHouseId_ShouldFailValidation(Validator validator) {
        var request = new PreferencesRequest(
                7L, true, ColorVision.DEUTERAN, Theme.DARK, Mode.ADVANCED, true, 150.5, null
        );

        var violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Active house ID must not be null");
    }

    @Test
    void shouldSerializeCorrectly(JacksonTester<PreferencesRequest> tester) throws IOException {
        var request = new PreferencesRequest(
                84L,
                false,
                ColorVision.PROTAN,
                Theme.LIGHT_CONTRAST,
                Mode.SIMPLE,
                true,
                250.75,
                8L
        );

        var json = tester.write(request);

        assertThat(json).extractingJsonPathNumberValue("@.userId").isEqualTo(84);
        assertThat(json).extractingJsonPathBooleanValue("@.largeFont").isFalse();
        assertThat(json).extractingJsonPathStringValue("@.vision").isEqualTo("protanopia");
        assertThat(json).extractingJsonPathStringValue("@.theme").isEqualTo("light_high_contrast");
        assertThat(json).extractingJsonPathStringValue("@.mode").isEqualTo("simple");
        assertThat(json).extractingJsonPathBooleanValue("@.shareLocation").isTrue();
        assertThat(json).extractingJsonPathNumberValue("@.energyGoal").isEqualTo(250.75);
        assertThat(json).extractingJsonPathNumberValue("@.activeHouseId").isEqualTo(8);
    }

    @Test
    void shouldDeserializeCorrectly(JacksonTester<PreferencesRequest> tester) throws IOException {
        var payload = """
                {
                    "userId": 100,
                    "largeFont": false,
                    "vision": "protanopia",
                    "theme": "light_high_contrast",
                    "mode": "simple",
                    "shareLocation": true,
                    "energyGoal": 250.75,
                    "activeHouseId": 8
                }
                """;

        var request = tester.parseObject(payload);

        assertThat(request.userId()).isEqualTo(100L);
        assertThat(request.largeFont()).isFalse();
        assertThat(request.vision()).isEqualTo(ColorVision.PROTAN);
        assertThat(request.theme()).isEqualTo(Theme.LIGHT_CONTRAST);
        assertThat(request.mode()).isEqualTo(Mode.SIMPLE);
        assertThat(request.shareLocation()).isTrue();
        assertThat(request.energyGoal()).isEqualTo(250.75);
        assertThat(request.activeHouseId()).isEqualTo(8L);
    }
}