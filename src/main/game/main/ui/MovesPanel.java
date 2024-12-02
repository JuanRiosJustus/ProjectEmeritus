package main.game.main.ui;

import main.constants.StateLock;
import main.game.main.GameController;
import main.ui.outline.OutlineLabelToLabelRowsWithHeader;
import main.ui.outline.production.OutlineLabelToLabelRow;
import org.json.JSONObject;

import java.awt.Color;

public class MovesPanel extends OutlineLabelToLabelRowsWithHeader {
    private final StateLock mStateLock = new StateLock();
    public MovesPanel(int width, int height, Color color, int visibleRows) {
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
//        System.out.println("Is moves panel open?: " + isShowing);
        gameController.setMovementPanelIsOpen(isShowing);

        String currentTurnsUnitID = gameController.getCurrentTurnsUnitID();
        if (!mStateLock.isUpdated("MOVES", currentTurnsUnitID)) { return; }
        JSONObject movementStats = gameController.getMovementStatsOfUnit(currentTurnsUnitID);

        clear();

        for (String key : movementStats.keySet()) {
            int value = movementStats.getInt(key);
            OutlineLabelToLabelRow olr = createRow(key + ":");
            olr.setLeftLabel(key);
            olr.setRightLabel(String.valueOf(value));
        }
    }
}
