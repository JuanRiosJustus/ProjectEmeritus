package main.game.main.ui;

import main.constants.SimpleCheckSum;
import main.game.main.GameController;
import main.ui.outline.production.OutlineLabelToLabelRow;
import main.ui.outline.production.OutlineLabelToLabelRowsWithHeader;
import main.utils.StringUtils;
import org.json.JSONObject;

import java.awt.Color;

public class MovesPanelV1 extends OutlineLabelToLabelRowsWithHeader {
    private final SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();
    public MovesPanelV1(int width, int height, Color color, int visibleRows) {
        super(width, height, color, visibleRows);

        createRow("Move:");
        createRow("Speed:");
        createRow("Climb:");
        createRow("Jump:");
        getTextField().setText("Movement");
        getTextField().setEditable(false);
    }

    public void gameUpdate(GameController gameController) {
        boolean isShowing = isShowing();
        gameController.setMovementPanelIsOpen(isShowing);

        String currentTurnsUnitID = gameController.getCurrentTurnsUnit();
        if (!mSimpleCheckSum.isUpdated("MOVES", currentTurnsUnitID)) { return; }
        JSONObject movementStats = gameController.getMovementStatsOfUnit(currentTurnsUnitID);

        clear();

        for (String key : movementStats.keySet()) {
            int value = movementStats.getInt(key);
            OutlineLabelToLabelRow olr = createRow(key + ":");
            String prettyKey = StringUtils.convertSnakeCaseToCapitalized(key);
            olr.setLeftLabel(prettyKey);
            olr.setRightLabel(String.valueOf(value));
        }
    }
}
