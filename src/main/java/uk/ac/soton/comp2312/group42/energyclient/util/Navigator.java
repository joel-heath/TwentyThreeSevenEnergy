package uk.ac.soton.comp2312.group42.energyclient.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Stack;

public class Navigator {
    private static Scene mainScene;
    private static final Stack<Parent> backHistory = new Stack<>();
    private static final Stack<Parent> forwardHistory = new Stack<>();

    private static Parent loadFXML(String fxmlPath) throws IOException {
        URL fxml = Objects.requireNonNull(
                Navigator.class.getResource(fxmlPath),
                fxmlPath + " not found"
        );

        return FXMLLoader.load(fxml);
    }

    public static void initialize(String fxmlPath, Stage mainStage) throws IOException {
        if (mainScene != null)
            System.out.println("Warning: You shouldn't call `initialise` more than once");

        mainScene = new Scene(loadFXML(fxmlPath));
        mainScene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            MouseButton button = event.getButton();
            if (button == MouseButton.BACK) {
                goBack();
                event.consume();
            }
            else if (button == MouseButton.FORWARD) {
                goForward();
                event.consume();
            }
        });

        mainStage.setScene(mainScene);
    }

    public static void goTo(String fxmlPath) {
        try {
            Parent newRoot = loadFXML(fxmlPath);
            backHistory.push(mainScene.getRoot());
            forwardHistory.clear();
            mainScene.setRoot(newRoot);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + fxmlPath, e);
        }
    }

    public static void goBack() {
        if (!backHistory.empty()) {
            forwardHistory.push(mainScene.getRoot());
            mainScene.setRoot(backHistory.pop());
        }
    }

    public static void goForward() {
        if (!forwardHistory.empty()) {
            backHistory.push(mainScene.getRoot());
            mainScene.setRoot(forwardHistory.pop());
        }
    }
}
