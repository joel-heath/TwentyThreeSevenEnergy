package uk.ac.soton.comp2300.group42.energyclient.presentation.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import uk.ac.soton.comp2300.group42.energyclient.di.AppStateManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.controller.RootController;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Stack;
import java.util.function.Consumer;

public class Navigator {

    // Can't convert to a record class because of the unchecked Consumer<?>
    @SuppressWarnings("ClassCanBeRecord")
    public static class ViewContext {
        final String fxmlPath;
        final Consumer<Object> controllerSetup;
        // could add bool doNotRedirect if wanted to force go to advanced dashboard, even if user preference is simple dashboard

        // We use unchecked casting here because we trust that the Consumer passed
        // matches the Controller of the FXML path.
        @SuppressWarnings("unchecked")
        public ViewContext(String path, Consumer<?> setup) {
            this.fxmlPath = path;
            this.controllerSetup = (Consumer<Object>) setup;
        }
    }

    public static final String DEFAULT_PATH = "/uk/ac/soton/comp2300/group42/energyclient/presentation/view/";

    private static final Stack<ViewContext> backHistory = new Stack<>();
    private static final Stack<ViewContext> forwardHistory = new Stack<>();

    private static RootController rootController;
    private static StackPane contentArea;

    public static <T> Parent loadFXML(ViewContext context) throws IOException {
        URL fxml = Objects.requireNonNull(
                Navigator.class.getResource(context.fxmlPath),
                context.fxmlPath + " not found"
        );

        FXMLLoader loader = new FXMLLoader(fxml);
        loader.setControllerFactory(AppStateManager.getInjector()::getInstance);
        Parent root = loader.load();

        T controller = loader.getController();
        if (context.controllerSetup != null && controller != null)
            context.controllerSetup.accept(controller);

        contentArea.setUserData(context);

        return root;
    }

    public static void initialize(Stage mainStage) throws IOException {
        if (contentArea != null)
            System.out.println("Warning: You shouldn't call `initialize` more than once");

        FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(DEFAULT_PATH + "Root.fxml"));
        loader.setControllerFactory(AppStateManager.getInjector()::getInstance);
        Parent root = loader.load();

        rootController = loader.getController();
        contentArea = rootController.getContentArea();

        Scene rootScene = new Scene(root);
        rootScene.getStylesheets().add(Objects.requireNonNull(
                Navigator.class.getResource("/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/global.css")
        ).toExternalForm());
        rootScene.getStylesheets().add(Objects.requireNonNull(
                Navigator.class.getResource("/uk/ac/soton/comp2300/group42/energyclient/presentation/styles/light-mode.css")
        ).toExternalForm());

        rootScene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            MouseButton button = event.getButton();
            if (button == MouseButton.BACK) {
                goBack();
                event.consume();
            } else if (button == MouseButton.FORWARD) {
                goForward();
                event.consume();
            }
        });

        mainStage.setScene(rootScene);
        AppStateManager.getInjector().getInstance(AppStateOrchestrator.class).initialize();
    }

    private static void switchView(ViewContext context) {
        try {
            Parent newRoot = loadFXML(context);
            contentArea.getChildren().setAll(newRoot);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load view: " + context.fxmlPath, e);
        }
    }

    private static <T> void _goTo(String fxmlPath, Consumer<T> controllerSetup) {
        forwardHistory.clear();
        switchView(new ViewContext(fxmlPath, controllerSetup));
    }

    public static void goToAbsoluteIrreversible(String fxmlPath) { goToAbsoluteIrreversible(fxmlPath, null); }
    public static <T> void goToAbsoluteIrreversible(String fxmlPath, Consumer<T> controllerSetup) {
        backHistory.clear();
        _goTo(fxmlPath, controllerSetup);
    }

    public static void goToAbsolute(String fxmlPath) { goToAbsolute(fxmlPath, null); }
    public static <T> void goToAbsolute(String fxmlPath, Consumer<T> controllerSetup) {
        backHistory.push((ViewContext) contentArea.getUserData());
        _goTo(fxmlPath, controllerSetup);
    }

    public static void goTo(String fxmlPath) { goTo(fxmlPath, null); }
    public static <T> void goTo(String fxmlPath, Consumer<T> controllerSetup) {
        goToAbsolute(DEFAULT_PATH + fxmlPath, controllerSetup);
    }

    public static void goToIrreversible(String fxmlPath) { goToIrreversible(fxmlPath, null); }
    public static <T> void goToIrreversible(String fxmlPath, Consumer<T> controllerSetup) {
        goToAbsoluteIrreversible(DEFAULT_PATH + fxmlPath, controllerSetup);
    }

    public static void goBack() {
        if (!backHistory.empty()) {
            forwardHistory.push((ViewContext) contentArea.getUserData());
            switchView(backHistory.pop());
        }
    }

    public static void goForward() {
        if (!forwardHistory.empty()) {
            backHistory.push((ViewContext) contentArea.getUserData());
            switchView(forwardHistory.pop());
        }
    }

    public static void showPopup(String popupTitle) {
        rootController.showPopup(popupTitle);
    }

    public static void showPopup(String title, String description) { rootController.showPopup(title, description);}
}
