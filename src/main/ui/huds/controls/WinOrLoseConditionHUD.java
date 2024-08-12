package main.ui.huds.controls;

import main.game.stores.pools.ColorPalette;
import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.graphics.JScene;

import javax.swing.JButton;

public class WinOrLoseConditionHUD extends JScene {

    private String mVictory = "ViCToRY!";
    private String mDefeat = "DeFeaT.";
    private JButton mMainLabel;
    public WinOrLoseConditionHUD(int width, int height) {
        super(width, height, 0, 0, WinOrLoseConditionHUD.class.getSimpleName());

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
        if (model.mSpeedQueue.teams() == 1) {
            mMainLabel.setVisible(true);
        }
    }
}
