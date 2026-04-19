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
}
