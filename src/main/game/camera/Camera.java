package main.game.camera;

import main.constants.Constants;
import main.constants.Settings;
import main.game.components.Size;
import main.game.components.Vector;
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
    private final Vector end = new Vector();
    private final Vector start = new Vector();
    private Movement currently = Movement.SETTING;

    public Camera() {
        int width = Settings.getInstance().getScreenWidth();
        int height = Settings.getInstance().getScreenHeight();
        Vector startPosition = new Vector(width, height);
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
        Vector local = entity.get(Vector.class);
        Vector global = get(Vector.class);
        return (int) (local.x - global.x);
    }
    public int globalX(int x) {
        Vector global = get(Vector.class);
        return (int) (x - global.x);
    }
    public int globalY(Entity entity) {
        Vector local = entity.get(Vector.class);
        Vector global = get(Vector.class);
        return (int) (local.y - global.y);
    }
    public int globalY(int y) {
        Vector global = get(Vector.class);
        return (int) (y - global.y);
    }

    public void glide(Vector toGlideTo) {
        currently = Movement.GLIDING;
        Vector vector = get(Vector.class);
        start.copy(vector.x, vector.y);
        // TODO magic numbers to center camera position
        int spriteSize = Settings.getInstance().getSpriteSize();
        int extraY = (spriteSize * 7);
        int extraX = (spriteSize * 2);;
        end.copy(toGlideTo.x + extraX, toGlideTo.y + extraY);
    }

    public void set(Vector toSetTo) {
        currently = Movement.SETTING;
        Vector toSetAs = Vector.temporary;
        toSetAs.x = (float) (toSetTo.x - (Settings.getInstance().getScreenWidth() * .4));
        toSetAs.y = (float) (toSetTo.y - (Settings.getInstance().getScreenHeight() * .4));
        Vector vector = get(Vector.class);
        start.copy(toSetAs);
        vector.copy(toSetAs);
        end.copy(toSetAs);
    }

    private void calculateViewBounds() {
        Size size = get(Size.class);
        Vector vector = get(Vector.class);
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
        Vector current = get(Vector.class);
        calculateViewBounds();
        if (currently != Movement.GLIDING) { return; }
        glide(current, end);
    }

    private void glide(Vector vector, Vector toGlideTo) {
        int spriteSize = Settings.getInstance().getSpriteSize();
        int width = Settings.getInstance().getScreenWidth();
        int height = Settings.getInstance().getScreenHeight();
        int targetX = (int) (-toGlideTo.x + (width / 2)) + spriteSize;
        int targetY = (int) (-toGlideTo.y + (height / 2)) + spriteSize;
        vector.x += (-targetX - vector.x) * 0.05f;
        vector.y += (-targetY - vector.y) * 0.05f;
    }


    public void drag(Vector current, boolean isOnFirstDragFrame) {
        currently = Movement.DRAGGING;

        if (isOnFirstDragFrame) {
            end.copy(current);
        } else {
            end.copy(start);
        }
        start.copy(current);

        Vector difference = Vector.temporary;

        difference.copy(end.x - start.x, end.y - start.y);

        if (difference.x == 0 || difference.y == 0) { return; }

        current = get(Vector.class);
        current.x += difference.x;
        current.y += difference.y;
    }
}