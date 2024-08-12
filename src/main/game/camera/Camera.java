package main.game.camera;

import main.constants.Settings;
import main.game.components.Size;
import main.game.components.Vector3f;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;

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
    private final Vector3f returnValue = new Vector3f();
    private Movement currently = Movement.SETTING;
    private final Vector3f mPosition = new Vector3f();

    public Camera() {
        int width = Settings.getInstance().getScreenWidth();
        int height = Settings.getInstance().getScreenHeight();
        Vector3f startPosition = new Vector3f(width, height);
//        add(startPosition);
        start.copy(startPosition);
        end.copy(startPosition);
//        add(new Size(width, height));
//        calculateViewBounds();
    }

    public static Camera getInstance() {
        if (instance == null) {
            instance = new Camera();
        }
        return instance;
    }

    public int globalX(Entity entity) {
        Vector3f local = entity.get(Vector3f.class);;
        Vector3f global = mPosition;
        return (int) (local.x - global.x);
    }
    public int globalX(int x) {
        Vector3f global = mPosition;
        return (int) (x - global.x);
    }
    public int globalY(Entity entity) {
        Vector3f local = entity.get(Vector3f.class);;
        Vector3f global = mPosition;
        return (int) (local.y - global.y);
    }
    public int globalY(int y) {
        Vector3f global = mPosition;
        return (int) (y - global.y);
    }
    
    public Vector3f getGlobalCoordinates(int x, int y) {
        return new Vector3f(x - mPosition.x, y - mPosition.y);
    }

    public Vector3f getGlobalCoordinates(GameModel model, int row, int column) {
        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();
        return new Vector3f(row * spriteHeight, column * spriteWidth);
    }


    public Vector3f getPosition() {
        return mPosition;
    }

    public void glide(Vector3f toGlideTo) {
        currently = Movement.GLIDING;
        Vector3f vector = mPosition;
        start.copy(vector.x, vector.y);

        // TODO magic numbers to center camera position
        int spriteSize = Settings.getInstance().getSpriteSize();

        int extraY = Settings.getInstance().getSpriteHeight();
        int extraX = Settings.getInstance().getSpriteWidth();;
        end.copy(toGlideTo.x + extraX, toGlideTo.y + extraY);
    }

    public void set(Vector3f toSetTo) {
        currently = Movement.SETTING;
        Vector3f toSetAs = Vector3f.temporary;
        toSetAs.x = (float) (toSetTo.x - (Settings.getInstance().getScreenWidth() * .4));
        toSetAs.y = (float) (toSetTo.y - (Settings.getInstance().getScreenHeight() * .4));
        start.copy(toSetAs);
        mPosition.copy(toSetAs);
        end.copy(toSetAs);
    }

    private void calculateViewBounds(GameModel model) {
        int screenWidth = model.getSettings().getScreenWidth();
        int screenHeight = model.getSettings().getScreenHeight();
        Vector3f vector = mPosition;
        boundary.setBounds(
                (int) vector.x,
                (int) vector.y,
                screenWidth,
                screenHeight
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

    public void update(GameModel model) {
        Vector3f current = mPosition;
        calculateViewBounds(model);
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

        current = mPosition;
        current.x += difference.x;
        current.y += difference.y;
    }
    public String toString() {
        return start.x + ", " + start.y;
    }
}