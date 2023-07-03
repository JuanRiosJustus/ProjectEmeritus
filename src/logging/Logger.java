package logging;

import java.text.MessageFormat;

public class Logger {

    private final String name;
    private final LoggerManager manager;

    public Logger(LoggerManager loggerManager, String loggerName) {
        name = loggerName;
        manager = loggerManager;
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
