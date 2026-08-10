package uk.ac.soton.comp2300.group42.energyclient.presentation.view.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import java.io.IOException;

public class AlertModal extends Modal {

    @FXML private Label titleLabel;
    @FXML private Label messageLabel;

    @FXML private void onOkClicked() {
        close();
    }

    public AlertModal() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AlertModal.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    public void show(String title, String message) {
        titleLabel.setText(title);
        messageLabel.setText(message);
        super.show();
    }

    public void show(String title, String message, Runnable onOk) {
        super.setOnClose(_ -> onOk.run());
        titleLabel.setText(title);
        messageLabel.setText(message);
        super.show();
    }
}
