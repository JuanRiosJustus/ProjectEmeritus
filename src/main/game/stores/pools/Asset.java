package main.game.stores.pools;

import main.game.components.Animation;

import java.awt.image.BufferedImage;

public class Asset {
    public static final Asset DEFAULT = new Asset("", "", null, -1);
    private final String mId;
    private final String mName;
    private final Animation mAnimation;
    private final int mIndex;
    public Asset(String name, String id, Animation animation, int index) {
        mName = name;
        mId = id;
        mAnimation = animation;
        mIndex = index;
    }

    public String getId() { return mId; }
    public String getName() { return mName; }
    public Animation getAnimation() { return mAnimation; }
    public int getIndex() { return mIndex; }
}
