package main.ui.huds.controls.v2;

import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.ui.huds.controls.HUD;

import javax.swing.JComponent;
import java.awt.CardLayout;
import java.awt.Dimension;

public class BlankHoldingPane extends HUD {
    private JComponent mCurrentComponent = null;

    public BlankHoldingPane(int width, int height, int x, int y) {
        super(width, height, x, y);
        setLayout(new CardLayout());
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));

        setOpaque(true);
        setBackground(ColorPalette.TRANSPARENT);
    }

    public void setup(JComponent component) {
        if (component == mCurrentComponent) {
            return;
        }
        mCurrentComponent = component;
//        removeAll();
        add(component, "");
        component.setPreferredSize(getPreferredSize());
        component.setMinimumSize(getMinimumSize());
        component.setMaximumSize(getMaximumSize());
    }

    @Override
    public void jSceneUpdate(GameModel model) {

    }
}
