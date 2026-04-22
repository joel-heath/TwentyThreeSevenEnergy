package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LandingViewModelTest {

    @Test
    void isDebugModeProperty_isAvailableAndStable() {
        LandingViewModel viewModel = new LandingViewModel();

        assertNotNull(viewModel.isDebugModeProperty());
        boolean initial = viewModel.isDebugModeProperty().get();
        assertEquals(initial, viewModel.isDebugModeProperty().get());
    }
}
