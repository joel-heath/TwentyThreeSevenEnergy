package uk.ac.soton.comp2300.group42.energyclient.presentation.services;

import com.google.inject.Singleton;
import javafx.application.Platform;

import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableActivation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

@Singleton
public class NotificationService {

    private final Timer timer = new Timer(true);
    private final Hashtable<ObservableActivation, TimerTask> timerTasks = new Hashtable<>();
    private Consumer<ObservableActivation> onCleanupAction;

    public void setOnCleanupAction(Consumer<ObservableActivation> action) {
        this.onCleanupAction = action;
    }

    public LocalDateTime scheduleNotification(ObservableActivation activation) {
        ObservableAppliance appliance = activation.getAppliance();
        LocalDateTime targetTime = activation.getNextActivationDateTime();

        LocalDateTime now = LocalDateTime.now();
        long delay = Math.max(0, Duration.between(now, targetTime).toMillis());

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    Navigator.showPopup(appliance.getName());
                    timerTasks.remove(activation);
                    if (activation.getActivationType() == ActivationType.RECURRING) {
                        scheduleNotification(activation);
                        activation.triggerUpdate(); // Trigger UI to recalculate next activation time
                    }
                    else if (onCleanupAction != null) {
                        onCleanupAction.accept(activation);
                    }
                });
            }
        };

        timerTasks.put(activation, task);
        timer.schedule(task, delay);

        return targetTime;
    }

    public void cancelNotification(ObservableActivation activation) {
        TimerTask task = timerTasks.remove(activation);
        if (task != null) task.cancel();
    }

    public void rescheduleNotification(ObservableActivation activation) {
        cancelNotification(activation);
        scheduleNotification(activation);
    }

    public void showPopup(String message) {
        if (message == null || message.isBlank()) return;
        Platform.runLater(() -> Navigator.showPopup(message));
    }

    public void showPopup(String title, String description) {
        if (title == null || title.isBlank()) return;
        String safeDescription = (description == null) ? "" : description;
        Platform.runLater(() -> Navigator.showPopup(title, safeDescription));
    }
}