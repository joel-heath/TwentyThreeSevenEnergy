package uk.ac.soton.comp2300.group42.energyclient.services;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Activation;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ActivationRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService {

    private final Hashtable<Activation, TimerTask> timerTasks = new Hashtable<>();
    private final Timer timer = new Timer(true);
    private final ActivationRepository activationRepository;

    public NotificationService(ActivationRepository activationRepository) {
        this.activationRepository = activationRepository;
    }

    public void scheduleNotification(Activation activation) {
        Appliance appliance = activation.getAppliance();
        LocalDateTime targetTime = activation.getActivationTime();

        LocalDateTime now = LocalDateTime.now();
        String formattedTime = targetTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        long delay = Duration.between(now, targetTime).toMillis();
        if (delay < 0)  return;

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Scheduled Reminder");
                    alert.setHeaderText(appliance.getName() + " Reminder.");
                    alert.setContentText("The time is " + formattedTime + ", time to use the " + appliance.getName() + ".");
                    alert.show();
                    dismissActivation(activation);
                    timerTasks.remove(activation);
                });
            }
        };

        timerTasks.put(activation, task);
        timer.schedule(task, delay);
    }

    public void cancelNotification(Activation activation) {
        TimerTask task = timerTasks.remove(activation);
        if (task != null) task.cancel();
    }

    public void rescheduleNotification(Activation activation) {
        cancelNotification(activation);
        scheduleNotification(activation);
    }

    private void dismissActivation(Activation activation) {
        activationRepository.delete(activation);
    }
}