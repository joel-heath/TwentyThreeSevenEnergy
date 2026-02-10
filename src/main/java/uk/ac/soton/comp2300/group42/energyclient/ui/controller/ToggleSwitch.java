package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ToggleSwitch extends ToggleButton {

    public ToggleSwitch() {
        super();
        getStyleClass().add("toggle-switch");
    }

    public ToggleSwitch(String text) {
        super(text);
        getStyleClass().add("toggle-switch");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ToggleSwitchSkin(this);
    }

    private static class ToggleSwitchSkin extends SkinBase<ToggleSwitch> {
        private final StackPane container = new StackPane();
        private final Rectangle track = new Rectangle(50, 28);
        private final Circle thumb = new Circle(11);

        // Transitions for smooth sliding and color fading
        private final TranslateTransition translate = new TranslateTransition(Duration.millis(150), thumb);
        private final FillTransition fill = new FillTransition(Duration.millis(150), track);
        private final ParallelTransition animation = new ParallelTransition(translate, fill);

        public ToggleSwitchSkin(ToggleSwitch control) {
            super(control);

            // Configure Track
            track.setArcWidth(28);
            track.setArcHeight(28);
            track.setFill(control.isSelected() ? Color.web("#3797ef") : Color.web("#b0b0b0"));

            // Configure Thumb
            thumb.setFill(Color.WHITE);
            thumb.setTranslateX(control.isSelected() ? 11 : -11);

            // CRITICAL: Ensure clicks pass through to the ToggleButton logic
            container.setPickOnBounds(true);
            container.getChildren().addAll(track, thumb);
            getChildren().add(container);

            // Handle the click manually to ensure state changes
            container.setOnMouseClicked(e -> control.fire());

            // Listener for state changes (updates animation)
            control.selectedProperty().addListener((obs, oldVal, newVal) -> {
                playAnimation(newVal);
            });
        }

        private void playAnimation(boolean selected) {
            animation.stop();

            // Slide thumb
            translate.setToX(selected ? 11 : -11);

            // Fade track color
            fill.setFromValue((Color) track.getFill());
            fill.setToValue(selected ? Color.web("#3797ef") : Color.web("#b0b0b0"));

            animation.play();
        }
    }
}