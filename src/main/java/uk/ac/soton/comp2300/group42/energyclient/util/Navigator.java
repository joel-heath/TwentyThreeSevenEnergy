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
import java.util.function.Consumer;

public class Navigator {
    // Can't convert to a record class because of the unchecked Consumer<?>
    @SuppressWarnings("ClassCanBeRecord")
    private static class ViewContext {
        final String fxmlPath;
        final Consumer<Object> controllerSetup;

        // We use unchecked casting here because we trust that the Consumer passed
        // matches the Controller of the FXML path.
        @SuppressWarnings("unchecked")
        ViewContext(String path, Consumer<?> setup) {
            this.fxmlPath = path;
            this.controllerSetup = (Consumer<Object>) setup;
        }
    }

    private static Scene mainScene;
    private static final Stack<ViewContext> backHistory = new Stack<>();
    private static final Stack<ViewContext> forwardHistory = new Stack<>();
    private static final String defaultPath = "/uk/ac/soton/comp2300/group42/energyclient/view/";
    private static final ViewModelFactory vmFactory = new ViewModelFactory();

    private static <T> Parent loadFXML(ViewContext context) throws IOException {
        URL fxml = Objects.requireNonNull(
                Navigator.class.getResource(context.fxmlPath),
                context.fxmlPath + " not found"
        );

        FXMLLoader loader = new FXMLLoader(fxml);

        loader.setControllerFactory(controllerClass -> {
            Object viewModel = vmFactory.getViewModel(controllerClass);
            try {
                return viewModel != null
                        ? controllerClass.getConstructor(viewModel.getClass()).newInstance(viewModel)
                        : controllerClass.getDeclaredConstructor().newInstance();

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException("Failed to instantiate the ViewModel, maybe you need a constructing case in the ViewModelFactory.", e);
            }
        });

        Parent root = loader.load();

        T controller = loader.getController();
        if (context.controllerSetup != null && controller != null)
            context.controllerSetup.accept(controller);

        root.setUserData(context);

        return root;
    }

    public static void initializeAbsolute(String fxmlPath, Stage mainStage) throws IOException { initializeAbsolute(fxmlPath, mainStage, null); }
    public static <T> void initializeAbsolute(String fxmlPath, Stage mainStage, Consumer<T> controllerSetup) throws IOException {
        if (mainScene != null)
            System.out.println("Warning: You shouldn't call `initialise` more than once");

        Parent root = loadFXML(new ViewContext(fxmlPath, controllerSetup));
        mainScene = new Scene(root);

        mainScene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            MouseButton button = event.getButton();
            if (button == MouseButton.BACK) {
                goBack();
                event.consume();
            } else if (button == MouseButton.FORWARD) {
                goForward();
                event.consume();
            }
        });

        mainStage.setScene(mainScene);
    }

    public static void initialize(String fxmlPath, Stage mainStage) throws IOException { initialize(fxmlPath, mainStage, null); }
    public static <T> void initialize(String fxmlPath, Stage mainStage, Consumer<T> controllerSetup) throws IOException {
        initializeAbsolute(defaultPath + fxmlPath, mainStage, controllerSetup);
    }

    private static void switchView(ViewContext context) {
        try {
            Parent newRoot = loadFXML(context);
            mainScene.setRoot(newRoot);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + context.fxmlPath, e);
        }
    }

    public static void goToAbsoluteIrreversible(String fxmlPath) { goToAbsoluteIrreversible(fxmlPath, null); }
    public static <T> void goToAbsoluteIrreversible(String fxmlPath, Consumer<T> controllerSetup) {
        forwardHistory.clear();
        switchView(new ViewContext(fxmlPath, controllerSetup));
    }

    public static void goToAbsolute(String fxmlPath) { goToAbsolute(fxmlPath, null); }
    public static <T> void goToAbsolute(String fxmlPath, Consumer<T> controllerSetup) {
        backHistory.push((ViewContext) mainScene.getRoot().getUserData());
        goToAbsoluteIrreversible(fxmlPath, controllerSetup);
    }

    public static void goTo(String fxmlPath) { goTo(fxmlPath, null); }
    public static <T> void goTo(String fxmlPath, Consumer<T> controllerSetup) {
        goToAbsolute(defaultPath + fxmlPath, controllerSetup);
    }

    public static void goToIrreversible(String fxmlPath) { goToIrreversible(fxmlPath, null); }
    public static <T> void goToIrreversible(String fxmlPath, Consumer<T> controllerSetup) {
        goToAbsoluteIrreversible(defaultPath + fxmlPath, controllerSetup);
    }

    public static void goBack() {
        if (!backHistory.empty()) {
            forwardHistory.push((ViewContext) mainScene.getRoot().getUserData());
            switchView(backHistory.pop());
        }
    }

    public static void goForward() {
        if (!forwardHistory.empty()) {
            backHistory.push((ViewContext) mainScene.getRoot().getUserData());
            switchView(forwardHistory.pop());
        }
    }
}
