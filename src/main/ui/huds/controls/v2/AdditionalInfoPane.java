package main.ui.huds.controls.v2;

import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.graphics.JScene;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

public class AdditionalInfoPane extends JScene {

    private final ButtonTabbedPane mButtonTabbedPane = null;
    public AdditionalInfoPane(int width, int height) {
        super(width, height, AdditionalInfoPane.class.getSimpleName());

        setLayout(new CardLayout());
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));

        setOpaque(true);
        setBackground(ColorPalette.getRandomColor());


//        JPanel testPanel = new JPanel();
//        testPanel.setPreferredSize(new Dimension(width, height));
//        testPanel.setMaximumSize(new Dimension(width, height));
//        testPanel.setMinimumSize(new Dimension(width, height));
//
//
//        setBackground(ColorPalette.TRANSPARENT);
//        setOpaque(false);
//
////        mButtonTabbedPane = new ButtonTabbedPane(width, height);
//        add(testPanel);

//        add(mButtonTabbedPane);
    }

    public void addAdditionalInfoPanel(String componentName, JComponent component) {
//        mButtonTabbedPane.addPanel(componentName, component);
    }

    @Override
    public void jSceneUpdate(GameModel model) {
//        mButtonTabbedPane.jSceneUpdate(model);
    }

    public void addAdditionalInfoPanel(String key, JPanel panel) {
        panel.setPreferredSize(new Dimension(getDisplayHeight(), getDisplayWidth()));
        panel.setMinimumSize(new Dimension(getDisplayHeight(), getDisplayWidth()));
        panel.setMaximumSize(new Dimension(getDisplayHeight(), getDisplayWidth()));
        add(key, panel);
    }

    public void show(String key) {
        CardLayout cardLayout = (CardLayout) getLayout();
        cardLayout.show(this, key);
    }
    public int getDisplayWidth() { return getJSceneWidth(); }
    public int getDisplayHeight() { return getJSceneHeight(); }
}
