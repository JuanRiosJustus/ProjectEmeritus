package main.game.main.ui;

import main.constants.StateLock;
import main.game.main.GameController;
import main.ui.outline.production.OutlineLabelToLabelRowsWithHeader;
import main.ui.outline.production.OutlineLabelToLabelRow;
import main.utils.StringUtils;
import org.json.JSONObject;

import java.awt.Color;

public class MovementPanel extends OutlineLabelToLabelRowsWithHeader {
    private final StateLock mStateLock = new StateLock();
    public MovementPanel(int width, int height, Color color, int visibleRows) {
        super(width, height, color, visibleRows);

        createRow("Move:");
        createRow("Speed:");
        createRow("Climb:");
        createRow("Jump:");
        getTextField().setText("Movement");
        getTextField().setEditable(false);
    }

    public MovementPanel(int x, int y, int width, int height, Color color, int visibleRows) {
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

        String currentTurnsUnitID = gameController.getCurrentUnitOnTurn();
        if (!mStateLock.isUpdated("MOVES", currentTurnsUnitID)) { return; }
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
