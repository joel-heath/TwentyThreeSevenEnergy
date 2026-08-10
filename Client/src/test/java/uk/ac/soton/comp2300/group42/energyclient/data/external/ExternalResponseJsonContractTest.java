package uk.ac.soton.comp2300.group42.energyclient.data.external;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExternalResponseJsonContractTest {

    private final JsonMapper mapper = new JsonMapper();

    @Test
    void unitRateResponse_deserializesSnakeCaseFields() throws Exception {
        String json = """
                {
                  "value_inc_vat": 19.1835,
                  "valid_from": "2026-03-03T22:00:00Z",
                  "valid_to": "2026-03-03T22:30:00Z"
                }
                """;

        UnitRateResponse response = mapper.readValue(json, UnitRateResponse.class);

        assertEquals(19.1835, response.valueIncVat());
        assertEquals("2026-03-03T22:00:00Z", response.validFrom());
        assertEquals("2026-03-03T22:30:00Z", response.validTo());
    }

    @Test
    void unitRateResponse_serializesToExpectedSnakeCaseFieldNames() throws Exception {
        UnitRateResponse response = new UnitRateResponse(21.5, "from", "to");

        String json = mapper.writeValueAsString(response);

        assertTrue(json.contains("\"value_inc_vat\":21.5"));
        assertTrue(json.contains("\"valid_from\":\"from\""));
        assertTrue(json.contains("\"valid_to\":\"to\""));
    }

    @Test
    void upcomingUnitRatesResponse_deserializesNestedList() throws Exception {
        String json = """
                {
                  "results": [
                    {"value_inc_vat": 10.0, "valid_from": "a", "valid_to": "b"},
                    {"value_inc_vat": 30.0, "valid_from": "c", "valid_to": "d"}
                  ]
                }
                """;

        UpcomingUnitRatesResponse response = mapper.readValue(json, UpcomingUnitRatesResponse.class);

        assertEquals(2, response.results().size());
        assertEquals(10.0, response.results().getFirst().valueIncVat());
        assertEquals("c", response.results().get(1).validFrom());
    }

    @Test
    void weatherResponse_deserializesNestedCurrentAndHourly() throws Exception {
        String json = """
                {
                  "latitude": 50.93,
                  "longitude": -1.39,
                  "current": {
                    "time": "2026-04-24T12:00",
                    "temperature_2m": 16.2,
                    "shortwave_radiation": 280.5
                  },
                  "hourly": {
                    "time": ["2026-04-24T12:00", "2026-04-24T13:00"],
                    "temperature_2m": [16.2, 16.8],
                    "shortwave_radiation": [280.5, 300.0]
                  }
                }
                """;

        WeatherResponse response = mapper.readValue(json, WeatherResponse.class);

        assertEquals(50.93, response.latitude());
        assertEquals(-1.39, response.longitude());
        assertEquals("2026-04-24T12:00", response.current().time());
        assertEquals(16.2, response.current().temperature_2m());
        assertEquals(280.5, response.current().shortwave_radiation());
        assertEquals(2, response.hourly().time().size());
        assertEquals(16.8, response.hourly().temperature_2m().get(1));
    }

    @Test
    void weatherRecords_constructAndExposeFields() {
        CurrentWeather current = new CurrentWeather("2026-04-24T12:00", 15.7, 310.2);
        HourlyWeather hourly = new HourlyWeather(
                java.util.List.of("2026-04-24T12:00"),
                java.util.List.of(15.7),
                java.util.List.of(310.2)
        );
        WeatherResponse weatherResponse = new WeatherResponse(50.93, -1.39, current, hourly);

        assertEquals("2026-04-24T12:00", current.time());
        assertEquals(15.7, current.temperature_2m());
        assertEquals(310.2, current.shortwave_radiation());
        assertEquals(java.util.List.of("2026-04-24T12:00"), hourly.time());
        assertEquals(50.93, weatherResponse.latitude());
        assertEquals(-1.39, weatherResponse.longitude());
        assertEquals(current, weatherResponse.current());
        assertEquals(hourly, weatherResponse.hourly());
    }
}
