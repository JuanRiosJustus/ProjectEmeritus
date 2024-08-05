package main.ui.custom;

import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.graphics.JScene;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ScrollableButtonArray extends JScene {
    private final Map<String, JButton> mButtonMap = new HashMap<>();
    private final GridBagConstraints mGridBagConstraints = new GridBagConstraints();
    private final int mMaxButtonsPerView = 5;

    private final JPanel mContainer = new JPanel();
    public ScrollableButtonArray(int width, int height) {
        super(width, height, "ScrollableButtonArray");

        mContainer.setOpaque(true);
        mContainer.setBackground(ColorPalette.TRANSPARENT);
        mContainer.setLayout(new GridBagLayout());

        mGridBagConstraints.fill = GridBagConstraints.BOTH;
        mGridBagConstraints.weightx = 1;
        mGridBagConstraints.weighty = 1;
        mGridBagConstraints.gridx = 0;
        mGridBagConstraints.gridy = 0;

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setOpaque(true);
        setBackground(ColorPalette.TRANSPARENT);

        JScrollPane pane = SwingUiUtils.createBonelessScrollingPane(width, height, mContainer);
        pane.setBackground(Color.RED);
        add(pane, mGridBagConstraints);
//        for (int i = 0; i < 8; i++) {
//            addButton("yyyyr " + i);
//        }
    }

    public JButton addButton(String button) {
        if (mButtonMap.containsKey(button)) {
            return mButtonMap.get(button);
        }

        JButton b3 = new JButton(button);
        b3.setBackground(Color.BLUE);

        int buttonHeight = mHeight / mMaxButtonsPerView;
        b3.setPreferredSize(new Dimension(b3.getWidth(), buttonHeight));
//        b3.setMaximumSize(new Dimension(mWidth, buttonHeight));
//        b3.setMinimumSize(new Dimension(mWidth, buttonHeight));

        mButtonMap.put(button, b3);
        mGridBagConstraints.fill = GridBagConstraints.BOTH;
        mGridBagConstraints.gridy = mButtonMap.size();

        mContainer.add(b3, mGridBagConstraints);

        return b3;
    }

    public boolean contains(String buttonName) {
        return mButtonMap.containsKey(buttonName);
    }

    public void clearGrid() {
        mButtonMap.clear();
        mContainer.removeAll();
    }

    @Override
    public void jSceneUpdate(GameModel model) {

    }
}
