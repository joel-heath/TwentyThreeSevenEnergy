package uk.ac.soton.comp2300.group42.energyclient.presentation.view.components;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class ActivationSchedulePane extends VBox {

    @FXML private ComboBox<ObservableAppliance> applianceSelector;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;

    @FXML private Button recurrenceButton;
    @FXML private VBox recurrenceRules;
    @FXML private VBox oneTimeSchedule;

    @FXML private RadioButton mondayButton;
    @FXML private RadioButton tuesdayButton;
    @FXML private RadioButton wednesdayButton;
    @FXML private RadioButton thursdayButton;
    @FXML private RadioButton fridayButton;
    @FXML private RadioButton saturdayButton;
    @FXML private RadioButton sundayButton;

    @FXML private DatePicker datePicker;

    private IntegerProperty hourProperty;
    private IntegerProperty minuteProperty;

    public ActivationSchedulePane() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ActivationSchedulePane.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    @FXML private void initialize() {
        LocalTime now = LocalTime.now();
        setupSpinner(hourSpinner, 0, 23, now.getHour());
        setupSpinner(minuteSpinner, 0, 59, now.getMinute());

        LocalDate today = LocalDate.now();
        datePicker.setValue(today);
        datePicker.setDayCellFactory(_ -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(today)) {
                    setDisable(true);
                }
            }
        });

        hourProperty = new SimpleIntegerProperty(hourSpinner.getValue());
        minuteProperty = new SimpleIntegerProperty(minuteSpinner.getValue());

        recurrenceButton.textProperty().bind(
                Bindings.when(recurrenceRules.visibleProperty())
                        .then("Disable recurrence")
                        .otherwise("Enable recurrence")
        );
        oneTimeSchedule.visibleProperty().bind(recurrenceRules.visibleProperty().not());

        hourProperty.subscribe(newValue -> hourSpinner.getValueFactory().setValue(newValue.intValue()));
        minuteProperty.subscribe(newValue -> minuteSpinner.getValueFactory().setValue(newValue.intValue()));
        hourSpinner.valueProperty().subscribe(newValue -> {
            if (newValue != null && newValue != hourProperty.get())
                hourProperty.set(newValue);
        });
        minuteSpinner.valueProperty().subscribe(newValue -> {
            if (newValue != null && newValue != minuteProperty.get())
                minuteProperty.set(newValue);
        });
    }

    @FXML private void onRecur() {
        setRecurrenceRulesVisible(!recurrenceRules.isVisible());
    }

    private void setupSpinner(Spinner<Integer> spinner, int min, int max, int initial) {
        var factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initial);
        factory.setWrapAround(true);
        spinner.setValueFactory(factory);
    }

    public void setApplianceList(ObservableList<ObservableAppliance> appliances) { applianceSelector.setItems(appliances); }

    public ObservableAppliance getSelectedAppliance() { return applianceSelector.getValue(); }
    public void setSelectedAppliance(ObservableAppliance appliance) { applianceSelector.setValue(appliance); }
    public ObjectProperty<ObservableAppliance> selectedApplianceProperty() { return applianceSelector.valueProperty(); }

    public int getHour() { return hourSpinner.getValue(); }
    public int getMinute() { return minuteSpinner.getValue(); }
    public IntegerProperty hourProperty() { return hourProperty; }

    public void setHour(int hour) { hourSpinner.getValueFactory().setValue(hour); }
    public void setMinute(int minute) { minuteSpinner.getValueFactory().setValue(minute); }
    public IntegerProperty minuteProperty() { return minuteProperty; }

    public boolean isRecursMonday() { return mondayButton.isSelected(); }
    public boolean isRecursTuesday() { return tuesdayButton.isSelected(); }
    public boolean isRecursWednesday() { return wednesdayButton.isSelected(); }
    public boolean isRecursThursday() { return thursdayButton.isSelected(); }
    public boolean isRecursFriday() { return fridayButton.isSelected(); }
    public boolean isRecursSaturday() { return saturdayButton.isSelected(); }
    public boolean isRecursSunday() { return sundayButton.isSelected(); }

    public void setRecursMonday(boolean value) { mondayButton.setSelected(value); }
    public void setRecursTuesday(boolean value) { tuesdayButton.setSelected(value); }
    public void setRecursWednesday(boolean value) { wednesdayButton.setSelected(value); }
    public void setRecursThursday(boolean value) { thursdayButton.setSelected(value); }
    public void setRecursFriday(boolean value) { fridayButton.setSelected(value); }
    public void setRecursSaturday(boolean value) { saturdayButton.setSelected(value); }
    public void setRecursSunday(boolean value) { sundayButton.setSelected(value); }

    public BooleanProperty recursMondayProperty() { return mondayButton.selectedProperty(); }
    public BooleanProperty recursTuesdayProperty() { return tuesdayButton.selectedProperty(); }
    public BooleanProperty recursWednesdayProperty() { return wednesdayButton.selectedProperty(); }
    public BooleanProperty recursThursdayProperty() { return thursdayButton.selectedProperty(); }
    public BooleanProperty recursFridayProperty() { return fridayButton.selectedProperty(); }
    public BooleanProperty recursSaturdayProperty() { return saturdayButton.selectedProperty(); }
    public BooleanProperty recursSundayProperty() { return sundayButton.selectedProperty(); }

    public Boolean isRecursSet() { return isRecursMonday() || isRecursTuesday() || isRecursWednesday() || isRecursThursday() || isRecursFriday() || isRecursSaturday() || isRecursSunday(); }

    public Boolean isRecurrenceRulesVisible() { return recurrenceRules.isVisible(); }
    public void setRecurrenceRulesVisible(boolean value) { recurrenceRules.setVisible(value); }
    public BooleanProperty recurrenceRulesVisibleProperty() { return recurrenceRules.visibleProperty(); }

    public LocalDate getDate() { return datePicker.getValue(); }
    public void setDate(LocalDate date) { datePicker.setValue(date); }
    public ObjectProperty<LocalDate> dateProperty() { return datePicker.valueProperty(); }
}
