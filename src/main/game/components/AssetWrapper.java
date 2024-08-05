package main.game.components;

import main.game.stores.pools.asset.AssetPool;

public class AssetWrapper {
    private final String mSpriteSheet;
    private final String mSprite;
    private final int mStartingFrame;
    private final String mAnimation;

    public AssetWrapper(String spriteSheet, String sprite, int startingFrame, String animationType) {
        this(null, spriteSheet, sprite, startingFrame, animationType);
    }

    public AssetWrapper(String id, String spriteSheet, String sprite, int startingFrame, String animationType) {
        mSpriteSheet = spriteSheet;
        mSprite = sprite;
        mStartingFrame = startingFrame;
        mAnimation = animationType;
    }

    public String getId() {
        return AssetPool.getInstance().getOrCreateAsset(
                AssetPool.getInstance().getOrCreateID(mSpriteSheet, mSprite, mAnimation),
                mSpriteSheet,
                mSprite,
                mStartingFrame,
                mAnimation
        );
    }
}
