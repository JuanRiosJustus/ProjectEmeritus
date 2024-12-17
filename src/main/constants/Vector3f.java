package main.constants;

import java.util.Objects;

public class Vector3f {

    private static final Vector3f temporary = new Vector3f();

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

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3f scale(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this; // Allow chaining if desired
    }

    /**
     * Normalizes the vector to have a magnitude of 1.
     * If the vector is a zero vector, it does nothing.
     */
    public Vector3f normalize() {
        float magnitude = magnitude();
        if (magnitude > 0.0f) {
            this.x /= magnitude;
            this.y /= magnitude;
            this.z /= magnitude;
        }
        return this;
    }

    /**
     * Subtract another vector from this vector and return a new vector.
     *
     * @param other The vector to subtract.
     * @return A new Vector3f representing the difference.
     */
    public Vector3f subtract(Vector3f other) {
        return new Vector3f(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    /**
     * Subtract another vector from this vector in place.
     *
     * @param other The vector to subtract.
     * @return This vector after subtraction.
     */
    public Vector3f subtractInPlace(Vector3f other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
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

    public static Vector3f getCenteredVector(int inX, int inY, int inWidth, int inHeight, int outWidth, int outHeight) {
        int widthDifference = Math.abs(inWidth - outWidth);
        int heightDifference = Math.abs(inHeight - outHeight);
        int outX = inX + (widthDifference / 2);
        int outY = inY + (heightDifference / 2);
        return new Vector3f(outX, outY);
    }

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

    public boolean isZero() {
        return this.x == 0 && this.y == 0 && this.z == 0;
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
