package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Theme;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Singleton
public class RootViewModel {

    public record Notification(String title, String description) {}

    private final ObservablePreferences preferences;
    private final ObservableList<Notification> activeNotifications;

    @Inject public RootViewModel(ObservablePreferences preferences) {
        this.preferences = preferences;
        this.activeNotifications = FXCollections.observableArrayList();
    }

    public ObjectProperty<Theme> themeProperty() {
        return preferences.themeProperty();
    }

    public ObjectProperty<ColorVision> visionProperty() {
        return preferences.visionProperty();
    }

    public ObservableList<Notification> getActiveNotifications() {
        return activeNotifications;
    }

    public void showReminder(String appliance) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        String title = appliance + " Reminder.";
        String description = "The time is " + time + ", time to use the " + appliance + ".";
        activeNotifications.add(new Notification(title, description));
    }

    public void showPopup(String title, String description) {
        activeNotifications.add(new Notification(title, description));
    }

    public void dismissNotification(Notification notification) {
        activeNotifications.remove(notification);
    }

    public void clearAllNotifications() {
        activeNotifications.clear();
    }
}
