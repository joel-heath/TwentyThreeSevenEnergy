package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.PreferencesDTO;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SettingsViewModelTest {

    SettingsViewModel vm;
    ObservablePreferences preferences;
    @Mock
    IDoEverything mockRepo;
    @Mock
    ObservableHouse mockHouse;

    @BeforeEach
    void setUp() {
        preferences = new ObservablePreferences(new PreferencesDTO(), mockHouse);
        //preferences.setVision(ColorVision.TYPICAL);

        when(mockRepo.getPreferences()).thenReturn(preferences);

        vm = new SettingsViewModel(mockRepo);

    }

    @Test
    void testDefaultColorVision() {
        assertEquals(ColorVision.TYPICAL, mockRepo.getPreferences().getVision());
    }

    @Test
    void testChangeColorVision() {
        ColorVision newVision = ColorVision.PROTAN;
        mockRepo.getPreferences().setVision(newVision);
        assertEquals(ColorVision.PROTAN, mockRepo.getPreferences().getVision());
    }



}
