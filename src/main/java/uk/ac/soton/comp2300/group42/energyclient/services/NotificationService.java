package uk.ac.soton.comp2300.group42.energyclient.services;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService {

    private static final Timer timer = new Timer(true);

    public static void scheduleNotification(String applianceName, LocalDateTime targetTime) {
        LocalDateTime now = LocalDateTime.now();
        String formattedTime = targetTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        long delay = Duration.between(now, targetTime).toMillis();

        if (delay < 0)  return;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Scheduled Reminder");
                    alert.setHeaderText(applianceName + " Reminder.");
                    alert.setContentText("The time is " + formattedTime + ", time to use the " + applianceName + ".");
                    alert.show();
                });
            }
        }, delay);
    }
}