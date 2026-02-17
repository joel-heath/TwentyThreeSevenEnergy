package uk.ac.soton.comp2300.group42.energyclient.ui.view.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;

public class Modal extends StackPane {

    private final ObjectProperty<ModalOverlayPane> overlayParent = new SimpleObjectProperty<>();
    private final ObjectProperty<EventHandler<ActionEvent>> onClose = new SimpleObjectProperty<>();

    public void setOverlayParent(ModalOverlayPane parent) {
        this.overlayParent.set(parent);
    }

    public void show() {
        if (overlayParent.get() == null)
            throw new IllegalStateException("Modal must be added to a ModalOverlayPane to be shown");

        overlayParent.get().showModal();
    }

    public void close() {
        if (overlayParent.get() == null)
            throw new IllegalStateException("Modal must be added to a ModalOverlayPane to be closed");

        overlayParent.get().hideModal();
    }

    public final ObjectProperty<EventHandler<ActionEvent>> onCloseProperty() { return this.onClose; }
    public final void setOnClose(EventHandler<ActionEvent> handler) { this.onCloseProperty().set(handler); }
    public final EventHandler<ActionEvent> getOnClose() { return this.onCloseProperty().get(); }
}