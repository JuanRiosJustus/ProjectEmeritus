package main.graphics;

import main.game.components.MovementComponent;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.ui.custom.SwingUiUtils;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public abstract class ControllerUI extends GameUI {
    protected final JButton mEnterButton;
    protected final JButton mExitButton;
    protected final int mMainContentWidth;
    protected final int mMainContentHeight;
    protected final int mExitWidth;
    protected final int mExitHeight;
    protected GameModel mModel;

    public ControllerUI(int width, int height, int x, int y, JButton enter, JButton exit) {
        super(width, height, x, y, "name");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        mMainContentWidth = width;
        mMainContentHeight = (int) (height * .8);

        mExitWidth = width;
        mExitHeight = height - mMainContentHeight;

        mEnterButton = enter;
        mExitButton = exit;
        mExitButton.setPreferredSize(new Dimension(mExitWidth, mExitHeight));
        mExitButton.setMinimumSize(new Dimension(mExitWidth, mExitHeight));
        mExitButton.setMaximumSize(new Dimension(mExitWidth, mExitHeight));
    }

    public void gameUpdate(GameModel model) { mModel = model; }
    public JButton getEnterButton() {
        return mEnterButton;
    }
    public JButton getExitButton() {
        return mExitButton;
    }
//    public void onOpenAction() { }
    public void onCloseAction() { }

    public void onOpenAction() {
        if (mModel == null) { return; }
        Entity entity = mModel.getSpeedQueue().peek();
        MovementComponent movementComponent = entity.get(MovementComponent.class);
        mModel.getGameState().setTileToGlideTo(movementComponent.getCurrentTile());
        mModel.getGameState().setupEntitySelections(movementComponent.getCurrentTile());
    }
}
