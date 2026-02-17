package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.util.Duration;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class SimpleDashboardViewModel {

    private final IntegerProperty counter = new SimpleIntegerProperty(0);

    private final Repository repository;
    private final ObservableList<ApplianceModel> appliances;
    private final SortedList<ActivationModel> activations;

    private final EnergyCalculator calc;

    public SimpleDashboardViewModel(Repository repository, EnergyCalculator calc) {
        this.repository = repository;
        this.calc = calc;

        this.appliances = repository.getAppliances();
        this.activations = new SortedList<>(repository.getActivations());
        this.activations.setComparator(Comparator.comparing(ActivationModel::getNextActivationDateTime));

        CompletableFuture.runAsync(repository::fetchAllData); // Run on a background thread so UI doesn't hang if the API is slow
    }

    public ObservableList<ApplianceModel> getAppliances() { return appliances; }
    public SortedList<ActivationModel> getActivations() { return activations; }
    public IntegerProperty counterProperty() { return counter; }

    public void incrementCounter() {
        counter.set(counter.get() + 1);
    }

    public void startAutoUpdateTest() {
        Timeline testTimeline = new Timeline(
            new KeyFrame(Duration.seconds(1), _ -> {
                counter.set(counter.get() + 1);
            })
        );
        testTimeline.setCycleCount(Timeline.INDEFINITE);
        testTimeline.play();
    }

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

    public PreferencesModel getPreferences() { return repository.getPreferences(); }
}
