package main.game.components;

import main.constants.Direction;

public class DirectionalFace extends Component {
    private Direction mFacingDirection = Direction.East;
    private Direction mPotentialDirection = Direction.East;
    public Direction getFacingDirection() { return mFacingDirection; }
    public void setDirection(Direction direction) { mFacingDirection = direction; }
    public void setPotentialDirection(Direction direction) { mPotentialDirection = direction; }
}
