package main.game.stores.pools.asset;

import main.graphics.Animation;

public class Asset {
//    public static final Asset DEFAULT = new Asset(null, null, null);
    private final String mId;
    private final String mSprite;
    private final int mOriginFrame;
    private final String mAnimationType;
    private final Animation mAnimation;

    public Asset(String id, String sprite, String animationType, int originFrame, Animation animation) {
        mId = id;
        mSprite = sprite;
        mAnimationType = animationType;
        mOriginFrame = originFrame;
        mAnimation = animation;
    }


//    public String getId() { return mId; }
//    public String getSprite() { return mSprite; }
    public int getOriginFrame() { return mOriginFrame; }
    public String getAnimationType() { return mAnimationType; }
    public Animation getAnimation() { return mAnimation; }
    public String getSprite() { return mSprite; }
}
