package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableActivation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.services.ActivationService;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.ApplianceStore;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class UpcomingActivationsViewModel {

    private final ObservableList<ObservableAppliance> appliances;
    private final SortedList<ObservableActivation> activations;
    private final ActivationService activationService;

    @Inject public UpcomingActivationsViewModel(ActivationService activationService, ApplianceStore applianceStore) {
        this.activationService = activationService;
        this.appliances = applianceStore.getAll();
        this.activations = new SortedList<>(activationService.getAll());
        this.activations.setComparator(Comparator.comparing(ObservableActivation::getNextActivationDateTime));

        CompletableFuture.runAsync(activationService::refreshAll);
    }

    public ObservableList<ObservableAppliance> getAppliances() { return appliances; }
    public SortedList<ObservableActivation> getActivations() { return activations; }

    public void removeActivation(ObservableActivation activation) {
        activationService.delete(activation);
    }

    public void updateActivation(ObservableActivation act, ObservableAppliance app, LocalTime time, LocalDate date,
                                 boolean recursMonday,
                                 boolean recursTuesday,
                                 boolean recursWednesday,
                                 boolean recursThursday,
                                 boolean recursFriday,
                                 boolean recursSaturday,
                                 boolean recursSunday,
                                 boolean isRecurring) {
        act.setAppliance(app);
        act.setActivationTime(time);

        if (isRecurring) {
            act.setActivationDate(null);
            act.setRecursMonday(recursMonday);
            act.setRecursTuesday(recursTuesday);
            act.setRecursWednesday(recursWednesday);
            act.setRecursThursday(recursThursday);
            act.setRecursFriday(recursFriday);
            act.setRecursSaturday(recursSaturday);
            act.setRecursSunday(recursSunday);
        }
        else {
            act.setActivationDate(date);
            act.setRecursMonday(false);
            act.setRecursTuesday(false);
            act.setRecursWednesday(false);
            act.setRecursThursday(false);
            act.setRecursFriday(false);
            act.setRecursSaturday(false);
            act.setRecursSunday(false);
        }

        activationService.save(act);
    }
}
