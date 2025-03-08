package main.game.main.ui;

import main.constants.SimpleCheckSum;
import main.constants.Tuple;
import main.game.main.GameControllerV1;
import main.game.stores.pools.action.AbilityDatabase;
import main.graphics.GameUI;
import main.ui.outline.OutlineLabel;
import main.ui.outline.OutlineTextArea;
import main.ui.outline.production.OutlineLabelToTextAreaRowsWithHeader;
import main.ui.outline.production.OutlineTextAreaToTextAreaRow;
import main.utils.StringUtils;
import org.json.JSONObject;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.Set;

public class MiniActionInfoPanelV1 extends GameUI {
    private int mImageWidth = 0;
    private int mImageHeight = 0;
    private JButton mImageButton = null;

    private int mValueWidth = 0;
    private int mValueHeight = 0;
    private JButton mValueButton = null;
    private SimpleCheckSum mSimpleCheckSum = new SimpleCheckSum();
//    private OutlineLabelToTextAreaRowsWithoutHeader mDataRows;
    private OutlineLabelToTextAreaRowsWithHeader mDataRows;
    private OutlineTextArea mDescriptionField = null;
    private JSONObject mEphemeralJsonRequest = new JSONObject();
    private static final int TEXT_THICKNESS = 2;


    public MiniActionInfoPanelV1(int width, int height, Color color) {
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
//
        mDataRows = new OutlineLabelToTextAreaRowsWithHeader(
                rowPanelWidth,
                rowPanelHeight,
                color,
                6
        );

//        mDataRows = new OutlineRowsWithHeader(
//                rowPanelWidth,
//                rowPanelHeight,
//                color,
//                6
//        );

//        int headerFontSize = (int) (mDataRows.getRowHeight() * 1.125f);
//        mDataRows.getHeaderField().setFont(FontPool.getInstance().getFontForHeight(headerFontSize));

//        mDataRows.getHeaderField().setText("yi");
//        mDataRows.getHeaderField().
//        mTitleRow = new OutlineButtonAndOutlineField(rowPanelWidth, rowPanelHeight, color);
//        mTitleRow.getTextField().setFont(FontPool.getInstance().getFontForHeight(rowPanelHeight));


//        mDataRows.createRow("title", mTitleRow);
//
//        mDescriptionField = new OutlineTextArea();
//        mDe
//        mDescriptionField.setBackground(Color.BLUE);
//        mDataRows.createRow("1", mDescriptionField);

        mDataRows.revalidate();
        mDataRows.repaint();



        mDataRows.getReturnButton().setVisible(false);
        mDataRows.getReturnButton().addActionListener(e -> {
            setVisible(false);
        });

        add(mDataRows);

        setBackground(color);
    }


//    public MiniActionInfoPanel(int width, int height, Color color) {
//        super(width, height);
//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//
//        int headerHeight = (int) (height * .2);
//        int headerWidth = width;
//        OutlineLabel outlineLabel = new OutlineLabel();
//        outlineLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        outlineLabel.setHorizontalTextPosition(SwingConstants.CENTER);
//        outlineLabel.setText("HEADER");
////        outlineLabel.
//        outlineLabel.setPreferredSize(new Dimension(headerWidth, headerHeight));
//        outlineLabel.setMinimumSize(new Dimension(headerWidth, headerHeight));
//        outlineLabel.setMaximumSize(new Dimension(headerWidth, headerHeight));
////        add(outlineLabel);
//
//        int rowPanelWidth = width;
//        int rowPanelHeight = height;
////
//        mDataRows = new OutlineLabelToTextAreaRowsWithHeader(
//                rowPanelWidth,
//                rowPanelHeight,
//                color,
//                6
//        );
//
////        mDataRows = new OutlineRowsWithHeader(
////                rowPanelWidth,
////                rowPanelHeight,
////                color,
////                6
////        );
//
////        int headerFontSize = (int) (mDataRows.getRowHeight() * 1.125f);
////        mDataRows.getHeaderField().setFont(FontPool.getInstance().getFontForHeight(headerFontSize));
//
////        mDataRows.getHeaderField().setText("yi");
////        mDataRows.getHeaderField().
////        mTitleRow = new OutlineButtonAndOutlineField(rowPanelWidth, rowPanelHeight, color);
////        mTitleRow.getTextField().setFont(FontPool.getInstance().getFontForHeight(rowPanelHeight));
//
//
////        mDataRows.createRow("title", mTitleRow);
////
////        mDescriptionField = new OutlineTextArea();
////        mDe
////        mDescriptionField.setBackground(Color.BLUE);
////        mDataRows.createRow("1", mDescriptionField);
//
//        mDataRows.revalidate();
//        mDataRows.repaint();
//
//
//
//        mDataRows.getReturnButton().setVisible(false);
//        mDataRows.getReturnButton().addActionListener(e -> {
//            setVisible(false);
//        });
//
//        add(mDataRows);
//
//        setBackground(color);
//    }


