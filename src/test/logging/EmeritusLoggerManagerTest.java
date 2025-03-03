package test.logging;

import main.logging.EmeritusLoggerManager;
import org.junit.jupiter.api.*;


import main.logging.EmeritusLoggerManager.StringFormatter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class EmeritusLoggerManagerTest {

    private EmeritusLoggerManager loggerManager;

    @BeforeEach
    void setUp() {
        loggerManager = EmeritusLoggerManager.getInstance();
        loggerManager.persistInMemory(true); // Enable in-memory logging
        loggerManager.setLogLevel("DEBUG"); // Ensure all logs are captured
        loggerManager.getPersistenceStore().clear(); // Reset storage before each test
    }

    @Test
    void testInfoLogging() {
        loggerManager.info("TestClass", "This is an info log");
        List<String> logs = loggerManager.getPersistenceStore();

        assertEquals(1, logs.size(), "Should contain one log entry");
        assertTrue(logs.get(0).contains("[INFO] [TestClass] This is an info log"),
                "Log should match expected format");
    }

    @Test
    void testDebugLogging() {
        loggerManager.debug("TestClass", "Debugging {} value", 42);
        List<String> logs = loggerManager.getPersistenceStore();

        assertEquals(1, logs.size(), "Should contain one debug log");
        assertTrue(logs.get(0).contains("[DEBUG] [TestClass] Debugging 42 value"),
                "Should replace {} placeholder correctly");
    }

    @Test
    void testWarnLogging() {
        loggerManager.warn("TestClass", "A warning message");
        List<String> logs = loggerManager.getPersistenceStore();

        assertEquals(1, logs.size(), "Should contain one warning log");
        assertTrue(logs.get(0).contains("[WARN] [TestClass] A warning message"),
                "Warn log should be formatted correctly");
    }

    @Test
    void testErrorLogging() {
        loggerManager.error("TestClass", "Something went wrong: {}", "OutOfMemoryError");
        List<String> logs = loggerManager.getPersistenceStore();

        assertEquals(1, logs.size(), "Should contain one error log");
        assertTrue(logs.get(0).contains("[ERROR] [TestClass] Something went wrong: OutOfMemoryError"),
                "Error log should include formatted message");
    }

    @Test
    void testLogFilteringByLevel() {
        loggerManager.setLogLevel("WARN");

        loggerManager.debug("TestClass", "This should not be logged");
        loggerManager.info("TestClass", "This should also not be logged");
        loggerManager.warn("TestClass", "This should be logged");
        loggerManager.error("TestClass", "This should also be logged");

        List<String> logs = loggerManager.getPersistenceStore();
        assertEquals(2, logs.size(), "Only WARN and ERROR logs should be captured");
    }

    @Test
    void testPlaceholderReplacement() {
        loggerManager.info("TestClass", "Hello, {}! You have {} messages.", "Alice", 5);
        List<String> logs = loggerManager.getPersistenceStore();

        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("[INFO] [TestClass] Hello, Alice! You have 5 messages."),
                "Should correctly replace multiple placeholders");
    }

    @Test
    void testConcurrentLogging() throws InterruptedException {
        int threadCount = 10;
        int logsPerThread = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                for (int j = 0; j < logsPerThread; j++) {
                    loggerManager.info("ThreadTest", "Log entry {}", j);
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        List<String> logs = loggerManager.getPersistenceStore();
        assertEquals(threadCount * logsPerThread, logs.size(), "All log entries should be recorded correctly");
    }

    @Test
    void testFlushDoesNotLoseLogs() {
        loggerManager.info("TestClass", "Before flush log");
        loggerManager.flush();
        loggerManager.info("TestClass", "After flush log");

        List<String> logs = loggerManager.getPersistenceStore();
        assertEquals(2, logs.size(), "Flush should not remove logs");
    }

    @Test
    void testSinglePlaceholderReplacement() {
        String result = EmeritusLoggerManager.StringFormatter.format("Hello, {}!", "Alice");
        assertEquals("Hello, Alice!", result, "Single placeholder should be replaced correctly.");
    }

    @Test
    void testMultiplePlaceholderReplacements() {
        String result = EmeritusLoggerManager.StringFormatter.format("{} is {} years old and has {} pets.", "Bob", 30, 2);
        assertEquals("Bob is 30 years old and has 2 pets.", result, "Multiple placeholders should be replaced correctly.");
    }

    @Test
    void testExtraArgumentsAreIgnored() {
        String result = EmeritusLoggerManager.StringFormatter.format("Hello, {}!", "Charlie", "extraArg");
        assertEquals("Hello, Charlie!", result, "Extra arguments should be ignored.");
    }

    @Test
    void testMissingArgumentsLeavePlaceholder() {
        String result = EmeritusLoggerManager.StringFormatter.format("This is {} and this is {}.", "onlyOneArg");
        assertEquals("This is onlyOneArg and this is {}.", result, "Missing arguments should leave placeholders.");
    }

    @Test
    void testNullArgumentsAreHandled() {
        String result = EmeritusLoggerManager.StringFormatter.format("This is a {} test.", (Object) null);
        assertEquals("This is a null test.", result, "Null values should be converted to 'null'.");
    }

    @Test
    void testNoPlaceholdersReturnsOriginalString() {
        String result = EmeritusLoggerManager.StringFormatter.format("No placeholders here.");
        assertEquals("No placeholders here.", result, "String without placeholders should remain unchanged.");
    }

    @Test
    void testEmptyStringInput() {
        String result = EmeritusLoggerManager.StringFormatter.format("");
        assertEquals("", result, "Empty input string should return empty.");
    }

    @Test
    void testOnlyPlaceholders() {
        String result = EmeritusLoggerManager.StringFormatter.format("{} {} {}", 1, 2, 3);
        assertEquals("1 2 3", result, "Should replace all placeholders with arguments.");
    }

    @Test
    void testConsecutivePlaceholders() {
        String result = EmeritusLoggerManager.StringFormatter.format("Hello {}{}!", "A", "B");
        assertEquals("Hello AB!", result, "Consecutive placeholders should be replaced correctly.");
    }

    @Test
    void testPlaceholderAtStart() {
        String result = EmeritusLoggerManager.StringFormatter.format("{} is at the start.", "This");
        assertEquals("This is at the start.", result, "Placeholder at the beginning should be replaced correctly.");
    }

    @Test
    void testPlaceholderAtEnd() {
        String result = EmeritusLoggerManager.StringFormatter.format("Ends with {}.", "this");
        assertEquals("Ends with this.", result, "Placeholder at the end should be replaced correctly.");
    }
}