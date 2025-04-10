package main.constants;

import java.util.Objects;

public class Vector3d {

    private static final Vector3d temporary = new Vector3d();

    public double x;
    public double y;
    public double z;

    public Vector3d() {
        this(0, 0, 0);
    }

    public Vector3d(double startingX, double startingY) {
        this(startingX, startingY, 0);
    }

    public Vector3d(double startingX, double startingY, double startingZ) {
        x = startingX;
        y = startingY;
        z = startingZ;
    }

    public double magnitude() {
        return (double) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3d scale(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this; // Allow chaining if desired
    }

    /**
     * Normalizes the vector to have a magnitude of 1.
     * If the vector is a zero vector, it does nothing.
     */
    public Vector3d normalize() {
        double magnitude = magnitude();
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
    public Vector3d subtract(Vector3d other) {
        return new Vector3d(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    /**
     * Subtract another vector from this vector in place.
     *
     * @param other The vector to subtract.
     * @return This vector after subtraction.
     */
    public Vector3d subtractInPlace(Vector3d other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }

    public void clear() {
        copy(0, 0, 0);
    }

    public Vector3d copy() {
        return new Vector3d(x, y, z);
    }

    public void copy(double newX, double newY) {
        x = newX;
        y = newY;
    }

    public void copy(double newX, double newY, double newZ) {
        x = newX;
        y = newY;
        z = newZ;
    }

    public void copy(Vector3d toCopy) {
        x = toCopy.x;
        y = toCopy.y;
        z = toCopy.z;
    }

    public void add(Vector3d toAdd) {
        x += toAdd.x;
        y += toAdd.y;
        x += toAdd.z;
    }

    public void add(double addX, double addY, double addZ) {
        x += addX;
        y += addY;
        z += addZ;
    }

    public static Vector3d centerLimitOnY(int size, int x, int y, int width, int height) {

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

    public static Vector3d center(int spriteWidth, int spriteHeight, int x, int y, int width, int height) {

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

    public static Vector3d getCenteredVector(int inX, int inY, int inWidth, int inHeight, int outWidth, int outHeight) {
        int widthDifference = Math.abs(inWidth - outWidth);
        int heightDifference = Math.abs(inHeight - outHeight);
        int outX = inX + (widthDifference / 2);
        int outY = inY + (heightDifference / 2);
        return new Vector3d(outX, outY);
    }

    public static void lerp(Vector3d start, Vector3d end, double percent, Vector3d result) {
        double x = start.x + percent * (end.x - start.x);
        double y = start.y + percent * (end.y - start.y);
        result.x = x;
        result.y = y;
    }

    public static Vector3d lerp(Vector3d start, Vector3d end, double percent) {
        double x = start.x + percent * (end.x - start.x);
        double y = start.y + percent * (end.y - start.y);
        return new Vector3d(x, y);
    }

    public static double lerp(double start, double end, double current) {
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
        if (!(o instanceof Vector3d vector)) return false;
        return Double.compare(vector.x, x) == 0 && Double.compare(vector.y, y) == 0 && Double.compare(vector.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