    public void gameUpdate(GameControllerV1 gameControllerV1, String action, String unit) {
        gameUpdate(gameControllerV1);

        if (action == null || action.isBlank()) {
            setVisible(false);
            return;
        }

        if (!mSimpleCheckSum.update("state", action, unit)) {
            return;
        }
        setVisible(true);

        mDataRows.clear();

        mDataRows.getHeaderLabel().setOutlineThickness(3);
        mDataRows.getHeaderLabel().setText(StringUtils.convertSnakeCaseToCapitalized(action));

        OutlineTextAreaToTextAreaRow row;

        row = mDataRows.createRow("0", TEXT_THICKNESS);
        row.setLeftLabelVisible(false);
        String description = AbilityDatabase.getInstance().getDescription(action);
        row.getRightTextArea().setTextAlignment(SwingConstants.CENTER);
        row.getRightTextArea().setWrapEnabled(true);
        row.setRightField(description);

//        row = mDataRows.createRow("-1", TEXT_THICKNESS);
//        row.setLeftLabel("Type");
//        row.setRightField(ActionDatabase.getInstance().getType(action).toString());

        Set<String> resources = AbilityDatabase.getInstance().getResourcesToDamage(action);
        StringBuilder sb = new StringBuilder();
        for (String resource : resources) {
            sb.delete(0, sb.length());
            List<Tuple<String,String, Float>> scalars = AbilityDatabase.getInstance().getResourceDamage(action, resource);

            int damage = 0;
            for (Tuple<String, String, Float> scaling : scalars) {
                if (!sb.isEmpty()) { sb.append(System.lineSeparator()); }

                if (scaling.getSecond() == null) {
                    int baseDamage = scaling.getThird().intValue();
                    damage += baseDamage;
                    sb.append(baseDamage)
                            .append(" Base");
                } else {
                    String magnitude = scaling.getFirst();
                    String attribute = scaling.getSecond();
                    Float value = scaling.getThird();

                    mEphemeralJsonRequest.clear();
                    mEphemeralJsonRequest.put("id", unit);
                    mEphemeralJsonRequest.put("attribute", attribute);
                    mEphemeralJsonRequest.put("scaling", magnitude);

                    int baseModifiedOrTotal = gameControllerV1.getUnitAttributeScaling(mEphemeralJsonRequest);

                    int additionalDamage = (int) (value * baseModifiedOrTotal);
                    damage += additionalDamage;

                    String prettyAttribute = StringUtils.convertSnakeCaseToCapitalized(attribute);
                    String prettyMagnitude = StringUtils.convertSnakeCaseToCapitalized(magnitude);
                    String prettyValue = StringUtils.floatToPercentage(value);

                    sb.append("(")
                            .append(additionalDamage)
                            .append(") ")
                            .append(prettyValue)
                            .append(" ")
                            .append(prettyMagnitude)
                            .append(" ")
                            .append(prettyAttribute);
                }
            }

            OutlineTextAreaToTextAreaRow calc = mDataRows.createRow(resource + " Damage Calculation", TEXT_THICKNESS);
            OutlineTextAreaToTextAreaRow form = mDataRows.createRow(resource + " Damage Formula", TEXT_THICKNESS);
            if (damage == 0) {
                calc.setVisible(false);
                form.setVisible(false);
                continue;
            }

            calc.setVisible(true);
            form.setVisible(true);

            String prettyResource = StringUtils.convertSnakeCaseToCapitalized(resource);
            calc.setLeftLabel(prettyResource + " Damage");
            calc.setRightField(damage + "");
//            calc.getTextArea().setAlignmentY(SwingConstants.CENTER);

            form.setLeftLabel("");
            form.getRightTextArea().setWrapEnabled(false);
            form.setRightField(sb.toString());
        }


        resources = AbilityDatabase.getInstance().getResourcesToCost(action);
        for (String resource : resources) {
            sb.delete(0, sb.length());
            List<Tuple<String,String, Float>> scalars = AbilityDatabase.getInstance().getResourceCost(action, resource);

            int cost = 0;
            for (Tuple<String, String, Float> scaling : scalars) {

                if (!sb.isEmpty()) { sb.append(System.lineSeparator()); }

                if (scaling.getSecond() == null) {
                    int baseDamage = scaling.getThird().intValue();
                    cost += baseDamage;
                    sb.append(baseDamage)
                            .append(" Base")
                            .append(System.lineSeparator());
                } else {
                    String magnitude = scaling.getFirst();
                    String attribute = scaling.getSecond();
                    Float value = scaling.getThird();

                    mEphemeralJsonRequest.clear();
                    mEphemeralJsonRequest.put("id", unit);
                    mEphemeralJsonRequest.put("attribute", attribute);
                    mEphemeralJsonRequest.put("scaling", magnitude);

                    int baseModifiedOrTotal = gameControllerV1.getUnitAttributeScaling(mEphemeralJsonRequest);

                    int additionalCost = (int) (value * baseModifiedOrTotal);
                    cost += additionalCost;

                    String prettyAttribute = StringUtils.convertSnakeCaseToCapitalized(attribute);
                    String prettyMagnitude = StringUtils.convertSnakeCaseToCapitalized(magnitude);
                    String prettyValue = StringUtils.floatToPercentage(value);

                    sb.append("(")
                            .append(additionalCost)
                            .append(") ")
                            .append(prettyValue)
                            .append(" ")
                            .append(prettyMagnitude)
                            .append(" ")
                            .append(prettyAttribute);
                }
            }

            OutlineTextAreaToTextAreaRow calc = mDataRows.createRow(resource + " Cost Calculation", TEXT_THICKNESS);
            OutlineTextAreaToTextAreaRow form = mDataRows.createRow(resource + " Cost Formula", TEXT_THICKNESS);

            if (cost == 0) {
                calc.setVisible(false);
                form.setVisible(false);
                continue;
            }

            calc.setVisible(true);
            form.setVisible(true);

            String prettyResource = StringUtils.convertSnakeCaseToCapitalized(resource);
            calc.setLeftLabel(prettyResource + " Cost");
            calc.setRightField(cost + "");

            form.setLeftLabel("");
            form.getRightTextArea().setWrapEnabled(false);
            form.setRightField(sb.toString());
        }


        row = mDataRows.createRow("1", TEXT_THICKNESS);
        row.setLeftLabel("Range");
        row.setRightField(AbilityDatabase.getInstance().getRange(action) + "");

        row = mDataRows.createRow("2", TEXT_THICKNESS);
        row.setLeftLabel("Area");
        row.setRightField(AbilityDatabase.getInstance().getArea(action) + "");

        row = mDataRows.createRow("3", TEXT_THICKNESS);
        row.setLeftLabel("Accuracy");
        row.setRightField(StringUtils.floatToPercentage(AbilityDatabase.getInstance().getAccuracy(action)));


        row = mDataRows.createRow("4", TEXT_THICKNESS);
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
