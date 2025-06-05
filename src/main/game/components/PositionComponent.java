package main.game.components;

import main.constants.Vector3f;

public class PositionComponent extends Component {
    private final Vector3f mPosition = new Vector3f();
    public void setPosition(int x, int y) { mPosition.x = x; mPosition.y = y; }
    public int getX() { return (int) mPosition.x; }
    public int getY() { return (int) mPosition.y; }

    public static int getManhattanDistance(PositionComponent a, PositionComponent b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

}
