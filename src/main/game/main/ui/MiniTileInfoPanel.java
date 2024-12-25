package main.game.main.ui;

import main.constants.StateLock;
import main.game.components.tile.Tile;
import main.game.main.GameController;
import main.game.stores.pools.FontPool;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.graphics.GameUI;
import main.ui.outline.production.core.OutlineButton;
import org.json.JSONObject;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

public class MiniTileInfoPanel extends GameUI {
    private int mImageWidth = 0;
    private int mImageHeight = 0;
    private JButton mImageButton = null;

    private int mValueWidth = 0;
    private int mValueHeight = 0;
    private JButton mValueButton = null;
    private StateLock mStateLock = new StateLock();
    public MiniTileInfoPanel(int width, int height, Color color) {
        super(width, height);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        int imageSize = Math.min(width, height);
        mImageWidth = imageSize;
        mImageHeight = imageSize;
        mImageButton = new OutlineButton("");
//        mImageButton.setPreferredSize(new Dimension(mImageWidth, mImageHeight));
        mImageButton.setMinimumSize(new Dimension(mImageWidth, mImageHeight));
        mImageButton.setMaximumSize(new Dimension(mImageWidth, mImageHeight));
        mImageButton.setBackground(color);


        mValueWidth = width - mImageWidth;
        mValueHeight = mImageHeight;
        mValueButton = new OutlineButton();
        mValueButton.setText("");
//        mValueButton.setPreferredSize(new Dimension(mValueWidth, mValueHeight));
        mValueButton.setMinimumSize(new Dimension(mValueWidth, mValueHeight));
        mValueButton.setMaximumSize(new Dimension(mValueWidth, mValueHeight));
        mValueButton.setFont(FontPool.getInstance().getFontForHeight(mValueHeight));
        mValueButton.setBackground(color);

        add(mImageButton, BorderLayout.WEST);
        add(mValueButton, BorderLayout.CENTER);
    }

    public void gameUpdate(GameController gameController) {
        List<JSONObject> selectedTiles = gameController.getSelectedTiles();
        if (selectedTiles.isEmpty()) { setVisible(false); return; }
        Tile selectedTile = (Tile) selectedTiles.get(0);

        if (!mStateLock.isUpdated("IS_NEW_STATE_SELECTED", selectedTile)) {
            setVisible(true); return;
        }

        setVisible(true);
        String assetName = selectedTile.getTopLayerAsset();
        String id = AssetPool.getInstance().getOrCreateAsset(
                mImageWidth,
                mImageHeight,
                assetName,
                AssetPool.STATIC_ANIMATION,
                0,
                assetName + "_mini_tile_panel"
        );
        Asset asset = AssetPool.getInstance().getAsset(id);
        mImageButton.setIcon(new ImageIcon(asset.getAnimation().toImage()));
        mValueButton.setText(selectedTile.getBasicIdentityString() + " (" + selectedTile.getHeight() + ")");
    }

    public JButton getValueButton() { return mValueButton; }
}
