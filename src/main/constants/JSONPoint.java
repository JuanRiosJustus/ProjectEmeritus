package main.constants;

import org.json.JSONObject;

public class JSONPoint extends JSONObject {
    private static final String JSON_X = "x";
    private static final String JSON_Y = "y";
    private static final String JSON_Z = "z";

    public JSONPoint(double x, double y, double z) {
        put(JSON_X, x);
        put(JSON_Y, y);
        put(JSON_Z, z);
    }

    public double getX() { return getDouble(JSON_X); }
    public double getY() { return getDouble(JSON_Y); }
    public double getZ() { return getDouble(JSON_Z); }

    public void setX(double x) { put(JSON_X, x); }
    public void setY(double y) { put(JSON_Y, y); }
    public void setZ(double z) { put(JSON_Z, z); }
}
