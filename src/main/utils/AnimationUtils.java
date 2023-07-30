package main.utils;

import main.game.components.Vector;

public class AnimationUtils {
    public static Vector lerp(Vector start, Vector end, float percent) {
        if (start == null) { return null; }
        float x = start.x + percent * (end.x - start.x);
        float y = start.y + percent * (end.y - start.y);
        return new Vector(x, y);
    }

    public static double singulerp(double startX, double endX, double percent) {
        if (startX == -1) { return 0; }
        return startX + percent * (endX - startX);
    }

    public static void lerp(Vector start, Vector end, float percent, Vector result) {
        float x = start.x + percent * (end.x - start.x);
        float y = start.y + percent * (end.y - start.y);
        result.x = x;
        result.y = y;
    }

    public static float distance(Vector v1, Vector v2) {
        double xDistance = v1.x - v2.x;
        double yDistance = v1.y - v2.y;
        return (float) Math.sqrt(xDistance * xDistance + yDistance * yDistance);
    }

    public static double easeOutElastic(double progress){
        double c4 = (2 * Math.PI) / 3;

        return progress == 0 ?
                0 : progress == 1 ?
                1 : Math.pow(2, -10 * progress) * Math.sin((progress * 10 - 0.75) * c4) + 1;
    }

    public static double easeInExpo(double progress) {
        return progress == 0 ? 0 : Math.pow(2, 10 * progress - 10);
    }

    public static double easeOutBounce(double progress) {
        double n1 = 7.5625;
        double d1 = 2.75;

        if (progress < 1 / d1) {
            return n1 * progress * progress;
        } else if (progress < 2 / d1) {
            return n1 * (progress -= 1.5 / d1) * progress + 0.75;
        } else if (progress < 2.5 / d1) {
            return n1 * (progress -= 2.25 / d1) * progress + 0.9375;
        } else {
            return n1 * (progress -= 2.625 / d1) * progress + 0.984375;
        }
    }
}
