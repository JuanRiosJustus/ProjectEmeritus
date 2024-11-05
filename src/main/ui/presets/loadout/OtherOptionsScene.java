package main.ui.presets.loadout;

import main.constants.Constants;
import main.engine.EngineScene;
import main.game.map.base.TileMap;
import main.game.stores.pools.ColorPalette;
import main.ui.custom.SwingUiUtils;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class OtherOptionsScene extends EngineScene {

    private Map<String, JButton> mButtonMap = new HashMap<>();

    public void setup(String[] buttonNames, int width, int height, Object mapScene) {

//        setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
        setPreferredSize(new Dimension(width, height));

        setBackground(ColorPalette.TRANSPARENT);
        int spriteSizes = Constants.CURRENT_SPRITE_SIZE;
        removeAll();

        JButton[] buttons = new JButton[buttonNames.length];
        for (int i = 0; i < buttons.length; i++) {
            JButton button = new JButton();
            buttons[i] = button;
        }

        JPanel container = new JPanel();
        container.removeAll();
        container.setLayout(new GridBagLayout());
        container.setBackground(ColorPalette.TRANSPARENT);
        container.setBorder(new EmptyBorder(0, 0, 0, 0));

        int rows = 3;
        int columns = (buttons.length * 2) + 1;
        int index = 0;
        int buttonWidth = width / columns;
        int buttonHeight = height / rows;


        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                GridBagConstraints gbc = SwingUiUtils.createGbc(row, column);

                boolean isEmpty = false;
                if (row == 0 || row == rows - 1) { isEmpty = true; }
                if (column % 2 == 0) { isEmpty = true; }

                boolean showedAllButtons = index >= buttons.length;

                if (isEmpty || showedAllButtons) {
                    JPanel panel = new JPanel();
                    panel.setOpaque(false);
                    panel.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
                    container.add(panel, gbc);
                } else {
                    JButton button = buttons[index];
                    button.setOpaque(true);
                    button.setBackground(ColorPalette.TRANSPARENT);
                    button.setForeground(Color.BLACK);
                    button.setText(buttonNames[index++]);
                    mButtonMap.put(button.getText(), button);
                    button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
                    container.add(button, gbc);
                }
            }
        }


        add(container);
    }

//    public void setup(String[] buttonNames, Rectangle bounds, MapScene mapScene) {
//
//        setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
//
//        setBackground(ColorPalette.TRANSPARENT);
//        int spriteSizes = Constants.CURRENT_SPRITE_SIZE;
//        removeAll();
//
//        JButton[] buttons = new JButton[buttonNames.length];
//        for (int i = 0; i < buttons.length; i++) {
//            JButton button = new JButton();
//            buttons[i] = button;
//        }
//
//        JPanel container = new JPanel();
//        container.removeAll();
//        container.setLayout(new GridBagLayout());
//        container.setBackground(ColorPalette.TRANSPARENT);
//        container.setBorder(new EmptyBorder(0, 0, 0, 0));
//
//        int rows = 3;
//        int columns = (buttons.length * 2) + 1;
//        int index = 0;
//        int buttonWidth = bounds.width / columns;
//        int buttonHeight = bounds.height / rows;
//
//
//        for (int row = 0; row < rows; row++) {
//            for (int column = 0; column < columns; column++) {
//                GridBagConstraints gbc = SwingUiUtils.createGbc(row, column);
//
//                boolean isEmpty = false;
//                if (row == 0 || row == rows - 1) { isEmpty = true; }
//                if (column % 2 == 0) { isEmpty = true; }
//
//                boolean showedAllButtons = index >= buttons.length;
//
//                if (isEmpty || showedAllButtons) {
//                    JPanel panel = new JPanel();
//                    panel.setOpaque(false);
//                    panel.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
//                    container.add(panel, gbc);
//                } else {
//                    JButton button = buttons[index];
//                    button.setOpaque(true);
//                    button.setBackground(ColorPalette.TRANSPARENT);
//                    button.setForeground(Color.BLACK);
//                    button.setText(buttonNames[index++]);
//                    mButtonMap.put(button.getText(), button);
//                    button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
//                    container.add(button, gbc);
//                }
//            }
//        }
//
//
//        add(container);
//    }

    public JButton getButton(String button) {
        return mButtonMap.get(button);
    }

    @Override
    public void update() {

    }

    @Override
    public void input() {

    }

    @Override
    public JPanel render() {
        return null;
    }
}
