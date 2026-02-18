package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import com.google.inject.Inject;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class UpcomingActivationsViewModel {

    private final ObservableList<ApplianceModel> appliances;
    private final SortedList<ActivationModel> activations;
    private final Repository repository;

    @Inject public UpcomingActivationsViewModel(Repository repository) {
        this.repository = repository;
        this.appliances = repository.getAppliances();
        this.activations = new SortedList<>(repository.getActivations());
        this.activations.setComparator(Comparator.comparing(ActivationModel::getNextActivationDateTime));

        CompletableFuture.runAsync(repository::fetchAllData);
    }

    public ObservableList<ApplianceModel> getAppliances() { return appliances; }
    public SortedList<ActivationModel> getActivations() { return activations; }

    public void removeActivation(ActivationModel activation) {
        repository.deleteActivation(activation);
    }

    public void updateActivation(ActivationModel act, ApplianceModel app, LocalTime time, LocalDate date,
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

        repository.saveActivation(act);
    }
}
