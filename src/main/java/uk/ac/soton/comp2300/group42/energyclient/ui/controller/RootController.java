package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RootController {
    private final Repository repository;

    @FXML private StackPane contentArea;
    @FXML private StackPane popupArea;
    @FXML private ScrollPane reminderScroll;
    @FXML private VBox remindersArea;

    private final BoxBlur blur = new BoxBlur(10, 10, 3);
    private boolean startedOnBg = false;

    public RootController(Repository repository) {
        this.repository = repository;
    }

    @FXML private void initialize() {
        popupArea.setVisible(false);
        popupArea.setOnMousePressed(e -> startedOnBg = (e.getTarget() == popupArea));
        popupArea.setOnMouseReleased(e -> {
            if (startedOnBg && e.getTarget() == popupArea)
                closePopup();
            startedOnBg = false;
        });
        reminderScroll.maxHeightProperty().bind(
                Bindings.min(500, remindersArea.heightProperty().add(40))
        );

        repository.getPreferences().themeProperty().addListener((obs, oldVal, newVal) -> {
            // Logic to switch CSS files on contentArea or Scene
            System.out.println("Theme changed to: " + newVal);
            // applyTheme(newVal);
        });
    }

    public StackPane getContentArea() { return contentArea; }

    public void showPopup(String popupTitle) {
        Node popup = createPopup(popupTitle);

        remindersArea.getChildren().add(popup);

        contentArea.setEffect(blur);
        popupArea.setVisible(true);

        reminderScroll.requestLayout();
        reminderScroll.applyCss();
        reminderScroll.layout();
    }

    public void closePopup() {
        remindersArea.getChildren().clear();

        contentArea.setEffect(null);
        popupArea.setVisible(false);
    }

    private Node createPopup(String appliance) {
        VBox card = new VBox();

        Button dismiss = new Button("Dismiss");
        dismiss.setOnAction(_ -> {
            remindersArea.getChildren().remove(card);
            reminderScroll.requestLayout();
            reminderScroll.applyCss();
            reminderScroll.layout();
            if (remindersArea.getChildren().isEmpty())
                closePopup();
        });

        Label title = new Label(appliance + " Reminder.");
        title.setStyle("-fx-font-weight: bold; -fx-font-scale: large");

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        Label description = new Label("The time is " + time + ", time to use the " + appliance + ".");

        card.setStyle("-fx-padding: 10; -fx-border-color: lightgray;");
        card.getChildren().addAll(title, description, dismiss);
        return card;
    }
}
