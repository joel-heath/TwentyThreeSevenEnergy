package uk.ac.soton.comp2300.group42.energyclient.presentation.view.components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import java.io.IOException;

public class AlertModal extends Modal{
    @FXML private Label titleLabel;
    @FXML private Label messageLabel;

    private Runnable onOk = () -> {};

    public AlertModal() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AlertModal.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    //public void setOnOk(Runnable onOk) { this.onOk = onOk != null ? onOk : () -> {}; }

    public void show(String title, String message) {
        titleLabel.setText(title);
        messageLabel.setText(message);
        super.show();
    }

    public void show(String title, String message, Runnable onOk) {
        this.onOk = onOk != null ? onOk : () -> {};
        titleLabel.setText(title);
        messageLabel.setText(message);
        super.show();
    }

    @FXML private void onOkClicked() {
        close();
        onOk.run();
    }
}
