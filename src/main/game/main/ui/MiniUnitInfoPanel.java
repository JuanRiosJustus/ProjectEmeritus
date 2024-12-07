package main.game.main.ui;

import main.constants.StateLock;
import main.game.main.GameController;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.ui.outline.OutlineButton;
import main.ui.outline.production.OutlineLabelToLabelRow;
import main.ui.outline.OutlineLabelToLabelRowsWithoutHeader;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

public class MiniUnitInfoPanel extends GameUI {

    private static final int DEFAULT_ROW_HEIGHT = 10;
    private int mHeaderPanelWidth = 0;
    private int mHeaderPanelHeight = 0;
    private int mUnitImageButtonWidth = 0;
    private int mUnitImageButtonHeight = 0;
    private JButton mUnitImageButton = null;

    private OutlineButton mFooterButton = new OutlineButton();
    private OutlineLabelToLabelRowsWithoutHeader mBodyContents = null;
    private OutlineButton mHeaderLabel = null;
    private StateLock mStateLock = new StateLock();
    public MiniUnitInfoPanel(int width, int height, Color color) {
        super(width, height);
//        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


        mHeaderPanelWidth = width;
        mHeaderPanelHeight = (int) (height * .35);
        JPanel headerRowPanel = new GameUI();
        headerRowPanel.setLayout(new BorderLayout());
        headerRowPanel.setPreferredSize(new Dimension(mHeaderPanelWidth, mHeaderPanelHeight));
        headerRowPanel.setBackground(color);


        mUnitImageButtonWidth = (int) (mHeaderPanelWidth * .25);
        mUnitImageButtonHeight = (mHeaderPanelHeight);
        mUnitImageButton = new OutlineButton();
        mUnitImageButton.setPreferredSize(new Dimension(mUnitImageButtonWidth, mUnitImageButtonHeight));
        mUnitImageButton.setBackground(color);

        headerRowPanel.add(mUnitImageButton, BorderLayout.WEST);


        int mHeaderLabelWidth = width - mUnitImageButtonWidth;
        int mHeaderLabelHeight = mUnitImageButtonHeight;

        mHeaderLabel = new OutlineButton();
        mHeaderLabel.setHorizontalTextPosition(JLabel.CENTER);
        mHeaderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mHeaderLabel.setFont(FontPool.getInstance().getFontForHeight(mHeaderLabelHeight));

        mHeaderLabel.setPreferredSize(new Dimension(mHeaderLabelWidth, mHeaderLabelHeight));
        mHeaderLabel.setMinimumSize(new Dimension(mHeaderLabelWidth, mHeaderLabelHeight));
        mHeaderLabel.setMaximumSize(new Dimension(mHeaderLabelWidth, mHeaderLabelHeight));
        mHeaderLabel.setBackground(color);

        headerRowPanel.add(mHeaderLabel, BorderLayout.CENTER);


//        int mHeaderLabelWidth = width - mUnitImageButtonWidth;
//        int mHeaderLabelHeight = mUnitImageButtonHeight / 2;
//        mHeaderLabel = new OutlineButton();
//        mHeaderLabel.setHorizontalTextPosition(JLabel.CENTER);
//        mHeaderLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        mHeaderLabel.setFont(FontPool.getInstance().getFontForHeight(mHeaderLabelHeight));
//
//        mHeaderLabel.setPreferredSize(new Dimension(mHeaderLabelWidth, mHeaderLabelHeight));
//        mHeaderLabel.setMinimumSize(new Dimension(mHeaderLabelWidth, mHeaderLabelHeight));
//        mHeaderLabel.setMaximumSize(new Dimension(mHeaderLabelWidth, mHeaderLabelHeight));
//        mHeaderLabel.setBackground(color);
//
//        int healthBarWidth = width - mUnitImageButtonWidth;
//        int healthBarHeight = mUnitImageButtonHeight / 2;
//        HealthBar healthBar = new HealthBar(100);
//        healthBar.setSize(new Dimension(healthBarWidth, healthBarHeight));
//        healthBar.setPreferredSize(new Dimension(healthBarWidth, healthBarHeight));
//        healthBar.setMinimumSize(new Dimension(healthBarWidth, healthBarHeight));
//        healthBar.setMaximumSize(new Dimension(healthBarWidth, healthBarHeight));
//
//
//        JPanel headerPanel = new JPanel();
//        headerPanel.setLayout(new BorderLayout());
//        headerPanel.setPreferredSize(new Dimension(mHeaderLabelWidth, mHeaderLabelHeight));
//        headerPanel.setMinimumSize(new Dimension(mHeaderLabelWidth, mHeaderLabelHeight));
//        headerPanel.setMaximumSize(new Dimension(mHeaderLabelWidth, mHeaderLabelHeight));
//
//        headerPanel.add(mHeaderLabel, BorderLayout.NORTH);
//        headerPanel.add(healthBar, BorderLayout.CENTER);

//        headerRowPanel.add(headerPanel, BorderLayout.CENTER);


        int bodyPanelWidth = width;
        int bodyPanelHeight = (int) (height * .8);
        mBodyContents = new OutlineLabelToLabelRowsWithoutHeader(bodyPanelWidth, bodyPanelHeight, color, 5);


        int footerButtonWidth = width;
        int footerButtonHeight = (int) (height * .0);
        mFooterButton = new OutlineButton();
//        mFooterButton.setText("tewf");
        mFooterButton.setFont(FontPool.getInstance().getFontForHeight(footerButtonHeight));
        mFooterButton.setPreferredSize(new Dimension(footerButtonWidth, footerButtonHeight));
        mFooterButton.setMinimumSize(new Dimension(footerButtonWidth, footerButtonHeight));
        mFooterButton.setMaximumSize(new Dimension(footerButtonWidth, footerButtonHeight));
        mFooterButton.setBackground(color);


        add(headerRowPanel);
        add(mBodyContents);
        add(mFooterButton);
    }

    public void gameUpdate(GameController gameController) {
        String unitAtSelectedTiles = gameController.getUnitAtSelectedTiles();
        if (unitAtSelectedTiles == null) {
            return;
        }
        if (!mStateLock.isUpdated("STATE_LOCK", unitAtSelectedTiles)) {
            setVisible(true);
            return;
        }

        setVisible(true);
        String assetName = gameController.getUnitName(unitAtSelectedTiles);
        String id = AssetPool.getInstance().getOrCreateAsset(
                mUnitImageButtonWidth,
                mUnitImageButtonHeight,
                assetName,
                AssetPool.STATIC_ANIMATION,
                0,
                assetName + "_mini_unit_panel"
        );
        Asset asset = AssetPool.getInstance().getAsset(id);
        mUnitImageButton.setIcon(new ImageIcon(asset.getAnimation().toImage()));


        String nickName = gameController.getNicknameOfID(unitAtSelectedTiles);
        mHeaderLabel.setText(nickName);


        JSONObject request = new JSONObject();
        request.put("id", unitAtSelectedTiles);
        JSONArray response = gameController.getUnitStatsForMiniUnitInfoPanel(request);

        mBodyContents.clear();
        for (int index = 0; index < response.length(); index++) {
            JSONArray stat = response.getJSONArray(index);

            String key = stat.getString(0);
            int base = stat.getInt(1);
            int modified = stat.getInt(2);

            OutlineLabelToLabelRow olr = mBodyContents.createRow(key);

            olr.setLeftLabel(key);
            olr.setRightLabel(base + " (" + (modified < 0 ? "-" : "+") + modified + ")");
        }
    }
}
