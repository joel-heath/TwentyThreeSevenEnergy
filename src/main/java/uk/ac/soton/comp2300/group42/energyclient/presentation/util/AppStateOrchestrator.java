package uk.ac.soton.comp2300.group42.energyclient.presentation.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.application.Platform;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.UIExecutor;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Singleton
public class AppStateOrchestrator {

    private static final String LOGGED_OUT_LANDING_PATH = "Landing.fxml";
    private static final String LOGGED_IN_LANDING_PATH = "Dashboard.fxml";

    private final AuthRepository authRepository;
    private final LocalStorageClient localStorageClient;
    private final UserStore userStore;
    private final HouseStore houseStore;
    private final HousemateStore housemateStore;
    private final ApplianceStore applianceStore;
    private final ActivationStore activationStore;
    private final SessionManager sessionManager;
    private final Executor uiExecutor;

    @Inject
    public AppStateOrchestrator(
            AuthRepository authRepository,
            LocalStorageClient localStorageClient,
            UserStore userStore,
            HouseStore houseStore,
            HousemateStore housemateStore,
            ApplianceStore applianceStore,
            ActivationStore activationStore,
            SessionManager sessionManager,
            @UIExecutor Executor uiExecutor) {

        this.authRepository = authRepository;
        this.localStorageClient = localStorageClient;
        this.userStore = userStore;
        this.houseStore = houseStore;
        this.housemateStore = housemateStore;
        this.applianceStore = applianceStore;
        this.activationStore = activationStore;
        this.sessionManager = sessionManager;
        this.uiExecutor = uiExecutor;

        sessionManager.subscribe(this::reloadApplicationState, false);
    }

    public void initialize() {
        boolean loggedIn = authRepository.verifyLoggedIn();
        sessionManager.setLoggedIn(loggedIn);
    }

    public void reloadApplicationState(boolean isLoggedIn) {
        Platform.runLater(() ->
                Navigator.goToIrreversible("LoadingSpinner.fxml")
        );

        houseStore.invalidateCacheAsync()
                .thenCompose(_ -> applianceStore.invalidateCacheAsync())
                .thenCompose(_ -> housemateStore.invalidateCacheAsync())
                .thenCompose(_ -> activationStore.invalidateCacheAsync())

                .thenCompose(_ -> localStorageClient.loadDataAsync())
                .thenCompose(_ -> userStore.refreshAsync())
                .thenCompose(_ -> houseStore.refreshAllAsync())
                .thenCompose(_ -> applianceStore.refreshAllAsync())
                .thenCompose(_ -> {
                    var housemates = housemateStore.refreshAllAsync();
                    var activations = activationStore.refreshAllAsync();
                    return CompletableFuture.allOf(housemates, activations);
                })

                .thenRunAsync(() ->
                    Navigator.goToIrreversible(isLoggedIn ? LOGGED_IN_LANDING_PATH : LOGGED_OUT_LANDING_PATH)
                , uiExecutor)

                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        System.err.println("Failed to load application state: " + ex.getMessage());
                        System.exit(-1);
                    });
                    return null;
                });
    }
}