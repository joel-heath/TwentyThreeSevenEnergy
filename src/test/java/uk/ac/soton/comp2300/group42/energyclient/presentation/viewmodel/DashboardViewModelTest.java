package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.Test;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DashboardViewModelTest {

    @Test
    void getPreferredMode_reflectsUnderlyingPreferencesProperty() {
        ObservablePreferences preferences = mock(ObservablePreferences.class);
        Mode initialMode = Mode.values()[0];
        Mode updatedMode = Mode.values()[Mode.values().length - 1];
        ObjectProperty<Mode> modeProperty = new SimpleObjectProperty<>(initialMode);
        when(preferences.modeProperty()).thenReturn(modeProperty);

        DashboardViewModel viewModel = new DashboardViewModel(preferences);

        assertEquals(initialMode, viewModel.getPreferredMode());
        modeProperty.set(updatedMode);
        assertEquals(updatedMode, viewModel.getPreferredMode());
    }
}
