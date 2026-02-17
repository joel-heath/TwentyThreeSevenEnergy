package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.Modal;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RootController {
    private final Repository repository;

    @FXML private Modal modal;
    @FXML private StackPane contentArea;
    @FXML private ScrollPane reminderScroll;
    @FXML private VBox remindersArea;

    public RootController(Repository repository) {
        this.repository = repository;
    }

    @FXML private void initialize() {
        reminderScroll.maxHeightProperty().bind(
                Bindings.min(500, remindersArea.heightProperty().add(40))
        );

        repository.getPreferences().themeProperty().addListener((obs, oldVal, newVal) -> {
            // Logic to switch CSS files on contentArea or Scene
            System.out.println("Theme changed to: " + newVal);
            // applyTheme(newVal);
        });
    }

    @FXML private void clearReminders() {
        remindersArea.getChildren().clear();
        System.out.println("Modal onClose has been triggered");
    }

    public StackPane getContentArea() { return contentArea; }

    public void showPopup(String popupTitle) {
        Node popup = createPopup(popupTitle);

        remindersArea.getChildren().add(popup);

        modal.show();

        reminderScroll.requestLayout();
        reminderScroll.applyCss();
        reminderScroll.layout();
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
                modal.close();
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
