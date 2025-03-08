package main.game.main.ui;

import main.constants.SimpleCheckSum;
import main.game.main.GameControllerV1;
import main.ui.outline.production.OutlineLabelToLabelRow;
import main.ui.outline.production.OutlineLabelToLabelRowsWithHeader;
import main.utils.StringUtils;
import org.json.JSONObject;

import java.awt.Color;

public class MovementPanelV1 extends OutlineLabelToLabelRowsWithHeader {
    private final SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();
    public MovementPanelV1(int width, int height, Color color, int visibleRows) {
        super(width, height, color, visibleRows);

        createRow("Move:");
        createRow("Speed:");
        createRow("Climb:");
        createRow("Jump:");
        getTextField().setText("Movement");
        getTextField().setEditable(false);
    }

    public MovementPanelV1(int x, int y, int width, int height, Color color, int visibleRows) {
        super(width, height, color, visibleRows);

        createRow("Move:");
        createRow("Speed:");
        createRow("Climb:");
        createRow("Jump:");
        getTextField().setText("Movement");
        getTextField().setEditable(false);
    }

    public void gameUpdate(GameControllerV1 gameControllerV1) {
        boolean isShowing = isShowing();
        gameControllerV1.setMovementPanelIsOpen(isShowing);

        String currentTurnsUnitID = gameControllerV1.getCurrentTurnsUnit();
        if (!mSimpleCheckSum.update("MOVES", currentTurnsUnitID)) { return; }
        JSONObject movementStats = gameControllerV1.getMovementStatsOfUnit(currentTurnsUnitID);

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
