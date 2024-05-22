package main.game.components;

import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;

public class AssetWrapper extends Component {
    private final String mSpriteSheet;
    private final String mSprite;
    private final int mStartingFrame;
    private final String mAnimation;

    public AssetWrapper(String spriteSheet, String sprite, int startingFrame, String animation) {
        mSpriteSheet = spriteSheet;
        mSprite = sprite;
        mStartingFrame = startingFrame;
        mAnimation = animation;
    }

    public String getAnimationId() {
        return AssetPool.getInstance().getOrCreateAsset(
                AssetPool.getInstance().createID(mSpriteSheet, mSprite, mAnimation),
                mSpriteSheet,
                mSprite,
                mStartingFrame,
                mAnimation
        );
    }
}
