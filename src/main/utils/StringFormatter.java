package main.utils;

public class StringFormatter {

    private static final String TOKEN_ID = "{}";
    private StringFormatter() { }

    public static String format(String toLog, Object...  args) {
        StringBuilder sb = new StringBuilder(toLog);
        int replaceableIndex = sb.indexOf(TOKEN_ID);
        int index = 0;
        while (replaceableIndex != -1 && index < args.length) {
            sb.replace(replaceableIndex, replaceableIndex + TOKEN_ID.length(), args[index].toString());
            replaceableIndex = sb.indexOf(TOKEN_ID);
            index++;
        }
        return sb.toString();
    }
}
