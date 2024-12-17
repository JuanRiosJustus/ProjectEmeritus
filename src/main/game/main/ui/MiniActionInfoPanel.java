package main.game.main.ui;

import main.constants.StateLock;
import main.constants.Tuple;
import main.game.main.GameController;
import main.game.stores.pools.action.ActionDatabase;
import main.graphics.GameUI;
import main.ui.outline.OutlineLabel;
import main.ui.outline.production.*;
import main.utils.StringUtils;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.util.List;
import java.util.Set;

public class MiniActionInfoPanel extends GameUI {
    private int mImageWidth = 0;
    private int mImageHeight = 0;
    private JButton mImageButton = null;

    private int mValueWidth = 0;
    private int mValueHeight = 0;
    private JButton mValueButton = null;
    private StateLock mStateLock = new StateLock();
//    private OutlineLabelToTextAreaRowsWithoutHeader mDataRows;
    private OutlineLabelToTextAreaRowsWithHeader mDataRows;
    private JSONObject mEphemeralJsonRequest = new JSONObject();
    private static final int TEXT_THICKNESS = 2;
    public MiniActionInfoPanel(int width, int height, Color color) {
        super(width, height);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        int headerHeight = (int) (height * .2);
        int headerWidth = width;
        OutlineLabel outlineLabel = new OutlineLabel();
        outlineLabel.setHorizontalAlignment(SwingConstants.CENTER);
        outlineLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        outlineLabel.setText("HEADER");
//        outlineLabel.
        outlineLabel.setPreferredSize(new Dimension(headerWidth, headerHeight));
        outlineLabel.setMinimumSize(new Dimension(headerWidth, headerHeight));
        outlineLabel.setMaximumSize(new Dimension(headerWidth, headerHeight));
//        add(outlineLabel);

        int rowPanelWidth = width;
        int rowPanelHeight = height;

        mDataRows = new OutlineLabelToTextAreaRowsWithHeader(
                rowPanelWidth,
                rowPanelHeight,
                color,
                6
        );
        mDataRows.getReturnButton().setVisible(false);
        mDataRows.getReturnButton().addActionListener(e -> {
            setVisible(false);
        });

        add(mDataRows);

        setBackground(color);
    }

