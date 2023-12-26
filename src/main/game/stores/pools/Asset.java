package main.game.stores.pools;

import main.game.components.Animation;

import java.awt.image.BufferedImage;

public class Asset {
    public static final Asset DEFAULT = new Asset("", "", null);
    private final String mId;
    private final String mName;
    private final Animation mAnimation;
    public Asset(String name, String id, Animation animation) {
        mName = name;
        mId = id;
        mAnimation = animation;
    }

    public String getId() { return mId; }
    public String getName() { return mName; }
    public Animation getAnimation() { return mAnimation; }
}
