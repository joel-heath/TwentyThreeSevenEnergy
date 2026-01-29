package uk.ac.soton.comp2300.group42.energyclient.viewmodel;

import uk.ac.soton.comp2300.group42.energyclient.model.entity.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ApplianceRepository;
import javafx.collections.ObservableList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleViewModelTest {

    @Mock
    private ApplianceRepository repo;
    @Mock
    private Appliance appliance1;
    @Mock
    private Appliance appliance2;

    @Test
    void testLoadAppliances() {
        List<Appliance> expectedData = List.of(appliance1, appliance2);
        when(repo.findAll()).thenReturn(expectedData);

        ScheduleViewModel vm = new ScheduleViewModel(repo);

        ObservableList<Appliance> list = vm.getApplianceList();
        assertEquals(2, list.size(), "ApplianceList should contain 2 items");
        assertTrue(list.contains(appliance1));
        assertTrue(list.contains(appliance2));

        verify(repo, times(1)).findAll();
    }

    @Test
    void testEmptyRepository() {
        when(repo.findAll()).thenReturn(Collections.emptyList());

        ScheduleViewModel vm = new ScheduleViewModel(repo);

        assertNotNull(vm);
        assertNotNull(vm.getApplianceList());
        assertTrue(vm.getApplianceList().isEmpty(), "ApplianceList should be empty");
    }

    @Test
    void testSelectedApplianceProperty() {
        when(repo.findAll()).thenReturn(Collections.emptyList());
        ScheduleViewModel vm = new ScheduleViewModel(repo);

        vm.selectedApplianceProperty().set(appliance1);

        assertEquals(appliance1, vm.getSelectedAppliance(), "Selected appliance should match the one set");
        assertNotNull(vm.selectedApplianceProperty().get());
    }
}