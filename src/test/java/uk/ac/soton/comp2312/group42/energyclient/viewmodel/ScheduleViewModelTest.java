package uk.ac.soton.comp2312.group42.energyclient.viewmodel;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScheduleViewModelTest {

    @Test
    void testInitialization() {
        ScheduleViewModel vm = new ScheduleViewModel();
        assertNotNull(vm, "ViewModel should initialize without errors");
    }

    // TODO: Add tests here once scheduling logic is added
}