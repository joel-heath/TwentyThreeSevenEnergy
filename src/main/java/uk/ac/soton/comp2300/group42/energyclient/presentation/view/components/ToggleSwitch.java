package uk.ac.soton.comp2300.group42.energyclient.presentation.view.components;

import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ColorVisionManager;

public class ToggleSwitch extends ToggleButton {

    private static final int THUMB_RADIUS = 10;
    private static final int WIDTH = 54;
    private static final int HEIGHT = 26;
    private static final Color DISABLED = Color.web("#b0b0b0");
    private static final int DISTANCE = WIDTH / 2 - THUMB_RADIUS - 3;

    public ToggleSwitch() {
        super();
        getStyleClass().add("toggle-switch");
        setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-background-insets: 0;");
        setPrefSize(WIDTH, HEIGHT);
        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
    }

    @Override protected Skin<?> createDefaultSkin() {
        return new ToggleSwitchSkin(this);
    }

    private static class ToggleSwitchSkin extends SkinBase<ToggleSwitch> {

        private final Rectangle track = new Rectangle(WIDTH, HEIGHT);
        private final Circle thumb = new Circle(THUMB_RADIUS);

        private final TranslateTransition translate = new TranslateTransition(Duration.millis(70), thumb);
        private final FillTransition fill = new FillTransition(Duration.millis(70), track);
        private final ParallelTransition animation = new ParallelTransition(translate, fill);

        public ToggleSwitchSkin(ToggleSwitch control) {
            super(control);

            track.setArcWidth(HEIGHT);
            track.setArcHeight(HEIGHT);
            track.setFill(control.isSelected()
                    ? ColorVisionManager.getColor(ColorVisionManager.ColorRole.TOGGLE_ENABLED)
                    : DISABLED);

            thumb.setFill(Color.WHITE);
            thumb.setTranslateX(control.isSelected() ? DISTANCE : -DISTANCE);
            //thumb.setEffect(new javafx.scene.effect.DropShadow(2, Color.gray(0.2)));

            translate.setInterpolator(Interpolator.EASE_IN);
            fill.setInterpolator(Interpolator.EASE_IN);

            StackPane container = new StackPane();
            container.setAlignment(Pos.CENTER);
            container.getChildren().addAll(track, thumb);
            container.setOnMouseClicked(_ -> control.fire());
            getChildren().add(container);

            control.selectedProperty().addListener((_, _, newVal) -> playAnimation(newVal));
            ColorVisionManager.visionProperty().addListener((_, _, _) -> {
                if (control.isSelected())
                    track.setFill(ColorVisionManager.getColor(ColorVisionManager.ColorRole.TOGGLE_ENABLED));
            });
        }

        private void playAnimation(boolean selected) {
            animation.stop();

            translate.setToX(selected ? DISTANCE : -DISTANCE);
            fill.setFromValue((Color) track.getFill());
            fill.setToValue(selected
                    ? ColorVisionManager.getColor(ColorVisionManager.ColorRole.TOGGLE_ENABLED)
                    : DISABLED);

            animation.play();
        }
    }
}
