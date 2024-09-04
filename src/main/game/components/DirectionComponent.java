package main.game.components;

import main.constants.Direction;

public class DirectionComponent extends Component {
    private Direction mFacingDirection = Direction.East;
    public Direction getFacingDirection() { return mFacingDirection; }
    public void setDirection(Direction direction) { mFacingDirection = direction; }
}
