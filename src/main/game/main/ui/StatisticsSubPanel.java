package main.game.main.ui;

import main.constants.Pair;
import main.constants.SimpleCheckSum;
import main.game.main.GameController;
import main.utils.StringUtils;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class StatisticsSubPanel extends MainControlsStatisticsSubPanel {
    private static final String[] stats = new String[]{
            "health",
            "mana",
            "stamina",
            "physical_attack",
            "physical_defense",
            "magical_attack",
            "magical_defense",
            "move",
            "climb",
            "jump",
            "speed"
    };
    public StatisticsSubPanel(int x, int y, int width, int height, Color color, int visibleRows) {
        super(x, y, width, height, color, visibleRows);
    }

    public void gameUpdate(GameController gameController) {
        boolean isShowing = isShowing();
        gameController.setStatisticsPanelIsOpen(isShowing);

        String currentTurnsUnitID = gameController.getCurrentTurnsUnit();
        if (!mSimpleCheckSum.update(currentTurnsUnitID)) { return; }

        mEphemeralObject.clear();
        mEphemeralObject.put("id", currentTurnsUnitID);
        JSONObject response = gameController.getStatisticsForUnit(mEphemeralObject);
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
