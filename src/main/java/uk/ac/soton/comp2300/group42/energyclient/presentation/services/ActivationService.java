package uk.ac.soton.comp2300.group42.energyclient.presentation.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Activation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableActivation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.ActivationStore;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Singleton
public class ActivationService {

    private final NotificationService notificationService;
    private final ActivationStore activationStore;

    @Inject
    public ActivationService(NotificationService notificationService, ActivationStore activationStore) {
        this.notificationService = notificationService;
        this.activationStore = activationStore;
        this.notificationService.setOnCleanupAction(this::delete);
    }

    public ObservableList<ObservableActivation> getAll() {
        return activationStore.getAll();
    }

    public CompletableFuture<Void> refreshAllAsync() {
        return activationStore.refreshAllAsync();
    }

    public LocalDateTime create(Activation pojo) {
        ObservableActivation activation = activationStore.add(pojo);
        return notificationService.scheduleNotification(activation);
    }

    public void save(ObservableActivation activation) {
        activationStore.update(activation.commit());
        notificationService.rescheduleNotification(activation);
    }

    public void delete(ObservableActivation activation) {
        activationStore.delete(activation.getId());
        notificationService.cancelNotification(activation);
    }
}
