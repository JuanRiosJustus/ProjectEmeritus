package main.game.stores.pools.asset;

import main.graphics.Animation;

import java.awt.image.BufferedImage;

public class Asset {
    //    public static final Asset DEFAULT = new Asset(null, null, null);
    private final String mId;
    private final String mAsset;
    private final int mOriginFrame;
    private final String mEffect;
    private final Animation mAnimation;

    public Asset(String id, String asset, String effect, int frame, BufferedImage image) {
        this(id, asset, effect, frame, new BufferedImage[]{ image });
    }

    public Asset(String id, String asset, String effect, int frame, BufferedImage[] images) {
        mId = id;
        mAsset = asset;
        mEffect = effect;
        mOriginFrame = frame;
        mAnimation = new Animation(images);
    }

    public int getFrame() { return mOriginFrame; }
    public String getEffect() { return mEffect; }
    public Animation getAnimation() { return mAnimation; }
    public String getAsset() { return mAsset; }
}