package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.google.inject.Inject;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendMapper;
import uk.ac.soton.comp2300.group42.energyprice.EnergyCostResponse;
import uk.ac.soton.comp2300.group42.energyprice.EnergyPriceResponse;
import uk.ac.soton.comp2300.group42.energyprice.SaveEnergyPriceRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EnergyPriceClient extends BaseApiClient {

    @Inject
    public EnergyPriceClient(AuthenticatedHttpClient httpClient, @BackendMapper JsonMapper mapper) {
        super(httpClient, mapper);
    }

    public List<EnergyPriceResponse> postEnergyPrices(List<SaveEnergyPriceRequest> prices) {
        return post("energy-prices/prices", prices, new TypeReference<>() {});
    }

    public List<EnergyPriceResponse> fetchEnergyPricesForDate(LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        return get("energy-prices/prices?date=" + dateStr, new TypeReference<>() {});
    }

    public List<EnergyPriceResponse> fetchAllEnergyPrices() {
        return get("energy-prices/prices", new TypeReference<>() {});
    }

    public List<EnergyCostResponse> fetchEnergyCostsForDate(Long houseId, LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        return get("houses/" + houseId + "/costs?date=" + dateStr, new TypeReference<>() {});
    }
}
