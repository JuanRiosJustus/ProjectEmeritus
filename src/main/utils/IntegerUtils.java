package main.utils;

public class IntegerUtils {
    public static Integer[] parseInts(String value, String delimiter) {
        String[] splitValue = value.split(delimiter);
        Integer[] returnValue = new Integer[splitValue.length];
        for (int index = 0; index < returnValue.length; index++) {
            returnValue[index] = Integer.valueOf(splitValue[index]);
        }
        return returnValue;
    }
}
