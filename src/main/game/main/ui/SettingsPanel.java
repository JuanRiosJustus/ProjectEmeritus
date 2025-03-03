package main.game.main.ui;

import main.game.main.GameControllerV1;
import main.game.stores.pools.FontPool;
import main.ui.outline.production.OutlineLabelToCheckBoxRow;
import main.ui.outline.production.OutlineLabelToDropDownRow;
import main.ui.outline.production.OutlineRowsWithHeader;

import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import java.awt.Color;

public class SettingsPanel extends OutlineRowsWithHeader {
    private OutlineLabelToCheckBoxRow mShowMovementPathingButton;
    private OutlineLabelToCheckBoxRow mShowActionPathingButton;
    private OutlineLabelToDropDownRow mAnimationSpeedLabel;
    private OutlineLabelToCheckBoxRow mTurnOffAnimationsButton;
    private int fontHeightMultiplier = 0;
    public SettingsPanel(int width, int height, Color color) {
        super(width, height, color);


        fontHeightMultiplier = height / 5;

        mShowMovementPathingButton = createCheckBoxLabelRow(1, "Show Movement Pathing");
        mShowActionPathingButton = createCheckBoxLabelRow(2, "Show Action Pathing");
        mTurnOffAnimationsButton = createCheckBoxLabelRow(3, "Turn Off Animations");
        mAnimationSpeedLabel = createTextFieldLabelRow(4, "Animation Speed");

        getHeaderField().setText("Settings");
    }

    private OutlineLabelToCheckBoxRow createCheckBoxLabelRow(int index, String wording) {
        OutlineLabelToCheckBoxRow row = new OutlineLabelToCheckBoxRow(getRowWidth(), getRowHeight(), mColor);
        row.getLeftLabel().setText(wording);
        row.getCheckBox().setHorizontalTextPosition(SwingConstants.CENTER);
        row.getCheckBox().setHorizontalAlignment(SwingConstants.CENTER);
        row.getCheckBox().setBorder(BorderFactory.createLoweredSoftBevelBorder());
        row.getLeftLabel().setFont(FontPool.getInstance().getFontForHeight(fontHeightMultiplier));
        createRow(index + "", row);
        return row;
    }

    private OutlineLabelToDropDownRow createTextFieldLabelRow(int index, String wording) {
        OutlineLabelToDropDownRow row = new OutlineLabelToDropDownRow(getRowWidth(), getRowHeight(), mColor);
        row.getLabel().setFont(FontPool.getInstance().getFontForHeight(fontHeightMultiplier));
        row.getLabel().setText(wording);
        row.getDropDown().addItem("1");
        row.getDropDown().addItem("2");
        row.getDropDown().addItem("3");
        row.getDropDown().addItem("4");
        row.getDropDown().addItem("5");
//        row.getCheckBox().setHorizontalAlignment(SwingConstants.CENTER);
        createRow(index + "", row);
        return row;
    }

    @Override
    public void gameUpdate(GameControllerV1 gameControllerV1) {

//        String id = "Show Movement Pathing";
//        OutlineLabelRow row = createRow(id, true);
//        OutlineCheckBox checkBox = getCheckBox(id);
//        gameController.getModel().getGameDataStore().setShouldShowMovementRanges(checkBox.isSelected());
//
//        id = "Show Action Pathing";
//        row = createRow(id, true);
//        checkBox = getCheckBox(id);
//        gameController.getModel().getGameDataStore().setShouldShowActionRanges(checkBox.isSelected());
//
//        id = "Fast Forward Turns";
//        row = createRow(id, true);
//        checkBox = getCheckBox(id);
//        gameController.getModel().getGameDataStore().setShouldFastForwardTurns(checkBox.isSelected());
//
//        id = "Other";
//        row = createRow(id, true);
//        checkBox = getCheckBox(id);
//        gameController.getModel().getGameDataStore().setShouldFastForwardTurns(checkBox.isSelected());


    }
}
