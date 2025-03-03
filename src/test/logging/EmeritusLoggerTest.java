package test.logging;

import main.logging.EmeritusLogger;
import main.logging.EmeritusLoggerManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmeritusLoggerTest {
    private EmeritusLogger logger;
    private EmeritusLoggerManager loggerManager;

    @BeforeEach
    void setUp() {
        loggerManager = EmeritusLoggerManager.getInstance();
        loggerManager.persistInMemory(true); // Enable in-memory logging
        loggerManager.setLogLevel("DEBUG"); // Ensure all logs are captured
        loggerManager.getPersistenceStore().clear(); // Reset logs before each test

        logger = EmeritusLogger.create(EmeritusLoggerTest.class);
    }

    @Test
    void testLoggerCreation() {
        EmeritusLogger testLogger = EmeritusLogger.create(String.class);
        assertNotNull(testLogger, "Logger instance should not be null");
    }

    @Test
    void testInfoLogging() {
        logger.info("This is an info log");
        List<String> logs = loggerManager.getPersistenceStore();

        assertEquals(1, logs.size(), "Should contain one log entry");
        assertTrue(logs.get(0).contains("[INFO] [EmeritusLoggerTest] This is an info log"),
                "Log format should match expected output");
    }

    @Test
    void testDebugLogging() {
        logger.debug("Debugging {} value", 42);
        List<String> logs = loggerManager.getPersistenceStore();

        assertEquals(1, logs.size(), "Should contain one debug log");
        assertTrue(logs.get(0).contains("[DEBUG] [EmeritusLoggerTest] Debugging 42 value"),
                "Should correctly replace {} placeholder");
    }

    @Test
    void testWarnLogging() {
        logger.warn("This is a warning message");
        List<String> logs = loggerManager.getPersistenceStore();

        assertEquals(1, logs.size(), "Should contain one warning log");
        assertTrue(logs.get(0).contains("[WARN] [EmeritusLoggerTest] This is a warning message"),
                "Warning log should be properly formatted");
    }

    @Test
    void testErrorLogging() {
        logger.error("Something went wrong: {}", "NullPointerException");
        List<String> logs = loggerManager.getPersistenceStore();

        assertEquals(1, logs.size(), "Should contain one error log");
        assertTrue(logs.get(0).contains("[ERROR] [EmeritusLoggerTest] Something went wrong: NullPointerException"),
                "Error log should correctly replace placeholders");
    }

    @Test
    void testFlushDoesNotRemoveLogs() {
        logger.info("Before flush log");
        logger.flush();
        logger.info("After flush log");

        List<String> logs = loggerManager.getPersistenceStore();
        assertEquals(2, logs.size(), "Flush should not remove logs");
    }
}