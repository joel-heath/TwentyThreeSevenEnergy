package uk.ac.soton.comp2300.group42.energyclient.services;

import javafx.application.Platform;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Activation;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ActivationRepository;
import uk.ac.soton.comp2300.group42.energyclient.util.Navigator;

import java.time.Duration;
import java.time.LocalDateTime;
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

        long delay = Duration.between(now, targetTime).toMillis();
        if (delay < 0)  return;

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    Navigator.showPopup(appliance.getName());
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