package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.PreferencesDTO;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.HouseModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.IDoEverything;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SettingsViewModelTest {

    SettingsViewModel vm;
    PreferencesModel preferences;
    @Mock
    IDoEverything mockRepo;
    @Mock HouseModel mockHouse;

    @BeforeEach
    void setUp() {
        preferences = new PreferencesModel(new PreferencesDTO(), mockHouse);
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
