package uk.ac.soton.comp2300.group42.energyclient.data.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.ac.soton.comp2300.group42.activation.ActivationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class ActivationClient {

    private final AuthenticatedHttpClient httpClient;
    private final ObjectMapper mapper;

    @Inject
    public ActivationClient(AuthenticatedHttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    private final List<ActivationResponse> activations = new ArrayList<>();

    public Optional<ActivationResponse> findById(Long id) {
        return activations.stream().filter(a -> Objects.equals(a.id(), id)).findFirst();
    }

    public List<ActivationResponse> findAll(Long houseId) {
        return activations;
    }

    public ActivationResponse save(ActivationResponse activation) {
        // real implementation:
        // HttpRequest request = ...
        // return httpClient.sendAsync(...)

        // testing implementation:
        /*
        if (activation.id() == null) {
            Long nextId = activations.stream()
                    .mapToLong(Activation::id)
                    .max()
                    .orElse(0L) + 1;

            try {
                Field idField = ActivationDTO.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(activation, nextId);
            }
            catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Failed to set id via reflection", e);
            }
        }
        else delete(activation);

        activations.add(activation);
        return activation;*/

        return activation;
    }

    public void delete(Long activationid) {
        //Optional<ActivationDTO> existing = findById(activation.getId());
        //existing.ifPresent(activations::remove);
    }
}
