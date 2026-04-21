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
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.RootViewModel;
import uk.ac.soton.comp2300.group42.preferences.Theme;

import java.util.Objects;

public class RootController {
    private static final String THEME_STYLESHEET_PREFIX = "/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/theme-";

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
                applyThemeVariant(newScene, vm.themeProperty().get(), vm.visionProperty().get());
        });

        vm.themeProperty().subscribe((_, _) -> {
            Scene scene = contentArea.getScene();
            if (scene != null)
                applyThemeVariant(scene, vm.themeProperty().get(), vm.visionProperty().get());
        });

        vm.visionProperty().subscribe((_, _) -> {
            Scene scene = contentArea.getScene();
            if (scene != null)
                applyThemeVariant(scene, vm.themeProperty().get(), vm.visionProperty().get());
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

    private String getThemeExternalForm(Theme theme, ColorVision vision) {
        String themePart = switch (theme) {
            case LIGHT -> "light";
            case DARK -> "dark";
            case LIGHT_CONTRAST -> "high-contrast-light";
            case DARK_CONTRAST -> "high-contrast-dark";
        };

        String visionPart = switch (vision == null ? ColorVision.TYPICAL : vision) {
            case TYPICAL -> "typical";
            case PROTAN -> "protan";
            case DEUTERAN -> "deuteran";
            case TRITAN -> "tritan";
            case ACHROMA -> "achroma";
        };

        String path = THEME_STYLESHEET_PREFIX + themePart + "-" + visionPart + ".css";

        return Objects.requireNonNull(
                RootController.class.getResource(path),
                path + " not found"
        ).toExternalForm();
    }

    private void removeAllThemeVariants(Scene scene) {
        scene.getStylesheets().removeIf(stylesheet -> stylesheet.contains("/presentation/styles/theme-"));
    }

    private void applyThemeVariant(Scene scene, Theme theme, ColorVision vision) {
        removeAllThemeVariants(scene);
        scene.getStylesheets().add(getThemeExternalForm(theme, vision));
    }
}
