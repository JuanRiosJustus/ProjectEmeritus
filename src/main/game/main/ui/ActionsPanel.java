package main.game.main.ui;

import main.constants.StateLock;
import main.game.main.GameController;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.production.*;
import main.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Color;

public class ActionsPanel extends OutlineButtonToButtonRowsWithHeader {
    private final StateLock mStateLock = new StateLock();
    private final JSONObject mEphemeralJsonObjectRequest = new JSONObject();
    private String mSelectedAction = null;
    private String mMonitoredEntity = null;
    public ActionsPanel(int width, int height, Color color) {
        super(width, height, color, 4);

        createRow("Basic Attack");
        createRow("Spiral of Destiny");
        createRow("Fire Blast");
        createRow("Dig");
        createRow("Flamethrower");

        mHeader.getTextField().setText("Actions");
        mHeader.getTextField().setEditable(false);
    }

    @Override
    public void gameUpdate(GameController gameController) {
        boolean isShowing = isShowing();
        gameController.setActionPanelIsOpen(isShowing);

        String unit = gameController.getCurrentUnitOnTurn();
        if (!mStateLock.isUpdated("ACTIONS", unit)) { return; }
        JSONArray actions = gameController.getActionsOfUnit(unit);

        clear();

        for (int i = 0; i < actions.length(); i++) {
            String action = actions.getString(i);
            OutlineButtonToButtonRow row = createRow(action);
            String capitalizedString = StringUtils.convertSnakeCaseToCapitalized(action);
//            row.getButton().setText(capitalizedString);
//            row.getCheckBox().setEnabled(false);
            row.getLeftButton().setText(capitalizedString);
            SwingUiUtils.setHoverEffect(row.getLeftButton());
            row.getLeftButton().addActionListener(e2 -> {


//                row.getCheckBox().setSelected(row.getCheckBox().i);
                mSelectedAction = action;
                mMonitoredEntity = unit;

                mEphemeralJsonObjectRequest.clear();
                mEphemeralJsonObjectRequest.put("id", unit);
                mEphemeralJsonObjectRequest.put("action", action);

                gameController.stageActionForUnit(mEphemeralJsonObjectRequest);
            });
        }
    }

    public String getMonitoredAction() { return mSelectedAction; }
    public String getMonitoredEntity() { return mMonitoredEntity; }
}
