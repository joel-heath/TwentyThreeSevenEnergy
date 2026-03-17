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

    private static final String LIGHT_THEME_PATH =
            "/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/light-mode.css";
    private static final String DARK_THEME_PATH =
            "/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/dark-mode.css";
    private static final String LIGHT_CONTRAST_THEME_PATH =
            "/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/high-contrast-light.css";
    private static final String DARK_CONTRAST_THEME_PATH =
            "/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/high-contrast-dark.css";
    private static final String COLORBLIND_PROTAN_PATH =
            "/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/colorblind-protan.css";
    private static final String COLORBLIND_DEUTERAN_PATH =
            "/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/colorblind-deuteran.css";
    private static final String COLORBLIND_TRITAN_PATH =
            "/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/colorblind-tritan.css";
    private static final String COLORBLIND_ACHROMA_PATH =
            "/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/colorblind-achroma.css";

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
                applyTheme(newScene, preferences.getTheme());
                applyColorVision(newScene, preferences.getVision());
            }
        });

        preferences.themeProperty().subscribe((oldTheme, newTheme) -> {
            Scene scene = contentArea.getScene();
            if (scene != null) {
                applyTheme(scene, newTheme);
                applyColorVision(scene, preferences.getVision());
            }
        });

        preferences.visionProperty().subscribe((_, newVision) -> {
            Scene scene = contentArea.getScene();
            if (scene != null)
                applyColorVision(scene, newVision);
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

    private void applyTheme(Scene scene, Theme theme) {
        String light = Objects.requireNonNull(
                RootController.class.getResource(LIGHT_THEME_PATH),
                LIGHT_THEME_PATH + " not found"
        ).toExternalForm();
        String dark = Objects.requireNonNull(
                RootController.class.getResource(DARK_THEME_PATH),
                DARK_THEME_PATH + " not found"
        ).toExternalForm();
        String lightContrast = Objects.requireNonNull(
                RootController.class.getResource(LIGHT_CONTRAST_THEME_PATH),
                LIGHT_CONTRAST_THEME_PATH + " not found"
        ).toExternalForm();
        String darkContrast = Objects.requireNonNull(
                RootController.class.getResource(DARK_CONTRAST_THEME_PATH),
                DARK_CONTRAST_THEME_PATH + " not found"
        ).toExternalForm();

        var stylesheets = scene.getStylesheets();
        stylesheets.remove(light);
        stylesheets.remove(dark);
        stylesheets.remove(lightContrast);
        stylesheets.remove(darkContrast);

        switch (theme) {
            case DARK -> stylesheets.add(dark);
            case LIGHT_CONTRAST -> stylesheets.add(lightContrast);
            case DARK_CONTRAST -> stylesheets.add(darkContrast);
            case LIGHT -> stylesheets.add(light);
            default -> stylesheets.add(light);
        }
    }

    private void applyColorVision(Scene scene, ColorVision vision) {
        String protan = Objects.requireNonNull(
                RootController.class.getResource(COLORBLIND_PROTAN_PATH),
                COLORBLIND_PROTAN_PATH + " not found"
        ).toExternalForm();
        String deuteran = Objects.requireNonNull(
                RootController.class.getResource(COLORBLIND_DEUTERAN_PATH),
                COLORBLIND_DEUTERAN_PATH + " not found"
        ).toExternalForm();
        String tritan = Objects.requireNonNull(
                RootController.class.getResource(COLORBLIND_TRITAN_PATH),
                COLORBLIND_TRITAN_PATH + " not found"
        ).toExternalForm();
        String achroma = Objects.requireNonNull(
                RootController.class.getResource(COLORBLIND_ACHROMA_PATH),
                COLORBLIND_ACHROMA_PATH + " not found"
        ).toExternalForm();

        var stylesheets = scene.getStylesheets();
        stylesheets.remove(protan);
        stylesheets.remove(deuteran);
        stylesheets.remove(tritan);
        stylesheets.remove(achroma);

        if (vision == null)
            return;

        switch (vision) {
            case PROTAN -> stylesheets.add(protan);
            case DEUTERAN -> stylesheets.add(deuteran);
            case TRITAN -> stylesheets.add(tritan);
            case ACHROMA -> stylesheets.add(achroma);
            case TYPICAL -> {
            }
            default -> {
            }
        }
    }
}
