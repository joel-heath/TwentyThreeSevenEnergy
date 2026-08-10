package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.appliance.ApplianceResponse;
import uk.ac.soton.comp2300.group42.appliance.CreateApplianceRequest;
import uk.ac.soton.comp2300.group42.appliance.UpdateApplianceRequest;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendMapper;

import java.util.List;

@Singleton
public class ApplianceClient extends BaseApiClient {

    @Inject
    public ApplianceClient(AuthenticatedHttpClient httpClient, @BackendMapper JsonMapper mapper) {
        super(httpClient, mapper);
    }

    public ApplianceResponse postAppliance(Long houseId, CreateApplianceRequest request) {
        return post("houses/" + houseId + "/appliances", request, new TypeReference<>() {});
    }

    public ApplianceResponse fetchAppliance(Long houseId, Long applianceId) {
        return get("houses/" + houseId + "/appliances/" + applianceId, new TypeReference<>() {});
    }

    public List<ApplianceResponse> fetchAllAppliances(Long houseId) {
        return get("houses/" + houseId + "/appliances", new TypeReference<>() {});
    }

    public ApplianceResponse putAppliance(Long houseId, Long applianceId, UpdateApplianceRequest request) {
        return put("houses/" + houseId + "/appliances/" + applianceId, request, new TypeReference<>() {});
    }

    public void deleteAppliance(Long houseId, Long applianceId) {
        delete("houses/" + houseId + "/appliances/" + applianceId);
    }
}
