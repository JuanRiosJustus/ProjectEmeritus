package main.game.main.ui;

import main.constants.Pair;
import main.game.main.GameControllerV1;
import main.utils.StringUtils;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.Color;

public class MovementSubPanel extends MainControlsStatisticsSubPanel {
    private static final String[] stats = new String[]{ "move", "climb", "jump", "speed" };
    public MovementSubPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height, color, visibleRows);
    }

    public void gameUpdate(GameControllerV1 gameControllerV1) {
        boolean isShowing = isShowing();
        gameControllerV1.setMovementPanelIsOpen(isShowing);

        String currentTurnsUnitID = gameControllerV1.getCurrentTurnsUnit();
        if (!mSimpleCheckSum.isUpdated("MOVES", currentTurnsUnitID)) { return; }

        mEphemeralObject.clear();
        mEphemeralObject.put("id", currentTurnsUnitID);
        JSONObject response = gameControllerV1.getStatisticsForUnit(mEphemeralObject);
        clear();

        for (String stat : stats) {
            JSONObject statData = response.getJSONObject(stat);
            int base = statData.getInt("base");
            int modified = statData.getInt("modified");

            Pair<JLabel, JLabel> rowData = getOrCreateRow(stat);
            JLabel left = rowData.getFirst();
            left.setText(StringUtils.convertSnakeCaseToCapitalized(stat));

            JLabel right = rowData.getSecond();
            String modifiedSign = (modified < 0 ? "-" : modified > 0 ? "+" : "");
            right.setText(base + " ( " + modifiedSign + Math.abs(modified) + " )");
        }
    }
}
