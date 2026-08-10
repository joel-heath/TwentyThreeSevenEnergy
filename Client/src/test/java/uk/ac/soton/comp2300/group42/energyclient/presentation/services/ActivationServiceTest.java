package uk.ac.soton.comp2300.group42.energyclient.presentation.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableActivation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.ActivationStore;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ActivationServiceTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ActivationStore activationStore;

    @Captor
    private ArgumentCaptor<Consumer<ObservableActivation>> cleanupActionCaptor;

    private ActivationService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ActivationService(notificationService, activationStore);
    }

    @Test
    void constructorRegistersCleanupAction() {
        then(notificationService).should().setOnCleanupAction(cleanupActionCaptor.capture());

        Consumer<ObservableActivation> registeredAction = cleanupActionCaptor.getValue();
        ObservableActivation mockObservable = mock(ObservableActivation.class);
        given(mockObservable.getId()).willReturn(1L);

        registeredAction.accept(mockObservable);

        then(activationStore).should().delete(1L);
        then(notificationService).should().cancelNotification(mockObservable);
    }

    @Test
    void getAllReturnsStoreList() {
        ObservableList<ObservableActivation> expectedList = FXCollections.observableArrayList();
        given(activationStore.getAll()).willReturn(expectedList);

        ObservableList<ObservableActivation> actualList = underTest.getAll();

        assertSame(expectedList, actualList);
    }

    @Test
    void refreshAllAsyncDelegatesToStore() {
        underTest.refreshAllAsync();

        then(activationStore).should().refreshAllAsync();
    }

    @Test
    void createAddsToStoreAndSchedulesNotification() {
        Activation pojo = mock(Activation.class);
        ObservableActivation observableActivation = mock(ObservableActivation.class);
        LocalDateTime expectedTime = LocalDateTime.of(2026, 1, 1, 12, 0);

        given(activationStore.add(pojo)).willReturn(observableActivation);
        given(notificationService.scheduleNotification(observableActivation)).willReturn(expectedTime);

        LocalDateTime actualTime = underTest.create(pojo);

        assertEquals(expectedTime, actualTime);

        InOrder inOrder = inOrder(activationStore, notificationService);
        inOrder.verify(activationStore).add(pojo);
        inOrder.verify(notificationService).scheduleNotification(observableActivation);
    }

    @Test
    void saveUpdatesStoreAndReschedulesNotification() {
        ObservableActivation observableActivation = mock(ObservableActivation.class);
        Activation pojo = mock(Activation.class);

        given(observableActivation.commit()).willReturn(pojo);

        underTest.save(observableActivation);

        InOrder inOrder = inOrder(observableActivation, activationStore, notificationService);
        inOrder.verify(observableActivation).commit();
        inOrder.verify(activationStore).update(pojo);
        inOrder.verify(notificationService).rescheduleNotification(observableActivation);
    }

    @Test
    void deleteRemovesFromStoreAndCancelsNotification() {
        ObservableActivation observableActivation = mock(ObservableActivation.class);
        given(observableActivation.getId()).willReturn(1L);

        underTest.delete(observableActivation);

        InOrder inOrder = inOrder(activationStore, notificationService);
        inOrder.verify(activationStore).delete(1L);
        inOrder.verify(notificationService).cancelNotification(observableActivation);
    }
}