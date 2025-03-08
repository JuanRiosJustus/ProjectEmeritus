package main.constants;

import main.constants.JSONPoint;
import main.constants.JSONSize;
import org.json.JSONObject;

public class JSONShape extends JSONObject {

    private JSONPoint mPoint = null;
    private JSONSize mSize = null;

    public JSONShape() {
        mPoint = new JSONPoint(0, 0, 0);
        mSize = new JSONSize(0, 0);
    }

    public double getWidth() { return mSize.getWidth(); }
    public double getHeight() { return mSize.getHeight(); }
    public void setWidth(double width) { mSize.setWidth(width); }
    public void setHeight(double height) { mSize.setHeight(height); }

    public double getX() { return mPoint.getX(); }
    public double getY() { return mPoint.getY(); }
    public void setX(double x) { mPoint.setX(x); }
    public void setY(double y) { mPoint.setY(y); }
}
