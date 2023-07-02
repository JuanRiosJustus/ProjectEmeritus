package logging;

import java.text.MessageFormat;

public class Logger {

    private final String name;
    private final LoggerManager manager;

    public Logger(LoggerManager loggerManager, String loggerName) {
        name = loggerName;
        manager = loggerManager;
    }

    public void banner(String pattern, Object... arguments) {
        manager.banner(MessageFormat.format("[{0}] {1}", name, MessageFormat.format(pattern, arguments)));
    }

    public void banner(String message) {
        manager.banner(message);
    }

    public void error(String message) {
        manager.error(MessageFormat.format("[{0}] {1}", name, message));
    }
    public void error(String pattern, Object... arguments) {
        manager.error(MessageFormat.format("[{0}] {1}", name, MessageFormat.format(pattern, arguments)));
    }

    public void log(String message) {
        manager.log(MessageFormat.format("[{0}] {1}", name, message));
    }

    public void log(String pattern, Object... arguments) {
        manager.log(MessageFormat.format("[{0}] {1}", name, MessageFormat.format(pattern, arguments)));
    }

    public void smartLog(String pattern, Object... arguments) {
        manager.log(MessageFormat.format("[{0}] {1}", name, MessageFormat.format(pattern, arguments)));
    }

    private final StringBuilder sb = new StringBuilder();

    public void log(String logging, Object...  args) {
        // TODO
        // for (int i = 0; i < logging.length() - 1; i++) {
        //     // Check if current char and next char are respective open and close curly brackets, insert arg if possible
        //     if (logging.charAt(i) == '{' && logging.charAt(i + 1) == '}') {

        //     }
            
        // }
    }
}
