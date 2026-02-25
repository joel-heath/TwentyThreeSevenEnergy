package uk.ac.soton.comp2300.group42.energyclient.data.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.soton.comp2300.group42.energyclient.presentation.model.EnergyPriceModel;

public class EnergyParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static EnergyPriceModel parse(String json) throws Exception {
        JsonNode root = mapper.readTree(json);
        JsonNode result = root.get("results").get(0);

        double price = result.get("value_inc_vat").asDouble();
        String validFrom = result.get("valid_from").asText();

        return new EnergyPriceModel(price, validFrom);
    }
}
