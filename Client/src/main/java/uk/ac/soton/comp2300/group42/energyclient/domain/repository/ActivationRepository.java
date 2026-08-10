package uk.ac.soton.comp2300.group42.energyclient.domain.repository;

import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;

import java.util.List;

public interface ActivationRepository {
    Activation add(Activation activation);
    Activation get(Long houseId, Long activationId);
    List<Activation> getAll(Long houseId);
    Activation update(Activation activation);
    void delete(Long houseId, Long activationId);
}
