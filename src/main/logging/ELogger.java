package main.logging;

public class ELogger {

    private final String mName;
    private final ELoggerManager mLoggerManager;

    public ELogger(ELoggerManager loggerManager, String name) {
        mName = name;
        mLoggerManager = loggerManager;
    }

    public void debug(String message) {
        mLoggerManager.debug(mName, message);
    }

    public void debug(String message, Object... arguments) {
        mLoggerManager.debug(mName, message, arguments);
    }

    public void info(String message) { mLoggerManager.info(mName, message); }

    public void info(String message, Object... arguments) {
        mLoggerManager.info(mName, message, arguments);
    }

    public void warn(String message) {
        mLoggerManager.warn(mName, message);
    }

    public void warn(String message, Object... arguments) {
        mLoggerManager.warn(mName, message, arguments);
    }

    public void error(String message) { mLoggerManager.error(mName, message); }

    public void error(String message, Object... arguments) {
        mLoggerManager.error(mName, message, arguments);
    }
    public void setLogLevel(String logLevel) { mLoggerManager.setLogLevel(logLevel); }
}
