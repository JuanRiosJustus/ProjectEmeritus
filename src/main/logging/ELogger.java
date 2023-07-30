package main.logging;

public class ELogger {

    private final String name;
    private final ELoggerManager manager;

    public ELogger(ELoggerManager loggerManager, String loggerName) {
        name = loggerName;
        manager = loggerManager;
    }

    public void debug(String message) {
        manager.debug(name, message);
    }

    public void debug(String message, Object... arguments) {
        manager.debug(name, message, arguments);
    }

    public void info(String message) {
        manager.info(name, message);
    }

    public void info(String message, Object... arguments) {
        manager.info(name, message, arguments);
    }

    public void warn(String message) {
        manager.warn(name, message);
    }

    public void warn(String message, Object... arguments) {
        manager.warn(name, message, arguments);
    }

    public void error(String message) {
        manager.error(name, message);
    }

    public void error(String message, Object... arguments) {
        manager.error(name, message, arguments);
    }
}
