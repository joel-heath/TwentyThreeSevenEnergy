package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.Modal;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Theme;

import java.util.Objects;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RootController {

    private static final String THEME_PATH_PREFIX =
            "/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/theme-";

    private final ObservablePreferences preferences;

    @FXML private Modal modal;
    @FXML private StackPane contentArea;
    @FXML private ScrollPane reminderScroll;
    @FXML private VBox remindersArea;

    @Inject public RootController(ObservablePreferences preferences) {
        this.preferences = preferences;
    }

    @FXML private void initialize() {
        reminderScroll.maxHeightProperty().bind(
                Bindings.min(500, remindersArea.heightProperty().add(40))
        );

        contentArea.sceneProperty().addListener((_, _, newScene) -> {
            if (newScene != null) {
                applyThemeAndVision(newScene, preferences.getTheme(), preferences.getVision());
            }
        });

        preferences.themeProperty().subscribe((oldTheme, newTheme) -> {
            Scene scene = contentArea.getScene();
            if (scene != null) {
                applyThemeAndVision(scene, newTheme, preferences.getVision());
            }
        });

        preferences.visionProperty().subscribe((_, newVision) -> {
            Scene scene = contentArea.getScene();
            if (scene != null)
                applyThemeAndVision(scene, preferences.getTheme(), newVision);
        });
    }

    @FXML private void clearReminders() {
        remindersArea.getChildren().clear();
        System.out.println("Modal onClose has been triggered");
    }

    public StackPane getContentArea() { return contentArea; }

    public void showPopup(String popupTitle) {
        Node popup = createReminderPopup(popupTitle);

        remindersArea.getChildren().add(popup);

        modal.show();

        reminderScroll.requestLayout();
        reminderScroll.applyCss();
        reminderScroll.layout();
    }

    public void showPopup(String title, String description) {
        Node popup = createPopup(title, description);

        remindersArea.getChildren().add(popup);

        modal.show();

        reminderScroll.requestLayout();
        reminderScroll.applyCss();
        reminderScroll.layout();
    }

    private Node createReminderPopup(String appliance) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        String title = appliance + " Reminder.";
        String description = "The time is " + time + ", time to use the " + appliance + ".";
        return createPopup(title, description);
    }

    private Node createPopup(String titleText, String descriptionText) {
        VBox card = new VBox();

        Button dismiss = new Button("Dismiss");
        dismiss.setOnAction(_ -> {
            remindersArea.getChildren().remove(card);
            reminderScroll.requestLayout();
            reminderScroll.applyCss();
            reminderScroll.layout();
            if (remindersArea.getChildren().isEmpty())
                modal.close();
        });

        Label title = new Label(titleText);
        title.setStyle("-fx-font-weight: bold; -fx-font-scale: large");

        Label description = new Label(descriptionText);

        card.getStyleClass().add("popup-card");
        card.getChildren().addAll(title, description, dismiss);
        return card;
    }

    private void applyThemeAndVision(Scene scene, Theme theme, ColorVision vision) {
        Theme safeTheme = theme == null ? Theme.LIGHT : theme;
        ColorVision safeVision = vision == null ? ColorVision.TYPICAL : vision;
        String themeKey = switch (safeTheme) {
            case DARK -> "dark";
            case LIGHT_CONTRAST -> "high-contrast-light";
            case DARK_CONTRAST -> "high-contrast-dark";
            case LIGHT -> "light";
        };
        String visionKey = switch (safeVision) {
            case PROTAN -> "protan";
            case DEUTERAN -> "deuteran";
            case TRITAN -> "tritan";
            case ACHROMA -> "achroma";
            case TYPICAL -> "typical";
        };
        String path = THEME_PATH_PREFIX + themeKey + "-" + visionKey + ".css";
        String stylesheet = Objects.requireNonNull(
                RootController.class.getResource(path),
                path + " not found"
        ).toExternalForm();

        var stylesheets = scene.getStylesheets();
        stylesheets.removeIf(entry -> entry.contains("/styles/theme-"));
        stylesheets.add(stylesheet);
    }
}
