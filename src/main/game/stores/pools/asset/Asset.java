package main.game.stores.pools.asset;

import main.graphics.Animation;

public class Asset {
//    public static final Asset DEFAULT = new Asset(null, null, null);
    private final String mId;
    private final String mAsset;
    private final int mOriginFrame;
    private final String mEffect;
    private final Animation mAnimation;

    public Asset(String id, String asset, String effect, int frame, Animation animation) {
        mId = id;
        mAsset = asset;
        mEffect = effect;
        mOriginFrame = frame;
        mAnimation = animation;
    }

    public int getFrame() { return mOriginFrame; }
    public String getEffect() { return mEffect; }
    public Animation getAnimation() { return mAnimation; }
    public String getAsset() { return mAsset; }
}
