package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.activation.ActivationResponse;
import uk.ac.soton.comp2300.group42.activation.CreateActivationRequest;
import uk.ac.soton.comp2300.group42.activation.UpdateActivationRequest;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendMapper;

import java.util.List;

@Singleton
public class ActivationClient extends BaseApiClient {

    @Inject
    public ActivationClient(AuthenticatedHttpClient httpClient, @BackendMapper ObjectMapper mapper) {
        super(httpClient, mapper);
    }

    public ActivationResponse postActivation(Long houseId, CreateActivationRequest request) {
        return post("houses/" + houseId + "/activations", request, new TypeReference<>() {});
    }

    public ActivationResponse fetchActivation(Long houseId, Long activationId) {
        return get("houses/" + houseId + "/activations/" + activationId, new TypeReference<>() {});
    }

    public List<ActivationResponse> fetchAllActivations(Long houseId) {
        return get("houses/" + houseId + "/activations", new TypeReference<>() {});
    }

    public ActivationResponse putActivation(Long houseId, Long activationId, UpdateActivationRequest request) {
        return put("houses/" + houseId + "/activations/" + activationId, request, new TypeReference<>() {});
    }

    public void deleteActivation(Long houseId, Long activationId) {
        delete("houses/" + houseId + "/activations/" + activationId);
    }

}
