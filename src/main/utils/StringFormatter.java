package main.utils;

public class StringFormatter {

    private StringFormatter() { }

    public static String format(String toLog, Object...  args) {
        StringBuilder sb = new StringBuilder(toLog);
        int index = sb.indexOf("{}");
        int argIndex = 0;
        while(index != -1) {
            if (args != null && args.length > 0 && argIndex < args.length) {
                sb.replace(index,  index + 2, args[argIndex].toString());
                argIndex++;
            }
            index = sb.indexOf("{}");
        }
        return sb.toString();
    }
}
