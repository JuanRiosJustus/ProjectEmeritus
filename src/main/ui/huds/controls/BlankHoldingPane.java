package main.ui.huds.controls;

import main.game.main.GameModel;
import main.graphics.JScene;
import main.ui.custom.SwingUiUtils;

import javax.swing.JComponent;
import java.awt.CardLayout;

public class BlankHoldingPane extends JScene {
    private JComponent mCurrentComponent = null;

    public BlankHoldingPane(int width, int height, int x, int y) {
        super(width, height, x, y, BlankHoldingPane.class.getSimpleName());
        setLayout(new CardLayout());

        setVisible(false);
        setOpaque(true);
        setLocation(x, y);
        setPreferredLocation(x, y);
        SwingUiUtils.setStylizedRaisedBevelBorder(this);
    }

    public void setup(JComponent component) {
        if (component == mCurrentComponent) {
            return;
        }
        mCurrentComponent = component;

        add(component, "");
        component.setPreferredSize(getPreferredSize());
        component.setMinimumSize(getMinimumSize());
        component.setMaximumSize(getMaximumSize());
    }

    @Override
    public void jSceneUpdate(GameModel model) {

    }
}
