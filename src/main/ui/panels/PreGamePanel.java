package main.ui.panels;

import main.game.stores.pools.ColorPalette;
import main.game.main.GameModel;
import main.graphics.JScene;

import javax.swing.JPanel;
import java.awt.Color;

public class PreGamePanel extends JScene {
    public PreGamePanel(int width, int height) {
        super(width, height, PreGamePanel.class.getSimpleName());

        setLayout(null);

        setBackground(Color.RED);

//        add(spawnSelectPanel(0, 0, width / 4, height));
        add(unitsList(5, 5, width / 4, height));
    }

    private JPanel unitsList(int x, int y, int width, int height) {
        JPanel panel = new JPanel();
        panel.setBounds(x, y, width, height);
        panel.setBackground(ColorPalette.BLUE);
        panel.setOpaque(true);

//        JList<String> list = new JList<String>(new String[]{ "t2", "52k5i2", "52j55jo2", "5ko3f"});
//        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
//        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
//        list.setOpaque(false);
//        list.setBackground(ColorPalette.getRandomColor());
//        list.setBounds(0, 0, width, height);
//        list.setFont(FontPool.getInstance().getFont(16));


//        panel.setLayout(null);
//        panel.add(list);
//        panel.setOpaque(false);

        return panel;
    }

    private JPanel spawnSelectPanel(int x, int y, int width, int height) {
        JPanel panel = new JPanel();
        panel.setBounds(x, y, width, height);

        panel.setBackground(ColorPalette.GREEN);
        return panel;
    }

    @Override
    public void jSceneUpdate(GameModel model) {

    }
}
