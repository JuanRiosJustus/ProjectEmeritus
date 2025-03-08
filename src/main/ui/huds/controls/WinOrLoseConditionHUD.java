package main.ui.huds.controls;

import main.game.stores.pools.ColorPaletteV1;
import main.game.main.GameModel;
import main.game.stores.pools.FontPoolV1;
import main.graphics.GameUI;

import javax.swing.JButton;

public class WinOrLoseConditionHUD extends GameUI {

    private String mVictory = "ViCToRY!";
    private String mDefeat = "DeFeaT.";
    private JButton mMainLabel;
    public WinOrLoseConditionHUD(int width, int height) {
        super(width, height);

        setLayout(null);
        setBackground(ColorPaletteV1.getRandomColor());
        setOpaque(false);

        mMainLabel = new JButton(mDefeat);
        mMainLabel.setVisible(false);
        mMainLabel.setFont(FontPoolV1.getInstance().getFont(mMainLabel.getFont().getSize() * 10));
        mMainLabel.setBounds(width / 4, height / 4, width / 2, height / 2);
        add(mMainLabel);
    }



    @Override
    public void gameUpdate(GameModel model) {
        if (model.getSpeedQueue().teams() == 1) {
            mMainLabel.setVisible(true);
        }
    }
}
