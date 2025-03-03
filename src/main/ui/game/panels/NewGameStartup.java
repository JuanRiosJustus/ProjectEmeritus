package main.ui.game.panels;

import main.game.stores.pools.ColorPalette;
import main.engine.EngineScene;
import main.game.main.GameModel;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Dimension;

public class NewGameStartup extends EngineScene {

    private JTextArea textArea;
    public NewGameStartup(int width, int height) {
        super(width, height);
        setBackground(ColorPalette.RED);
        setLayout(null);

        textArea = createTextArea(0, 0, width, (int) (height * .8));
        add(textArea);
    }

    private static JTextArea createTextArea(int x, int y, int width, int height) {
        JTextArea area = new JTextArea();
        area.setBounds(x, y, width, height);
        area.setPreferredSize(new Dimension(width, height));
        area.setBackground(ColorPalette.RED);

        return area;
    }

    public void jSceneUpdate(GameModel model) {

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
