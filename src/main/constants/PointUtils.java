package main.constants;

import java.awt.Point;

public class PointUtils {
    public static Point lerp(Point start, Point end, float percent) {
        float x = start.x + percent * (end.x - start.x);
        float y = start.y + percent * (end.y - start.y);
        return new Point((int) x, (int) y);
    }
}
