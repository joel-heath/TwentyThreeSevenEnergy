package uk.ac.soton.comp2300.group42.energyclient.presentation.view.components;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.StackPane;

public class ModalOverlayPane extends StackPane {

    private Modal modal;
    private Node mainContent;
    private final BoxBlur blur = new BoxBlur(10, 10, 3);
    private boolean startedOnBg = false;

    public ModalOverlayPane() {
        getChildren().addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Node node : change.getAddedSubList()) {
                        if (node instanceof Modal)
                            setupModal((Modal) node);
                        else
                            setupMainContentArea(node);
                    }
                }
            }
        });
    }

    private void setupMainContentArea(Node node) {
        if (this.mainContent != null)
            throw new IllegalStateException("ModalOverlayPane may only contain one non-Modal child");

        this.mainContent = node;
    }

    private void setupModal(Modal pane) {
        if (this.modal != null)
            throw new IllegalStateException("Only one Modal can be added to ModalOverlayPane");

        this.modal = pane;
        pane.setOverlayParent(this);
        pane.setVisible(false);
        pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        pane.setOnMousePressed(e -> startedOnBg = (e.getTarget() == pane));
        pane.setOnMouseReleased(e -> {
            if (startedOnBg && e.getTarget() == pane)
                hideModal();
            startedOnBg = false;
        });
    }

    public void showModal() {
        if (modal == null)
            throw new IllegalStateException("No Modal content has been added to this ModalOverlayPane");

        modal.setVisible(true);
        mainContent.setEffect(blur);
    }

    public void hideModal() {
        if (modal == null)
            throw new IllegalStateException("No Modal content has been added to this ModalOverlayPane");

        modal.setVisible(false);
        mainContent.setEffect(null);

        EventHandler<ActionEvent> handler = modal.getOnClose();
        if (handler != null)
            handler.handle(new ActionEvent(this, null));
    }
}