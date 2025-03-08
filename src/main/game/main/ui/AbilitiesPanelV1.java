package main.game.main.ui;

import main.constants.SimpleCheckSum;
import main.game.main.GameControllerV1;
import main.ui.custom.SwingUiUtils;
import main.ui.outline.production.OutlineButtonToButtonRow;
import main.ui.outline.production.OutlineButtonToButtonRowsWithHeader;
import main.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Color;

public class AbilitiesPanelV1 extends OutlineButtonToButtonRowsWithHeader {
    private final SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();
    private final JSONObject mEphemeralJsonObjectRequest = new JSONObject();
    private String mSelectedAction = null;
    private String mMonitoredEntity = null;
    public AbilitiesPanelV1(int width, int height, Color color) {
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
    public void gameUpdate(GameControllerV1 gameControllerV1) {
        boolean isShowing = isShowing();
        gameControllerV1.setActionPanelIsOpen(isShowing);

        String unit = gameControllerV1.getCurrentTurnsUnit();
        if (!mSimpleCheckSum.update("ACTIONS", unit)) { return; }
        JSONArray actions = gameControllerV1.getActionsOfUnit(unit);

        clear();
        mSelectedAction = null;
        mMonitoredEntity = null;

        for (int i = 0; i < actions.length(); i++) {
            String action = actions.getString(i);
            OutlineButtonToButtonRow row = createRow(action + " " + i);
            String capitalizedString = StringUtils.convertSnakeCaseToCapitalized(action);
//            row.getButton().setText(capitalizedString);
//            row.getCheckBox().setEnabled(false);



            row.getLeftButton().setText("*");
            row.getRightButton().setText(capitalizedString);
//            row.getRightButton().setText(capitalizedString);
            SwingUiUtils.setHoverEffect(row.getLeftButton());
            row.getRightButton().addActionListener(e2 -> {


//                row.getCheckBox().setSelected(row.getCheckBox().i);
                mSelectedAction = action;
                mMonitoredEntity = unit;

                mEphemeralJsonObjectRequest.clear();
                mEphemeralJsonObjectRequest.put("id", unit);
                mEphemeralJsonObjectRequest.put("action", action);

                gameControllerV1.stageActionForUnit(mEphemeralJsonObjectRequest);
            });
        }
    }

    public String getMonitoredAction() { return mSelectedAction; }
    public String getMonitoredEntity() { return mMonitoredEntity; }
}
