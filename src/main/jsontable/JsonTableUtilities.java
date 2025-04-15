package main.jsontable;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class JsonTableUtilities {
    public static Map<String, Object> flatten(JSONObject json) {
        Map<String, Object> result = new LinkedHashMap<>();
        Deque<Map.Entry<String, Object>> stack = new ArrayDeque<>();
        stack.push(Map.entry("", json));

        while (!stack.isEmpty()) {
            Map.Entry<String, Object> current = stack.pop();
            String prefix = current.getKey();
            Object obj = current.getValue();

            if (obj instanceof JSONObject jsonObj) {
                List<String> keys = new ArrayList<>(jsonObj.keySet());
                Collections.reverse(keys);
                for (String key : keys) {
                    String newPrefix = prefix.isEmpty() ? key : prefix + "." + key;
                    stack.push(Map.entry(newPrefix, jsonObj.get(key)));
                }
            } else if (obj instanceof JSONArray jsonArr) {
                for (int i = jsonArr.length() - 1; i >= 0; i--) {
                    String newPrefix = prefix.isEmpty() ? String.valueOf(i) : prefix + "." + i;
                    stack.push(Map.entry(newPrefix, jsonArr.get(i)));
                }
            } else {
                result.put(prefix, obj);
            }
        }

        return result;
    }

    public static boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static double computeTextWidth(Font font, String text) {
        Text helper = new Text(text);
        helper.setFont(font);
        return helper.getLayoutBounds().getWidth();
    }
}
