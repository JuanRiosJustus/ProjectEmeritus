package jsonsql.main;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

public class JsonTableUtilities {


    public static JSONArray flattenJSONArray(JSONArray input) {
        JSONArray result = new JSONArray();

        for (Object item : input) {
            if (item instanceof JSONObject obj) {
                JSONObject flat = new JSONObject();
                flatten("", obj, flat);
                result.add(flat);
            } else {
                throw new IllegalArgumentException("All elements in the input array must be JSONObjects.");
            }
        }

        return result;
    }

    private static void flatten(String prefix, Object value, JSONObject output) {
        if (value instanceof JSONObject jsonObject) {
            for (String key : jsonObject.keySet()) {
                Object nestedValue = jsonObject.get(key);
                String newKey = prefix.isEmpty() ? key : prefix + "." + key;
                flatten(newKey, nestedValue, output);
            }
        } else if (value instanceof JSONArray jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                Object nestedValue = jsonArray.get(i);
                String newKey = prefix + "[" + i + "]";
                flatten(newKey, nestedValue, output);
            }
        } else {
            output.put(prefix, value);
        }
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
