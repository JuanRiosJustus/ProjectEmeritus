package main.ui.panels;

import main.constants.ColorPalette;
import main.engine.EngineScene;
import main.game.main.GameModel;
import main.graphics.JScene;
import main.utils.ComponentUtils;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.TextArea;

public class NewGameStartup extends EngineScene {

    private JTextArea textArea;
    public NewGameStartup(int width, int height) {
        super(width, height, NewGameStartup.class.getSimpleName());
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
