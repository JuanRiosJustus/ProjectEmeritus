package main.graphics;


import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import main.game.main.GameModel;
import main.game.stores.pools.FontPool;
import main.ui.components.OutlineButton;
import main.ui.custom.SwingUiUtils;

public abstract class JScene extends JPanel {

    protected final JButton mEnterButton;
    protected final JButton mExitButton;
    protected final int mWidth;
    protected final int mHeight;

    public JScene(int width, int height, String name) {
        this(width, height, 0, 0, name);
    }
    public JScene(int width, int height, int x, int y, String name) {
        mEnterButton = new OutlineButton("Enter", SwingConstants.CENTER);
//        mExitButton = new JButton("Exit");
        mExitButton = new OutlineButton("Exit", SwingConstants.CENTER);

        SwingUiUtils.automaticallyStyleButton(mEnterButton);
        SwingUiUtils.automaticallyStyleButton(mExitButton);
//        mExitButton.setFont(FontPool.getInstance().getFont(26));
//        mExitButton = new OutlineButton();
//        mExitButton.setText("Exit");
        setName(name.replaceAll("Panel", ""));
        mWidth = width;
        mHeight = height;
        setPreferredSize(new Dimension(width, height));
        setPreferredLocation(x, y);
        setDoubleBuffered(true);
    }

    public JButton getEnterButton() { return mEnterButton; }
    public JButton getExitButton() { return mExitButton; }

    public void setPreferredLocation(int x, int y) {
        setBounds(x, y, (int)getPreferredSize().getWidth(), (int)getPreferredSize().getHeight());
    }

    public void setName(String name) {
        super.setName(name);
        mEnterButton.setText(name);
        mEnterButton.setName(name);
        mExitButton.setName(name);
    }

    public abstract void jSceneUpdate(GameModel model);

    public int getJSceneWidth() { return (int)getPreferredSize().getWidth(); }
    public int getJSceneHeight() { return (int)getPreferredSize().getHeight(); }
}
