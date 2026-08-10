package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.AlertModal;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.RootViewModel;
import uk.ac.soton.comp2300.group42.preferences.Theme;

import java.util.Objects;

public class RootController {
    private static final String THEME_STYLESHEET_PREFIX = "/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/theme-";

    @FXML private StackPane contentArea;
    @FXML private AlertModal alertModal;

    private final RootViewModel vm;
    @Inject public RootController(RootViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
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
                    if (!notifications.isEmpty()) {
                        RootViewModel.Notification notification = notifications.get(0);
                        alertModal.show(
                                notification.title(),
                                notification.description(),
                                () -> vm.dismissNotification(notification)
                        );
                    }
                })
        );
    }


    public StackPane getContentArea() { return contentArea; }

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
