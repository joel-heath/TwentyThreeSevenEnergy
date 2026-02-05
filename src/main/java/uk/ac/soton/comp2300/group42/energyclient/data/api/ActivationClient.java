package uk.ac.soton.comp2300.group42.energyclient.data.api;

import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ActivationClient {

    private final List<ActivationDTO> activations = new ArrayList<>();

    public Optional<ActivationDTO> findById(Long id) {
        return activations.stream().filter(a -> Objects.equals(a.getId(), id)).findFirst();
    }

    public List<ActivationDTO> findAll() {
        return activations;
    }

    public ActivationDTO save(ActivationDTO activation) {
        // real implementation:
        // HttpRequest request = ...
        // return httpClient.sendAsync(...)

        // testing implementation:
        if (activation.getId() == null) {
            Long nextId = activations.stream()
                    .mapToLong(ActivationDTO::getId)
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
        return activation;
    }

    public void delete(ActivationDTO activation) {
        Optional<ActivationDTO> existing = findById(activation.getId());
        existing.ifPresent(activations::remove);
    }
}
