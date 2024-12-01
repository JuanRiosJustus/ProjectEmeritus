package main.game.main.ui;

import main.constants.StateLock;
import main.game.main.GameAPI;
import main.game.main.GameController;
import main.ui.outline.production.OutlineButtonToButtonRow;
import main.ui.outline.production.OutlineButtonToButtonRowsWithHeader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Color;

public class ActionsPanel extends OutlineButtonToButtonRowsWithHeader {
    private final StateLock mStateLock = new StateLock();
    private final JSONObject mEphemeralJsonObjectRequest = new JSONObject();
    public ActionsPanel(int width, int height, Color color) {
        super(width, height, color);

        createRow("Basic Attack");
        createRow("Spiral of Destiny");
        createRow("Fire Blast");
        createRow("Dig");
        createRow("Flamethrower");

        getTextField().setText("Actions");
        getTextField().setEditable(false);
    }

    @Override
    public void gameUpdate(GameController gameController) {

        String currentUnitsTurnID = gameController.getCurrentTurnsUnitID();
        if (currentUnitsTurnID == null) { return; }
        JSONArray actions = gameController.getActionsOfUnit(currentUnitsTurnID);

        if (!mStateLock.isUpdated("ACTIONS", actions)) { return; }
        clear();
        for (int i = 0; i < actions.length(); i++) {
            String action = actions.getString(i);
            OutlineButtonToButtonRow row = createRow(action,false);
            row.getLeftButton().setText(action);
            row.getLeftButton().addActionListener(e2 -> {
                gameController.setActionOfUnit(currentUnitsTurnID, action);
            });
        }
    }
}
