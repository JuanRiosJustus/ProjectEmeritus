package main.constants;

import java.util.Objects;

public class Vector3f {

    public static final Vector3f temporary = new Vector3f();

    public float x;
    public float y;
    public float z;

    public Vector3f() {
        this(0, 0, 0);
    }

    public Vector3f(float startingX, float startingY) {
        this(startingX, startingY, 0);
    }

    public Vector3f(float startingX, float startingY, float startingZ) {
        x = startingX;
        y = startingY;
        z = startingZ;
    }

    public void clear() {
        copy(0, 0, 0);
    }

    public Vector3f copy() {
        return new Vector3f(x, y, z);
    }

    public void copy(float newX, float newY) {
        x = newX;
        y = newY;
    }

    public void copy(float newX, float newY, float newZ) {
        x = newX;
        y = newY;
        z = newZ;
    }

    public void copy(Vector3f toCopy) {
        x = toCopy.x;
        y = toCopy.y;
        z = toCopy.z;
    }

    public void add(Vector3f toAdd) {
        x += toAdd.x;
        y += toAdd.y;
        x += toAdd.z;
    }

    public void add(float addX, float addY, float addZ) {
        x += addX;
        y += addY;
        z += addZ;
    }

    public static Vector3f centerLimitOnY(int size, int x, int y, int width, int height) {

        if (height > size) {
            y -= height - size;
        }

        int widthDifference = Math.abs(size - width);
        if (width > size) {
            x -= widthDifference / 2;
        } else {
            x += widthDifference / 2;
        }

        temporary.copy(x, y);
        return temporary;
    }

    public static Vector3f center(int spriteWidth, int spriteHeight, int x, int y, int width, int height) {

        int heightDifference = Math.abs(spriteHeight - height);
        if (height > spriteHeight) {
            y -= heightDifference / 2;
        } else {
            y += heightDifference / 2;
        }
        int widthDifference = Math.abs(spriteWidth - width);
        if (width > spriteWidth) {
            x -= widthDifference / 2;
        } else {
            x += widthDifference / 2;
        }

        temporary.copy(x, y);
        return temporary;
    }


//    public static Vector center(int size, int x, int y, int width, int height) {
//
//        int heightDifference = Math.abs(size - height);
//        if (height > size) {
//            y -= heightDifference / 2;
//        } else {
//            y += heightDifference / 2;
//        }
//        int widthDifference = Math.abs(size - width);
//        if (width > size) {
//            x -= widthDifference / 2;
//        } else {
//            x += widthDifference / 2;
//        }
//
//        temporary.copy(x, y);
//        return temporary;
//    }

    public static void lerp(Vector3f start, Vector3f end, float percent, Vector3f result) {
        float x = start.x + percent * (end.x - start.x);
        float y = start.y + percent * (end.y - start.y);
        result.x = x;
        result.y = y;
    }

    public static Vector3f lerp(Vector3f start, Vector3f end, float percent) {
        float x = start.x + percent * (end.x - start.x);
        float y = start.y + percent * (end.y - start.y);
        return new Vector3f(x, y);
    }

    public static float lerp(float start, float end, float current) {
        return start + current * (end - start);
    }

    @Override
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector3f vector)) return false;
        return Float.compare(vector.x, x) == 0 && Float.compare(vector.y, y) == 0 && Float.compare(vector.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
