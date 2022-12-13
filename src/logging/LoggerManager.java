package logging;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LoggerManager {

    private PrintWriter outputStream = null;
    private final StringBuilder buffer = new StringBuilder();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public LoggerManager() {
        try {
            FileOutputStream fos = new FileOutputStream(FileDescriptor.out);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            BufferedWriter bw = new BufferedWriter(osw, 512);
            outputStream = new PrintWriter(bw);
        } catch (Exception ex) {
            System.err.println("LOGGER FAILED - INITIALIZATION EXCEPTION");
        }
    }

    public void banner(String message) {
        log("===============" + message + "===============");
    }

    public void log(String message) {
        String toLog = MessageFormat.format("[{0}] {1}", formatter.format(Instant.now()), message);
        buffer.append(toLog).append("\n");
        outputStream.println(toLog);
        outputStream.flush();
    }

    public void error(String message) {
        String toLog = MessageFormat.format("[{0}] {1}", formatter.format(Instant.now()), message);
        buffer.append(toLog).append("\n");
        System.err.println(toLog);
    }

    public void flush() {
        try {
            String fileName = getClass().getSimpleName() + ".log";
            PrintWriter out = new PrintWriter(new FileWriter(fileName, false), true);
            out.write(buffer.toString());
            out.close();
            buffer.delete(0, buffer.length());
        } catch (Exception ex) {
            System.err.println("LOGGER FAILED - FLUSHING EXCEPTION");
        }
    }
}