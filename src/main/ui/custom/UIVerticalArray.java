package main.ui.custom;

import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.graphics.GameUI;

import javax.swing.JButton;
import javax.swing.JComponent;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

public class UIVerticalArray extends GameUI {
    private final GridBagConstraints constraints = new GridBagConstraints();
    private final List<JComponent> components = new ArrayList<>();

    public UIVerticalArray(int width, int height) {
        super(width, height);

        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;

        setLayout(new GridBagLayout());
    }

    public void addUIVerticalButton(JButton button) {
        constraints.gridy = components.size();
        components.add(button);
        button.setFont(FontPool.getInstance().getFont(15).deriveFont(Font.BOLD));
        add(button, constraints);
        revalidate();
        repaint();
    }
    @Override
    public void gameUpdate(GameModel model) {
        revalidate();
        repaint();
    }
}
