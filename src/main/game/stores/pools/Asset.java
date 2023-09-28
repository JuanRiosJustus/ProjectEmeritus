package main.game.stores.pools;

import main.game.components.Animation;

import java.awt.image.BufferedImage;

public class Asset {
    private final int mId;
    private final String mName;
    private final Animation mAnimation;
    public Asset(String name, int id, Animation animation) {
        mName = name;
        mId = id;
        mAnimation = animation;
    }

    public int getId() { return mId; }
    public String getName() { return mName; }
    public Animation getAnimation() { return mAnimation; }
}
