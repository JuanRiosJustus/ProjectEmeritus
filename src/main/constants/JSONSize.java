package main.constants;

import org.json.JSONObject;

public class JSONSize extends JSONObject {
    private static final String JSON_WIDTH = "width";
    private static final String JSON_HEIGHT = "y";
    private static final String Z = "z";

    public JSONSize(double width, double height) {
        put(JSON_WIDTH, width);
        put(JSON_HEIGHT, height);
    }

    public double getWidth() { return getDouble(JSON_WIDTH); }
    public double getHeight() { return getDouble(JSON_HEIGHT); }
    public void setWidth(double x) { put(JSON_WIDTH, x); }
    public void setHeight(double y) { put(JSON_HEIGHT, y); }
}