    public void gameUpdate(GameController gameController, String monitoredAction, String monitoredUnit) {
        gameUpdate(gameController);

        if (monitoredAction == null || monitoredAction.isBlank()) {
            setVisible(false);
            return;
        }

        if (!mStateLock.isUpdated("state", monitoredAction, monitoredUnit)) {
            return;
        }
        setVisible(true);

//        mDataRows.clear();

        mDataRows.getHeaderLabel().setOutlineThickness(3);
        mDataRows.getHeaderLabel().setText(StringUtils.convertSnakeCaseToCapitalized(monitoredAction));

        OutlineLabelToTextAreaRow row;

        row = mDataRows.createRow("0", TEXT_THICKNESS);
        row.setLeftLabelVisible(false);
        String description = ActionDatabase.getInstance().getDescription(monitoredAction);
        row.getTextArea().setTextAlignment(SwingConstants.CENTER);
        row.setRightField(description);

        row = mDataRows.createRow("-1", TEXT_THICKNESS);
        row.setLeftLabel("Type");
        row.setRightField(ActionDatabase.getInstance().getType(monitoredAction).toString());

        Set<String> resources = ActionDatabase.getInstance().getResourcesToDamage(monitoredAction);
        for (String resource : resources) {

            int baseDamage = ActionDatabase.getInstance().getBaseDamage(monitoredAction, resource);
            List<Tuple<String, String, Float>> scalings = ActionDatabase.getInstance().getScalingDamageFromUser(monitoredAction, resource);
            int totalDamage = baseDamage;
            for (Tuple<String, String, Float> scaling : scalings) {
                String magnitude = scaling.getFirst();
                String attribute = scaling.getSecond();
                Float value = scaling.getThird();

                mEphemeralJsonRequest.clear();
                mEphemeralJsonRequest.put("id", monitoredUnit);
                mEphemeralJsonRequest.put("attribute", attribute);
                mEphemeralJsonRequest.put("scaling", magnitude);

                int scaling1 = gameController.getUnitAttributeScaling(mEphemeralJsonRequest);
                int additionalDamage = (int) (value * scaling1);
                totalDamage += additionalDamage;
            }

            row = mDataRows.createRow(resource + " Damage", TEXT_THICKNESS);
            String prettyResource = StringUtils.convertSnakeCaseToCapitalized(resource);
            row.setLeftLabel(prettyResource + " Damage");
            row.setRightField(totalDamage + "");
        }


        row = mDataRows.createRow("1", TEXT_THICKNESS);
        row.setLeftLabel("Range");
        row.setRightField(ActionDatabase.getInstance().getRange(monitoredAction) + "");

        row = mDataRows.createRow("2", TEXT_THICKNESS);
        row.setLeftLabel("Area");
        row.setRightField(ActionDatabase.getInstance().getArea(monitoredAction) + "");

        row = mDataRows.createRow("3", TEXT_THICKNESS);
        row.setLeftLabel("Accuracy");
        row.setRightField(StringUtils.floatToPercentage(ActionDatabase.getInstance().getAccuracy(monitoredAction)));

        resources = ActionDatabase.getInstance().getResourcesToDamage(monitoredAction);
//        for (String resource : resources) {
//
//            int baseDamage = ActionDatabase.getInstance().getBaseDamage(monitoredAction, resource);
//            List<Tuple<String, String, Float>> scalings = ActionDatabase.getInstance().getScalingDamageFromUser(monitoredAction, resource);
//            int totalDamage = baseDamage;
//            for (Tuple<String, String, Float> scaling : scalings) {
//                String magnitude = scaling.getFirst();
//                String attribute = scaling.getSecond();
//                Float value = scaling.getThird();
//
//                mEphemeralJsonRequest.clear();
//                mEphemeralJsonRequest.put("id", monitoredUnit);
//                mEphemeralJsonRequest.put("attribute", attribute);
//                mEphemeralJsonRequest.put("scaling", magnitude);
//
//                int scaling1 = gameController.getUnitAttributeScaling(mEphemeralJsonRequest);
//                int additionalDamage = (int) (value * scaling1);
//                totalDamage += additionalDamage;
//            }
//
//            row = mDataRows.createRow(resource + " Damage", TEXT_THICKNESS);
//            String prettyResource = StringUtils.convertSnakeCaseToCapitalized(resource);
//            row.setLeftLabel(prettyResource + " Damage");
//            row.setRightField(totalDamage + "");
//        }
        
        for (String resource : resources) {
            row = mDataRows.createRow(resource + " Damage Formula", TEXT_THICKNESS);
            row.setLeftLabel("");
            row.getTextArea().setWrapEnabled(false);
            row.setRightField(ActionDatabase.getInstance().getDamageCalculations(monitoredAction));
        }

        Set<String> costs = ActionDatabase.getInstance().getResourcesToCost(monitoredAction);
        for (String cost : costs) {
            List<Tuple<String, String, Float>> scalingCosts = ActionDatabase.getInstance().getScalingCostFromUser(monitoredAction, cost);
            int baseCost = ActionDatabase.getInstance().getBaseCost(monitoredAction, cost);
            int totalCost = baseCost;
            for (Tuple<String, String, Float> scalingCost : scalingCosts) {
                String magnitude = scalingCost.getFirst();
                String attribute = scalingCost.getSecond();
                Float value = scalingCost.getThird();

                mEphemeralJsonRequest.clear();
                mEphemeralJsonRequest.put("id", monitoredUnit);
                mEphemeralJsonRequest.put("attribute", attribute);
                mEphemeralJsonRequest.put("scaling", magnitude);

                int scaling = gameController.getUnitAttributeScaling(mEphemeralJsonRequest);
                int additionalCost = (int) (scaling * value);
                totalCost += additionalCost;
            }

            row = mDataRows.createRow(cost + " Cost", TEXT_THICKNESS);
            String prettyResource = StringUtils.convertSnakeCaseToCapitalized(cost);
            row.setLeftLabel(prettyResource + " Cost");
            row.setRightField(totalCost + " ");
        }

        for (String cost : costs) {
            row = mDataRows.createRow(cost + " Cost Formula", TEXT_THICKNESS);
            row.setLeftLabel("");
            row.getTextArea().setWrapEnabled(false);
            row.setRightField(ActionDatabase.getInstance().getResourceCalculations(monitoredAction));
        }

        row = mDataRows.createRow("4", TEXT_THICKNESS);
        row.setLeftLabel("Is Damaging?");
        row.setRightField(ActionDatabase.getInstance().isDamagingAbility(monitoredAction) + "");
        System.out.println("MINI ACTION INFO PANEL UPDATED");

    }
    public void gameUpdate(GameController gameController) {

//        String currentUnitsTurnID = gameController.getCurrentTurnsUnitID();
//        if (!mStateLock.isUpdated("ACTIONS", currentUnitsTurnID)) { return; }


//        List<JSONObject> selectedTiles = gameController.getSelectedTiles();
//        if (selectedTiles.isEmpty()) { setVisible(false); return; }
//        Tile selectedTile = (Tile) selectedTiles.get(0);
//
//        if (!mStateLock.isUpdated("IS_NEW_STATE_SELECTED", selectedTile)) {
//            setVisible(true); return;
//        }
//
//        setVisible(true);
//        String assetName = selectedTile.getTopLayerAsset();
//        String id = AssetPool.getInstance().getOrCreateAsset(
//                mImageWidth,
//                mImageHeight,
//                assetName,
//                AssetPool.STATIC_ANIMATION,
//                0,
//                assetName + "_mini_tile_panel"
//        );
//        Asset asset = AssetPool.getInstance().getAsset(id);
//        mImageButton.setIcon(new ImageIcon(asset.getAnimation().toImage()));
//        mValueButton.setText(selectedTile.getBasicIdentityString() + " (" + selectedTile.getHeight() + ")");
    }

    public JButton getValueButton() { return mValueButton; }
}
