package main.game.camera;

import main.constants.Settings;
import main.game.components.Size;
import main.game.components.Vector3f;
import main.game.entity.Entity;

import java.awt.*;

public class Camera extends Entity {

    public enum Movement {
        DRAGGING,
        GLIDING,
        SETTING,
        STATIONARY
    }

    private static Camera instance = null;
    private final Rectangle boundary = new Rectangle();
    private final Vector3f end = new Vector3f();
    private final Vector3f start = new Vector3f();
    private Movement currently = Movement.SETTING;

    public Camera() {
        int width = Settings.getInstance().getScreenWidth();
        int height = Settings.getInstance().getScreenHeight();
        Vector3f startPosition = new Vector3f(width, height);
        add(startPosition);
        start.copy(startPosition);
        end.copy(startPosition);
        add(new Size(width, height));
        calculateViewBounds();

    }

    public static Camera getInstance() {
        if (instance == null) {
            instance = new Camera();
        }
        return instance;
    }

    public int globalX(Entity entity) {
        Vector3f local = entity.get(Vector3f.class);
        Vector3f global = get(Vector3f.class);
        return (int) (local.x - global.x);
    }
    public int globalX(int x) {
        Vector3f global = get(Vector3f.class);
        return (int) (x - global.x);
    }
    public int globalY(Entity entity) {
        Vector3f local = entity.get(Vector3f.class);
        Vector3f global = get(Vector3f.class);
        return (int) (local.y - global.y);
    }
    public int globalY(int y) {
        Vector3f global = get(Vector3f.class);
        return (int) (y - global.y);
    }

    public Vector3f getVector() {
        return get(Vector3f.class).copy();
    }

    public void glide(Vector3f toGlideTo) {
        currently = Movement.GLIDING;
        Vector3f vector = get(Vector3f.class);
        start.copy(vector.x, vector.y);

        // TODO magic numbers to center camera position
        int spriteSize = Settings.getInstance().getSpriteSize();

        int extraY = (Settings.getInstance().getSpriteHeight() * 1);
        int extraX = (Settings.getInstance().getSpriteWidth() * 1);;
        end.copy(toGlideTo.x + extraX, toGlideTo.y + extraY);
    }

    public void set(Vector3f toSetTo) {
        currently = Movement.SETTING;
        Vector3f toSetAs = Vector3f.temporary;
        toSetAs.x = (float) (toSetTo.x - (Settings.getInstance().getScreenWidth() * .4));
        toSetAs.y = (float) (toSetTo.y - (Settings.getInstance().getScreenHeight() * .4));
        Vector3f vector = get(Vector3f.class);
        start.copy(toSetAs);
        vector.copy(toSetAs);
        end.copy(toSetAs);
    }

    private void calculateViewBounds() {
        Size size = get(Size.class);
        Vector3f vector = get(Vector3f.class);
        boundary.setBounds(
                (int) vector.x,
                (int) vector.y,
                (int) size.width,
                (int) size.height
        );
    }

    public boolean isWithinView(int x, int y, int width, int height) {
        return boundary.intersects(x, y, width, height );
    }
////
//    public Vector getWorldVector(Vector toGetWorldFor) {
//        Vector worldVector = new Vector();
//        worldVector.x = globalX((int) toGetWorldFor.x);
//        worldVector.y = globalY((int) toGetWorldFor.y);
//        return worldVector;
//    }

//    public int getWorldX(int x) { return x - (int)m_vector.x; }
//    public int getWorldY(int y) { return y - (int)m_vector.y; }

    public void update() {
        Vector3f current = get(Vector3f.class);
        calculateViewBounds();
        if (currently != Movement.GLIDING) { return; }
        glide(current, end);
    }

    private void glide(Vector3f vector, Vector3f toGlideTo) {
        int spriteSize = Settings.getInstance().getSpriteSize();
        int width = Settings.getInstance().getScreenWidth();
        int height = Settings.getInstance().getScreenHeight();
        int targetX = (int) (-toGlideTo.x + (width / 2)) + spriteSize;
        int targetY = (int) (-toGlideTo.y + (height / 2)) + spriteSize;
        vector.x += (-targetX - vector.x) * 0.05f;
        vector.y += (-targetY - vector.y) * 0.05f;
    }


    public void drag(Vector3f current, boolean isOnFirstDragFrame) {
        currently = Movement.DRAGGING;

        if (isOnFirstDragFrame) {
            end.copy(current);
        } else {
            end.copy(start);
        }
        start.copy(current);

        Vector3f difference = Vector3f.temporary;

        difference.copy(end.x - start.x, end.y - start.y);

        if (difference.x == 0 || difference.y == 0) { return; }

        current = get(Vector3f.class);
        current.x += difference.x;
        current.y += difference.y;
    }
    public String toString() {
        return start.x + ", " + start.y;
    }
}