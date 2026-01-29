package uk.ac.soton.comp2300.group42.energyclient.model.repository;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Activation;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Appliance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActivationRepository {

    private final ObservableList<Activation> activations = FXCollections.observableArrayList(
            a -> new Observable[] { a.activationTimeProperty() }
    );

    public ObservableList<Activation> getActivations() { return activations; }

    public Optional<Activation> findById(int id) {
        return activations.stream().filter(a -> a.getId() == id).findFirst();
    }

    public List<Activation> findAll() {
        return new ArrayList<>(activations);
    }

    public Activation save(Activation activation) {
        if (activation.getId() == -1) {
            int nextId = activations.stream()
                    .mapToInt(Activation::getId)
                    .max()
                    .orElse(0) + 1;
            activation.setId(nextId);
        }
        else {
            Optional<Activation> existing = findById(activation.getId());
            existing.ifPresent(activations::remove);
        }

        activations.add(activation);
        return activation;
    }

    public Activation save(Appliance appliance, LocalDateTime targetDateTime) {
        int lastId = activations.stream().map(Activation::getId).max(Integer::compareTo).orElse(0);
        return save(new Activation(lastId + 1, appliance, targetDateTime));
    }

    public void delete(Activation activation) {
        activations.remove(activation);
    }
}
