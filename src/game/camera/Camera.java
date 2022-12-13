package game.camera;

import constants.Constants;
import game.components.Dimension;
import game.components.Vector;
import game.entity.Entity;

import java.awt.*;

public class Camera extends Entity {

    public enum Movement {
        DRAGGING,
        GLIDING,
        SETTING
    }

    private static Camera instance = null;
    private final Rectangle boundary = new Rectangle();
    private final Vector end = new Vector();
    private final Vector start = new Vector();
    private Movement currently = Movement.SETTING;

    public Camera() {
        Vector startPosition = new Vector(
                Constants.APPLICATION_WIDTH,
                Constants.APPLICATION_HEIGHT
        );
        add(startPosition);
        start.copy(startPosition);
        end.copy(startPosition);
        int width = Constants.APPLICATION_WIDTH;
        int height = Constants.APPLICATION_HEIGHT;
        add(new Dimension(width, height));
        calculateViewBounds();

    }

    public static Camera get() {
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
        end.copy(toGlideTo.x, toGlideTo.y);
    }

    public void set(Vector toSetTo) {
        currently = Movement.SETTING;
        Vector toSetAs = Vector.temporary;
        toSetAs.x = (float) (toSetTo.x - (Constants.APPLICATION_WIDTH * .4));
        toSetAs.y = (float) (toSetTo.y - (Constants.APPLICATION_HEIGHT * .4));
        Vector vector = get(Vector.class);
        start.copy(toSetAs);
        vector.copy(toSetAs);
        end.copy(toSetAs);
    }

    private void calculateViewBounds() {
        Dimension dimension = get(Dimension.class);
        Vector vector = get(Vector.class);
        boundary.setBounds(
                (int) vector.x,
                (int) vector.y,
                (int) dimension.width,
                (int) dimension.height
        );
//        EmeritusLogger.get().log(boundary.toString());
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
        calculateViewBounds();
        if (currently != Movement.GLIDING) { return; }
        Vector current = get(Vector.class);
        glide(current, end);
    }


    private void glide(Vector vector, Vector toGlideTo) {
        int targetX = (int) (-toGlideTo.x + (Constants.APPLICATION_WIDTH / 2)) + Constants.SPRITE_SIZE;
        int targetY = (int) (-toGlideTo.y + (Constants.APPLICATION_HEIGHT / 2)) + Constants.SPRITE_SIZE;
        vector.x += (-targetX - vector.x) * 0.05;
        vector.y += (-targetY - vector.y) * 0.05;
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