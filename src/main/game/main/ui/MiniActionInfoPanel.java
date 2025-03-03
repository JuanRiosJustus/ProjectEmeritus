package main.game.main.ui;

import main.constants.SimpleCheckSum;
import main.game.entity.Entity;
import main.game.main.GameControllerV1;
import main.game.stores.factories.EntityStore;
import main.game.stores.pools.action.AbilityDatabase;
import main.graphics.GameUI;
import main.ui.outline.production.*;
import main.utils.StringUtils;

import javax.swing.*;
import java.awt.Color;
import java.util.Set;

public class MiniActionInfoPanel extends GameUI {
    private JButton mValueButton = null;
    private SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();
    private OutlineLabelToTextAreaRowsWithHeader mDataRows;
    private static final int TEXT_THICKNESS = 2;

    public MiniActionInfoPanel(int width, int height, Color color) {
        super(width, height);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        int rowPanelWidth = width;
        int rowPanelHeight = height;

        mDataRows = new OutlineLabelToTextAreaRowsWithHeader(
                rowPanelWidth,
                rowPanelHeight,
                color,
                4
        );


        mDataRows.revalidate();
        mDataRows.repaint();


        mDataRows.getReturnButton().setVisible(false);
        mDataRows.getReturnButton().addActionListener(e -> {
            setVisible(false);
        });

        add(mDataRows);

        setBackground(color);
    }


    public void gameUpdate(GameControllerV1 gameControllerV1, String action, String unit) {
        gameUpdate(gameControllerV1);

        if (action == null || action.isBlank()) {
            setVisible(false);
            return;
        }

        if (!mSimpleCheckSum.isUpdated("state", action, unit)) {
            setVisible(true);
            return;
        }
        setVisible(true);

        mDataRows.clear();

        mDataRows.getHeaderLabel().setOutlineThickness(3);
        mDataRows.getHeaderLabel().setText(StringUtils.convertSnakeCaseToCapitalized(action));

        OutlineTextAreaToTextAreaRow row;

        row = mDataRows.createRow("Description", TEXT_THICKNESS);
        row.setLeftLabelVisible(false);
        String description = AbilityDatabase.getInstance().getDescription(action);
        row.getRightTextArea().setTextAlignment(SwingConstants.CENTER);
        row.setRightField(description);

        row = mDataRows.createRow("Type", TEXT_THICKNESS);
        row.setLeftLabel("Type");
        row.setRightField(AbilityDatabase.getInstance().getType(action).toString());



        Set<String> resourcesToTarget = AbilityDatabase.getInstance().getResourcesToDamage(action);
        for (String resource : resourcesToTarget) {
            OutlineTextAreaToTextAreaRow calc = mDataRows.createRow(resource + " Damage Calculation", TEXT_THICKNESS);
            String prettyResourceLabel = StringUtils.convertSnakeCaseToCapitalized(resource);
            calc.setLeftLabel(prettyResourceLabel + " Damage");
            Entity unitEntity = EntityStore.getInstance().get(unit);
            int totalDamage = 0; //AbilityDatabase.getInstance().getTotalDamage(unitEntity, action, resource);
            calc.setRightField(String.valueOf(totalDamage));

            OutlineTextAreaToTextAreaRow form = mDataRows.createRow(resource + " Damage Formula", TEXT_THICKNESS);
            form.setLeftLabelVisible(false);
            String formula = ""; //AbilityDatabase.getInstance().getTotalDamageFormula(unitEntity, action, resource);
            form.getRightTextArea().setWrapEnabled(true);
            form.getRightTextArea().setWrapStyleWord(true);
            form.setRightField(formula);
        }

        resourcesToTarget = AbilityDatabase.getInstance().getResourcesToCost(action);
        for (String resource : resourcesToTarget) {
            OutlineTextAreaToTextAreaRow calc = mDataRows.createRow(resource + " Cost Calculation", TEXT_THICKNESS);
            String prettyResourceLabel = StringUtils.convertSnakeCaseToCapitalized(resource);
            calc.setLeftLabel(prettyResourceLabel + " Cost");
            Entity unitEntity = EntityStore.getInstance().get(unit);
            int totalCost = 0; //AbilityDatabase.getInstance().getTotalCost(unitEntity, action, resource);
            calc.setRightField(String.valueOf(totalCost));

            OutlineTextAreaToTextAreaRow form = mDataRows.createRow(resource + " Cost Formula", TEXT_THICKNESS);
            form.setLeftLabelVisible(false);
            String formula = "";//AbilityDatabase.getInstance().getTotalCostFormula(unitEntity, action, resource);
            form.getRightTextArea().setWrapEnabled(true);
            form.getRightTextArea().setWrapStyleWord(true);
            form.setRightField(formula);
        }


        row = mDataRows.createRow("Range", TEXT_THICKNESS);
        row.setLeftLabel("Range");
        row.setRightField(AbilityDatabase.getInstance().getRange(action) + "");

        row = mDataRows.createRow("Area", TEXT_THICKNESS);
        row.setLeftLabel("Area");
        row.setRightField(AbilityDatabase.getInstance().getArea(action) + "");

        row = mDataRows.createRow("Accuracy", TEXT_THICKNESS);
        row.setLeftLabel("Accuracy");
        row.setRightField(StringUtils.floatToPercentage(AbilityDatabase.getInstance().getAccuracy(action)));


        row = mDataRows.createRow("makes contact", TEXT_THICKNESS);
        row.setLeftLabel("Makes Contact?");
        row.setRightField(AbilityDatabase.getInstance().getMakesPhysicalContact(action) + "");

        row = mDataRows.createRow("Is Damaging", TEXT_THICKNESS);
        row.setLeftLabel("Is Damaging?");
        row.setRightField(AbilityDatabase.getInstance().isDamagingAbility(action) + "");
        System.out.println("MINI ACTION INFO PANEL UPDATED");
    }

    public void gameUpdate(GameControllerV1 gameControllerV1) {

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
