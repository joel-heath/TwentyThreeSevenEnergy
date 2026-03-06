package uk.ac.soton.comp2300.group42.energyclient.presentation.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.activation.ActivationType;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableActivation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private Executor uiExecutor;

    @Mock
    private ObservableActivation mockActivation;

    @Mock
    private ObservableAppliance mockAppliance;

    @Mock
    private Consumer<ObservableActivation> cleanupAction;

    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    private NotificationService underTest;
    private MockedStatic<Navigator> navigatorMock;

    @BeforeEach
    void setUp() {
        underTest = new NotificationService(uiExecutor);
        
        navigatorMock = mockStatic(Navigator.class);
    }

    @AfterEach
    void tearDown() {
        navigatorMock.close();
    }

    @Test
    void showPopupStringIgnoresBlank() {
        underTest.showPopup(null);
        underTest.showPopup("  ");

        then(uiExecutor).shouldHaveNoInteractions();
    }

    @Test
    void showPopupStringExecutes() {
        underTest.showPopup("Hello World");

        then(uiExecutor).should().execute(runnableCaptor.capture());

        runnableCaptor.getValue().run();

        navigatorMock.verify(() -> Navigator.showPopup("Hello World"));
    }

    @Test
    void showPopupStringStringExecutes() {
        underTest.showPopup("Warning", null);

        then(uiExecutor).should().execute(runnableCaptor.capture());

        runnableCaptor.getValue().run();

        navigatorMock.verify(() -> Navigator.showPopup("Warning", ""));
    }

    @Test
    void scheduleNotificationOneOff() {
        given(mockActivation.getAppliance()).willReturn(mockAppliance);
        given(mockAppliance.getName()).willReturn("Oven");
        given(mockActivation.getActivationType()).willReturn(ActivationType.NON_RECURRING);
        
        LocalDateTime immediateTime = LocalDateTime.now().minusDays(1);
        given(mockActivation.getNextActivationDateTime()).willReturn(immediateTime);

        underTest.setOnCleanupAction(cleanupAction);

        LocalDateTime resultTime = underTest.scheduleNotification(mockActivation);

        assertEquals(immediateTime, resultTime);

        then(uiExecutor).should(timeout(1000)).execute(runnableCaptor.capture());

        runnableCaptor.getValue().run();

        navigatorMock.verify(() -> Navigator.showPopup("Oven"));
        then(cleanupAction).should().accept(mockActivation);
        then(mockActivation).should(never()).triggerUpdate();
    }

    @Test
    void scheduleNotificationRecurring() {
        given(mockActivation.getAppliance()).willReturn(mockAppliance);
        given(mockAppliance.getName()).willReturn("Washing Machine");
        given(mockActivation.getActivationType()).willReturn(ActivationType.RECURRING);

        // First call forces immediate execution (delay 0)
        // Second call (when it reschedules itself) goes to the future to avoid an infinite loop
        LocalDateTime immediateTime = LocalDateTime.now().minusDays(1);
        LocalDateTime futureTime = LocalDateTime.now().plusDays(1);
        given(mockActivation.getNextActivationDateTime())
                .willReturn(immediateTime)
                .willReturn(futureTime);

        underTest.scheduleNotification(mockActivation);

        then(uiExecutor).should(timeout(1000)).execute(runnableCaptor.capture());
        
        runnableCaptor.getValue().run();

        navigatorMock.verify(() -> Navigator.showPopup("Washing Machine"));
        then(mockActivation).should().triggerUpdate();

        then(mockActivation).should(times(2)).getNextActivationDateTime();
    }

    @Test
    void cancelNotificationRemovesTask() throws Exception {
        given(mockActivation.getAppliance()).willReturn(mockAppliance);
        
        given(mockActivation.getNextActivationDateTime()).willReturn(LocalDateTime.now().plusDays(1));
        underTest.scheduleNotification(mockActivation);

        underTest.cancelNotification(mockActivation);

        Hashtable<?, ?> tasks = getInternalTimerTasksMap();
        assertTrue(tasks.isEmpty(), "Timer task should be removed from the map");
    }

    @Test
    void rescheduleNotificationReplacesTask() throws Exception {
        given(mockActivation.getAppliance()).willReturn(mockAppliance);
        given(mockActivation.getNextActivationDateTime()).willReturn(LocalDateTime.now().plusDays(1));
        
        underTest.scheduleNotification(mockActivation);
        Object firstTask = getInternalTimerTasksMap().get(mockActivation);

        underTest.rescheduleNotification(mockActivation);

        Object secondTask = getInternalTimerTasksMap().get(mockActivation);
        
        assertNotNull(secondTask, "A new task should have been mapped");
        assertNotSame(firstTask, secondTask, "The new TimerTask instance should be different from the original");
    }

    private Hashtable<?, ?> getInternalTimerTasksMap() throws Exception {
        Field field = NotificationService.class.getDeclaredField("timerTasks");
        field.setAccessible(true);
        return (Hashtable<?, ?>) field.get(underTest);
    }
}