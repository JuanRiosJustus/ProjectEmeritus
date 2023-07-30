package main.logging;


public class ELoggerFactory {

    public static ELoggerFactory getInstance() { return instance; }
    private static final ELoggerFactory instance = new ELoggerFactory();
    private final ELoggerManager manager = new ELoggerManager();

    public ELogger getELogger(Class<?> loggerName) {
        return new ELogger(manager, loggerName.getSimpleName());
    }

    public void close() { manager.flush(); }
}
