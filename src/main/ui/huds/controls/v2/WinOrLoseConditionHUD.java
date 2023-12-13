package main.ui.huds.controls.v2;

import main.constants.ColorPalette;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.ui.huds.controls.HUD;

import javax.swing.JButton;
import javax.swing.JLabel;

public class WinOrLoseConditionHUD extends HUD {

    private String mVictory = "ViCToRY!";
    private String mDefeat = "DeFeaT.";
    private JButton mMainLabel;
    public WinOrLoseConditionHUD(int width, int height) {
        super(width, height, WinOrLoseConditionHUD.class.getSimpleName());

        setLayout(null);
        setBackground(ColorPalette.getRandomColor());
        setOpaque(false);

        mMainLabel = new JButton(mDefeat);
        mMainLabel.setVisible(false);
        mMainLabel.setFont(FontPool.getInstance().getFont(mMainLabel.getFont().getSize() * 10));
        mMainLabel.setBounds(width / 4, height / 4, width / 2, height / 2);
        add(mMainLabel);
    }



    @Override
    public void jSceneUpdate(GameModel model) {
        if (model.speedQueue.teams() == 1) {
            mMainLabel.setVisible(true);
        }
    }
}
