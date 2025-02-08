package main.game.main.ui;

import main.game.main.GameAPI;
import main.game.main.GameController;
import main.ui.outline.production.OutlineButtonToCheckBoxRow;
import main.ui.outline.production.OutlineButtonToCheckBoxRows;
import org.json.JSONObject;

import javax.swing.JButton;
import java.awt.Color;

public class MainControlsPanelV1 extends OutlineButtonToCheckBoxRows {
//public class MainControlsPanel extends OutlineButtonsList {

    private OutlineButtonToCheckBoxRow mActionsButton;
    private OutlineButtonToCheckBoxRow mMoveButton;
    private OutlineButtonToCheckBoxRow mSettingsButton;
    private OutlineButtonToCheckBoxRow mEndTurnButton;
    private OutlineButtonToCheckBoxRow mTeamButton;
    private OutlineButtonToCheckBoxRow mExitButton;
    public MainControlsPanelV1(int x, int y, int width, int height, Color color) {
        super(width, height, color, 4);
        mActionsButton = this.createRow("Action", true);
        mMoveButton = this.createRow("Movement", true);

        mEndTurnButton = this.createRow("End Turn");
        mSettingsButton = this.createRow("Settings");
        mTeamButton = this.createRow("Team");
        mExitButton = this.createRow("Exit");
        setBounds(x, y, width, height);

    }


    public void gameUpdate(GameController gameController) {

        JSONObject currentTurnState = gameController.getCurrentUnitTurnStatus();

        boolean hasActed = currentTurnState.getBoolean(GameAPI.GET_CURRENT_UNIT_TURN_STATUS_HAS_ACTED);
        mActionsButton.getCheckBox().setSelected(hasActed);

        boolean hasMoved = currentTurnState.getBoolean(GameAPI.GET_CURRENT_UNIT_TURN_STATUS_HAS_MOVED);
        mMoveButton.getCheckBox().setSelected(hasMoved);

    }

    public JButton getActionsButton() { return mActionsButton.getButton(); }

    public JButton getMoveButton() { return mMoveButton.getButton(); }

    public JButton getTeamButton() { return mTeamButton.getButton(); }

    public JButton getExitButton() { return mExitButton.getButton(); }

    public JButton getSettingsButton() { return mSettingsButton.getButton(); }

    public JButton getEndTurnButton() { return mEndTurnButton.getButton(); }


}
