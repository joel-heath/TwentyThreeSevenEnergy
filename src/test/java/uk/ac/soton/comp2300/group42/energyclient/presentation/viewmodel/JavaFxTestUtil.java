package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

final class JavaFxTestUtil {

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    private JavaFxTestUtil() {
    }

    static void initJavaFx() {
        if (INITIALIZED.compareAndSet(false, true)) {
            CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(latch::countDown);
                await(latch);
            } catch (IllegalStateException _) {
                // JavaFX runtime already initialized by another test run.
            }
        }
    }

    static void waitForFxEvents() {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        await(latch);
    }

    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(2, TimeUnit.SECONDS)) {
                throw new AssertionError("Timed out waiting for JavaFX tasks");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting for JavaFX tasks", e);
        }
    }
}
