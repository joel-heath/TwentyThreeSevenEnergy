package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.Modal;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.RootViewModel;
import uk.ac.soton.comp2300.group42.preferences.Theme;

import java.util.Objects;

public class RootController {

    private static final String LIGHT_THEME_PATH = "/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/light-mode.css";
    private static final String DARK_THEME_PATH = "/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/dark-mode.css";
    private static final String LIGHT_CONTRAST_THEME_PATH = "/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/high-contrast-light.css";
    private static final String DARK_CONTRAST_THEME_PATH = "/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/high-contrast-dark.css";

    @FXML private Modal modal;
    @FXML private StackPane contentArea;
    @FXML private ScrollPane reminderScroll;
    @FXML private VBox remindersArea;

    private final RootViewModel vm;
    @Inject public RootController(RootViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        reminderScroll.maxHeightProperty().bind(
                Bindings.min(500, remindersArea.heightProperty().add(40))
        );

        contentArea.sceneProperty().subscribe(newScene -> {
            if (newScene != null)
                applyTheme(newScene, vm.themeProperty().get());
        });

        vm.themeProperty().subscribe((oldTheme, newTheme) -> {
            Scene scene = contentArea.getScene();
            if (scene != null) {
                unapplyTheme(scene, oldTheme);
                applyTheme(scene, newTheme);
            }
        });

        var notifications = vm.getActiveNotifications();
        notifications.subscribe(() ->
                Platform.runLater(() -> {
                    remindersArea.getChildren().setAll(
                            notifications.stream().map(this::createPopup).toList()
                    );

                    if (notifications.isEmpty())
                        modal.close();
                    else
                        modal.show();
                })
        );
    }

    @FXML private void clearReminders() {
        vm.clearAllNotifications();
    }

    public StackPane getContentArea() { return contentArea; }

    private Node createPopup(RootViewModel.Notification notification) {
        VBox card = new VBox();
        card.setStyle("-fx-padding: 10; -fx-border-color: lightgray");

        Label title = new Label(notification.title());
        title.setStyle("-fx-font-weight: bold; -fx-font-scale: large");

        Label description = new Label(notification.description());

        Button dismiss = new Button("Dismiss");
        dismiss.setOnAction(_ -> vm.dismissNotification(notification));

        card.getChildren().addAll(title, description, dismiss);
        return card;
    }

    private String getThemeExternalForm(Theme theme) {
        String path = switch (theme) {
            case LIGHT -> LIGHT_THEME_PATH;
            case DARK -> DARK_THEME_PATH;
            case LIGHT_CONTRAST -> LIGHT_CONTRAST_THEME_PATH;
            case DARK_CONTRAST -> DARK_CONTRAST_THEME_PATH;
        };

        return Objects.requireNonNull(
                RootController.class.getResource(path),
                path + " not found"
        ).toExternalForm();
    }

    private void unapplyTheme(Scene scene, Theme theme) {
        scene.getStylesheets().remove(getThemeExternalForm(theme));
    }

    private void applyTheme(Scene scene, Theme theme) {
        scene.getStylesheets().add(getThemeExternalForm(theme));
    }
}
