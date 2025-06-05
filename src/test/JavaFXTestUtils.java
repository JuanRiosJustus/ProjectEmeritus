package test;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class JavaFXTestUtils {

    public static void runAndWait(Runnable runnable) throws InterruptedException {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                runnable.run();
            } finally {
                latch.countDown();
            }
        });
        latch.await(); // Wait for task to finish
    }

    public static <T> T runAndWaitWithResult(java.util.concurrent.Callable<T> callable) throws Exception {
        if (Platform.isFxApplicationThread()) {
            return callable.call();
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Exception> exception = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                result.set(callable.call());
            } catch (Exception e) {
                exception.set(e);
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        if (exception.get() != null) {
            throw exception.get();
        }
        return result.get();
    }
}