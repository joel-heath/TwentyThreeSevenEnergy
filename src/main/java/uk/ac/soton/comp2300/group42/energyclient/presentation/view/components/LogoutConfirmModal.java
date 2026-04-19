package uk.ac.soton.comp2300.group42.energyclient.presentation.view.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import java.io.IOException;

public class LogoutConfirmModal extends Modal {
    @FXML private Label titleLabel;
    @FXML private Label messageLabel;

    private Runnable onConfirm = () -> {};

    public LogoutConfirmModal() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LogoutConfirmModal.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    public void setOnConfirm(Runnable onConfirm) {
        this.onConfirm = onConfirm != null ? onConfirm : () -> {};
    }

    public void show(String title, String message, Runnable onConfirm) {
        this.onConfirm = onConfirm != null ? onConfirm : () -> {};
        titleLabel.setText(title);
        messageLabel.setText(message);
        super.show();
    }

    @FXML private void onConfirmClicked() {
        close();
        onConfirm.run();
    }

    @FXML private void onCancelClicked() {
        close();
    }
}
