package uk.ac.soton.comp2300.group42.energyclient.presentation.view.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.presentation.model.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.EnergyUsageViewModel;

import java.io.IOException;

public class EnergyUsageWidget extends VBox {
    private static final String WIDGET_STYLE =
            "-fx-background-radius: 10; -fx-padding: 15; -fx-font-weight: bold";

    @FXML private EnergyUsageRect energyUsageRect;
    @FXML private Label totalSpentLabel;
    @FXML private Label costLabel;
    @FXML private Label goalLabel;

    public void bindComponents(EnergyUsageViewModel vm) {
        costLabel.textProperty().unbind();
        goalLabel.textProperty().unbind();
        energyUsageRect.usageProperty().unbind();
        energyUsageRect.fillProperty().unbind();

        costLabel.textProperty().bind(vm.costMessageProperty());
        goalLabel.textProperty().bind(vm.goalMessageProperty());
        energyUsageRect.usageProperty().bind(vm.usageProperty());
        energyUsageRect.fillProperty().bind(
                vm.getPreferences().visionProperty().map(ColorVisionManager::getGradientFor)
        );
    }

    public EnergyUsageWidget() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EnergyUsageWidget.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        bindWidgetStyles();
    }

    private void bindWidgetStyles() {
        styleProperty().bind(ColorVisionManager.visionProperty().map(
                vision -> "-fx-background-color: " + ColorVisionManager.getWebColor(
                        vision, ColorVisionManager.ColorRole.WIDGET_SURFACE
                ) + "; " + WIDGET_STYLE
        ));

        totalSpentLabel.textFillProperty().bind(ColorVisionManager.visionProperty().map(
                vision -> ColorVisionManager.getColor(vision, ColorVisionManager.ColorRole.WIDGET_TEXT)
        ));
        costLabel.textFillProperty().bind(ColorVisionManager.visionProperty().map(
                vision -> ColorVisionManager.getColor(vision, ColorVisionManager.ColorRole.WIDGET_TEXT)
        ));
        goalLabel.textFillProperty().bind(ColorVisionManager.visionProperty().map(
                vision -> ColorVisionManager.getColor(vision, ColorVisionManager.ColorRole.WIDGET_TEXT)
        ));
    }
}
