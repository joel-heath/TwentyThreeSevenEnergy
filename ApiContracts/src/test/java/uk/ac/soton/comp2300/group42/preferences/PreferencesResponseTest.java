package uk.ac.soton.comp2300.group42.preferences;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import uk.ac.soton.comp2300.group42.extensions.ApiContractTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ApiContractTest
class PreferencesResponseTest {

    @Test
    void shouldSerializeCorrectly(JacksonTester<PreferencesResponse> tester) throws IOException {
        var request = new PreferencesResponse(
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
    void shouldDeserializeCorrectly(JacksonTester<PreferencesResponse> tester) throws IOException {
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