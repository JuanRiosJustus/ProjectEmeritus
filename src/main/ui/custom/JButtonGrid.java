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
    private final int mRows;
    private final int mColumns;

    public JButtonGrid(int width, int height, int rows, int columns) {
        super(width, height, JButtonGrid.class.getSimpleName());

        setLayout(new GridBagLayout());

        mConstraints.weightx = 1;
        mConstraints.weighty = 1;
        mConstraints.gridx = 0;
        mConstraints.gridy = 0;
        mConstraints.fill = GridBagConstraints.BOTH;
        mRows = rows;
        mColumns = columns;
        mHeight = height;
        mWidth = width;

    }

    public void add(String[] names) {
        int index = 0;
        for (int row = 0; row < mRows; row++) {
            for (int column = 0; column < mColumns; column++) {
                mConstraints.gridy = row;
                mConstraints.gridx = column;

                String name = names[index++];
                JButton button = new JButton(name);
                button.setPreferredSize(new Dimension(mWidth / mColumns, mHeight / mRows));
                mComponentMap.put(name, button);
                add(button, mConstraints);
            }
        }
    }

    public JButton getButton(String key) { return mComponentMap.get(key); }

    @Override
    public void jSceneUpdate(GameModel model) {

    }
}
