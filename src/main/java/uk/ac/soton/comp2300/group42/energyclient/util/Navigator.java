package uk.ac.soton.comp2300.group42.energyclient.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Objects;
import java.util.Stack;

public class Navigator {
    private static Scene mainScene;
    private static final Stack<String> backHistory = new Stack<>();
    private static final Stack<String> forwardHistory = new Stack<>();
    private static final String defaultPath = "/uk/ac/soton/comp2300/group42/energyclient/view/";
    private static final ViewModelFactory vmFactory = new ViewModelFactory();

    private static Parent loadFXML(String fxmlPath) throws IOException {
        URL fxml = Objects.requireNonNull(
                Navigator.class.getResource(fxmlPath),
                fxmlPath + " not found"
        );

        FXMLLoader loader = new FXMLLoader(fxml);

        loader.setControllerFactory(controllerClass -> {
            Object viewModel = vmFactory.getViewModel(controllerClass);
            try {
                return viewModel != null
                        ? controllerClass.getConstructor(viewModel.getClass()).newInstance(viewModel)
                        : controllerClass.getDeclaredConstructor().newInstance();

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException("Failed to instantiate the ViewModel, maybe you need a constructing case in the ViewModelFactory.", e);
            }
        });

        return loader.load();
    }

    public static void initializeAbsolute(String fxmlPath, Stage mainStage) throws IOException {
        if (mainScene != null)
            System.out.println("Warning: You shouldn't call `initialise` more than once");

        Parent root = loadFXML(fxmlPath);
        root.setUserData(fxmlPath);
        mainScene = new Scene(root);

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

    public static void initialize(String fxmlPath, Stage mainStage) throws IOException {
        initializeAbsolute(defaultPath + fxmlPath, mainStage);
    }

    private static void goToAbsoluteNoHistory(String fxmlPath) {
        try {
            Parent newRoot = loadFXML(fxmlPath);
            newRoot.setUserData(fxmlPath);
            mainScene.setRoot(newRoot);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + fxmlPath, e);
        }
    }

    public static void goToAbsoluteIrreversible(String fxmlPath) {
        forwardHistory.clear();
        goToAbsoluteNoHistory(fxmlPath);
    }

    public static void goToAbsolute(String fxmlPath) {
        backHistory.push((String)mainScene.getRoot().getUserData());
        goToAbsoluteIrreversible(fxmlPath);
    }

    public static void goTo(String fxmlPath) {
        goToAbsolute(defaultPath + fxmlPath);
    }

    public static void goToIrreversible(String fxmlPath) {
        goToAbsoluteIrreversible(defaultPath + fxmlPath);
    }

    public static void goBack() {
        if (!backHistory.empty()) {
            forwardHistory.push((String)mainScene.getRoot().getUserData());
            System.out.println(backHistory.peek());
            goToAbsoluteNoHistory(backHistory.pop());
        }
    }

    public static void goForward() {
        if (!forwardHistory.empty()) {
            backHistory.push((String)mainScene.getRoot().getUserData());
            goToAbsoluteNoHistory(forwardHistory.pop());
        }
    }
}
