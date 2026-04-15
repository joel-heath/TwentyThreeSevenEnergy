package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableActivation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.services.ActivationService;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.ApplianceStore;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class UpcomingActivationsViewModel {

    private final ObservableList<ObservableAppliance> appliances;
    private final SortedList<ObservableActivation> activations;
    private final ActivationService activationService;

    private final ObjectProperty<ObservableActivation> selectedActivation = new SimpleObjectProperty<>(null);

    @Inject public UpcomingActivationsViewModel(ActivationService activationService, ApplianceStore applianceStore) {
        this.activationService = activationService;
        this.appliances = applianceStore.getAll();
        this.activations = new SortedList<>(activationService.getAll());
        this.activations.setComparator(Comparator.comparing(ObservableActivation::getNextActivationDateTime));
    }

    public CompletableFuture<Void> refreshActivationsAsync() {
        return activationService.refreshAllAsync();
    }

    public void selectActivation(ObservableActivation activation) {
        selectedActivation.set(activation);
    }

    public ObservableList<ObservableAppliance> getAppliances() { return appliances; }
    public SortedList<ObservableActivation> getActivations() { return activations; }
    public ObjectProperty<ObservableActivation> selectedActivationProperty() { return selectedActivation; }
}
