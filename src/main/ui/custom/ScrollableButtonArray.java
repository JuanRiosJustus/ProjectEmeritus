package main.ui.custom;

import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.graphics.JScene;
import main.ui.components.OutlineButton;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ScrollableButtonArray extends JScene {
    private final Map<String, OutlineButton> mButtonMap = new HashMap<>();
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
    }

    public JButton addOutlineButton(String button) {
        int buttonHeight = mHeight / mMaxButtonsPerView;
        int buttonWidth = mWidth;

        OutlineButton outlineButton = mButtonMap.get(button);

        if (outlineButton != null) {
            outlineButton.setText(button);
            return outlineButton;
        }

        outlineButton = new OutlineButton(button);
        outlineButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        outlineButton.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
        outlineButton.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
        SwingUiUtils.automaticallyStyleComponent(outlineButton, (int) (buttonHeight * .75));

        mButtonMap.put(button, outlineButton);
        mGridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        mGridBagConstraints.fill = GridBagConstraints.VERTICAL;
        mGridBagConstraints.gridy = mButtonMap.size();

        mContainer.add(outlineButton, mGridBagConstraints);

//        SwingUiUtils.automaticallyStyleComponent(outlineButton);

        return outlineButton;
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
