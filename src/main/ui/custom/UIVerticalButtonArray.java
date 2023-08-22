package main.ui.custom;

import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.graphics.JScene;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

public class UIVerticalButtonArray extends JScene {

    private final List<JButton> buttons = new ArrayList<>();
    private final GridBagConstraints constraints = new GridBagConstraints();

    public UIVerticalButtonArray(int width, int height) {
        super(width, height, UIVerticalButtonArray.class.getSimpleName());

        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;

        setLayout(new GridBagLayout());
    }

    public void addUIVerticalButton(JButton button) {
        constraints.gridy = buttons.size();
        buttons.add(button);
        button.setFont(FontPool.getInstance().getFont(15).deriveFont(Font.BOLD));
        add(button, constraints);
        revalidate();
        repaint();
    }
    @Override
    public void jSceneUpdate(GameModel model) {
        revalidate();
        repaint();
    }
}
