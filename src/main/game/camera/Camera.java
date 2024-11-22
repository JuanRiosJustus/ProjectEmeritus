package main.game.camera;

import main.game.components.tile.Tile;
import main.game.main.GameConfigurations;
import main.constants.Vector3f;
import main.game.entity.Entity;
import main.game.main.GameModel;

public class Camera extends Entity {

    public enum Movement {
        DRAGGING,
        GLIDING,
        SETTING,
        STATIONARY
    }

    private static Camera mInstance = null;
    private final Vector3f end = new Vector3f();
    private final Vector3f start = new Vector3f();
    private Movement currently = Movement.SETTING;
    private final Vector3f mCurrentPosition = new Vector3f();

    public Camera() {
        Vector3f startPosition = new Vector3f();
        start.copy(startPosition);
        end.copy(startPosition);
    }

    public static Camera getInstance() {
        if (mInstance == null) {
            mInstance = new Camera();
        }
        return mInstance;
    }

    public int globalX(Entity entity) {
        Vector3f local = entity.get(Vector3f.class);;
        return (int) (local.x - mCurrentPosition.x);
    }
    public int globalX(int x) {
        return (int) (x - mCurrentPosition.x);
    }
    public int globalY(Entity entity) {
        Vector3f local = entity.get(Vector3f.class);;
        return (int) (local.y - mCurrentPosition.y);
    }
    public int globalY(int y) {
        return (int) (y - mCurrentPosition.y);
    }
    
    public Vector3f getGlobalCoordinates(int x, int y) {
        return new Vector3f(x - mCurrentPosition.x, y - mCurrentPosition.y);
    }

    public Vector3f getGlobalCoordinates(GameModel model, int row, int column) {
        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();
        return new Vector3f(row * spriteHeight, column * spriteWidth);
    }


    public Vector3f getPosition() {
        return mCurrentPosition;
    }

    public void glide(Vector3f toGlideTo) {
        currently = Movement.GLIDING;
        Vector3f vector = mCurrentPosition;
        start.copy(vector.x, vector.y);

        int extraY = GameConfigurations.getInstance().getSpriteHeight();
        int extraX = GameConfigurations.getInstance().getSpriteWidth();;
        end.copy(toGlideTo.x + extraX, toGlideTo.y + extraY);
    }

    public void glide(GameModel model, Entity tileEntity) {
        currently = Movement.GLIDING;
        Vector3f vector = mCurrentPosition;
        start.copy(vector.x, vector.y);

        // TODO magic numbers to center camera position
//        int spriteSize = GameConfigurations.getInstance().getSpriteSize();

        int extraY = model.getSettings().getSpriteHeight();
        int extraX = model.getSettings().getSpriteWidth();
        Tile tile = tileEntity.get(Tile.class);
        Vector3f toGlideTo = tile.getLocalVector(model);
        end.copy(toGlideTo.x + extraX, toGlideTo.y + extraY);
    }

    public void set(Vector3f toSetTo) {
        currently = Movement.SETTING;
        Vector3f toSetAs = new Vector3f();
        toSetAs.x = (float) (toSetTo.x - (GameConfigurations.getInstance().getViewPortWidth() * .4));
        toSetAs.y = (float) (toSetTo.y - (GameConfigurations.getInstance().getViewPortHeight() * .4));
        start.copy(toSetAs);
        mCurrentPosition.copy(toSetAs);
        end.copy(toSetAs);
    }

    private void calculateViewBounds(GameModel model) {
        int screenWidth = model.getSettings().getViewPortWidth();
        int screenHeight = model.getSettings().getViewPortHeight();
        Vector3f vector = mCurrentPosition;
//        boundary.setBounds(
//                (int) vector.x,
//                (int) vector.y,
//                screenWidth,
//                screenHeight
//        );
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
        Vector3f current = mCurrentPosition;
//        calculateViewBounds(model);
        if (currently != Movement.GLIDING) { return; }
        glide(current, end);
    }

    private void glide(Vector3f vector, Vector3f toGlideTo) {
//        int spriteSize = GameConfigurations.getInstance().getSpriteSize();
        int width = GameConfigurations.getInstance().getViewPortWidth();
        int height = GameConfigurations.getInstance().getViewPortHeight();
        int targetX = (int) (-toGlideTo.x + (width / 2)) + width;
        int targetY = (int) (-toGlideTo.y + (height / 2)) + height;
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

        Vector3f difference = new Vector3f();

        difference.copy(end.x - start.x, end.y - start.y);

        if (difference.x == 0 || difference.y == 0) { return; }

        current = mCurrentPosition;
        current.x += difference.x;
        current.y += difference.y;
    }
    public String toString() {
        return start.x + ", " + start.y;
    }
}