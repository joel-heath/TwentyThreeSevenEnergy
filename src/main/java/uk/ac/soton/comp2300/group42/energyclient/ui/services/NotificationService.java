package uk.ac.soton.comp2300.group42.energyclient.ui.services;

import javafx.application.Platform;

import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class NotificationService {

    private final Timer timer = new Timer(true);
    private final Hashtable<ActivationModel, TimerTask> timerTasks = new Hashtable<>();
    private Consumer<ActivationModel> onCleanupAction;

    public void setOnCleanupAction(Consumer<ActivationModel> action) {
        this.onCleanupAction = action;
    }

    public LocalDateTime scheduleNotification(ActivationModel activation) {
        ApplianceModel appliance = activation.getAppliance();
        LocalDateTime targetTime = activation.getNextActivationDateTime();

        LocalDateTime now = LocalDateTime.now();
        long delay = Math.max(0, Duration.between(now, targetTime).toMillis());

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    Navigator.showPopup(appliance.getName());
                    timerTasks.remove(activation);
                    if (activation.isRecurring()) {
                        scheduleNotification(activation);
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

    public void cancelNotification(ActivationModel activation) {
        TimerTask task = timerTasks.remove(activation);
        if (task != null) task.cancel();
    }

    public void rescheduleNotification(ActivationModel activation) {
        cancelNotification(activation);
        scheduleNotification(activation);
    }
}