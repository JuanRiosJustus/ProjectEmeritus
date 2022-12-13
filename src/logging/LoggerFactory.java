package logging;

public class LoggerFactory {

    public static LoggerFactory instance() { return instance; }
    private static final LoggerFactory instance = new LoggerFactory();
    private final LoggerManager manager = new LoggerManager();

    public Logger logger(Class<?> loggerName) {
        return new Logger(manager, loggerName.getSimpleName());
    }

    public void close() { manager.flush(); }
}
