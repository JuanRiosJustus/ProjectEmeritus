package main.ui.huds.controls.v2;

import main.game.main.GameModel;
import main.game.stores.pools.ColorPalette;
import main.graphics.JScene;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

public class AdditionalInfoPane extends JScene {
    private Map<String, JComponent> mComponentMap = new HashMap<>();
    public AdditionalInfoPane(int width, int height) {
        super(width, height, AdditionalInfoPane.class.getSimpleName());

        setLayout(new CardLayout());
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));

        setOpaque(true);
        setBackground(ColorPalette.TRANSPARENT);


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

    @Override
    public void jSceneUpdate(GameModel model) {
//        mButtonTabbedPane.jSceneUpdate(model);
    }

    private JComponent component;

    public void show(String key) {
        CardLayout cardLayout = (CardLayout) getLayout();
        cardLayout.show(this, key);
    }
    public int getDisplayWidth() { return getJSceneWidth(); }
    public int getDisplayHeight() { return getJSceneHeight(); }
}
