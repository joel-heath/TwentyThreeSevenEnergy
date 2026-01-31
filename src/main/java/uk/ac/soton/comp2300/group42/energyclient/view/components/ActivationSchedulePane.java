package uk.ac.soton.comp2300.group42.energyclient.view.components;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Appliance;
import java.io.IOException;
import java.time.LocalTime;

public class ActivationSchedulePane extends VBox {
    @FXML private ComboBox<Appliance> applianceSelector;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;

    public ActivationSchedulePane()  {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("activationSchedulePane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML private void initialize() {
        LocalTime now = LocalTime.now();
        setupSpinner(hourSpinner, 0, 23, now.getHour());
        setupSpinner(minuteSpinner, 0, 59, now.getMinute());
    }

    private void setupSpinner(Spinner<Integer> spinner, int min, int max, int initial) {
        var factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initial);
        factory.setWrapAround(true);
        spinner.setValueFactory(factory);
    }

    public void setApplianceList(ObservableList<Appliance> appliances) { applianceSelector.setItems(appliances); }
    public ObjectProperty<Appliance> selectedApplianceProperty() { return applianceSelector.valueProperty(); }
    public Appliance getSelectedAppliance() { return applianceSelector.getValue(); }
    public int getHour() { return hourSpinner.getValue(); }
    public int getMinute() { return minuteSpinner.getValue(); }
    public void setHour(int hour) { hourSpinner.getValueFactory().setValue(hour); }
    public void setMinute(int minute) { minuteSpinner.getValueFactory().setValue(minute); }
}
