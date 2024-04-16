package main.ui.custom;

import main.game.main.GameModel;
import main.graphics.JScene;

import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

public class JButtonGrid extends JScene {
    private final GridBagConstraints mConstraints = new GridBagConstraints();
    private final Map<String, JButton> mComponentMap = new HashMap<>();
    private final int mWidth;
    private final int mHeight;

    public JButtonGrid(int width, int height) {
        super(width, height, JButtonGrid.class.getSimpleName());

        setLayout(new GridBagLayout());

        mConstraints.weightx = 1;
        mConstraints.weighty = 1;
        mConstraints.gridx = 0;
        mConstraints.gridy = 0;
        mConstraints.fill = GridBagConstraints.BOTH;
        mHeight = height;
        mWidth = width;
    }

    public void add(String[] names, int rows, int columns) {
        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                mConstraints.gridy = row;
                mConstraints.gridx = column;
                if (names.length > index) { return; }

                String name = names[index];
                JButton button = new JButton(name);
                button.setPreferredSize(new Dimension(mWidth / rows, mHeight / columns));
                mComponentMap.put(name, button);
                add(button, mConstraints);
                index++;
            }
        }
    }

    public JButton getButton(String key) { return mComponentMap.get(key); }

    @Override
    public void jSceneUpdate(GameModel model) {

    }
}
