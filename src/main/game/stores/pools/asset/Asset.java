package main.game.stores.pools.asset;

import main.game.components.Animation;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class Asset {
//    public static final Asset DEFAULT = new Asset(null, null, null);
    private static ELogger logger = ELoggerFactory.getInstance().getELogger(Asset.class);
    private String mId;
    private String mPreviousId;
    private String mSheet;
    private String mSprite;
    private int mStartingFrame;
    private String mAnimationType;
    private final Animation mAnimation;

    public Asset(String id, String sheet, String sprite, int startingFrame, String animationType) {
        this(id, sheet, sprite, startingFrame, animationType, null);
    }

    public Asset(String id, String sheet, String sprite, int startingFrame, String animationType, Animation animation) {
        mId = id;
        mSheet = sheet;
        mSprite = sprite;
        mStartingFrame = startingFrame;
        mAnimationType = animationType;
        mAnimation = animation;
    }

    public Asset(String id, String effect, Animation animation) {
        mId = id;
        mAnimationType = effect;
        mAnimation = animation;
    }

    public String getId() { return mId; }
//    public String getId() {
//        String id = AssetPool.getInstance().getOrCreateID(
//                mId,
//                mSheet,
//                mSprite,
//                mStartingFrame,
//                mAnimationType
//        );
//
//        // Only used to test for bugs
//        if (mPreviousId != null && !mPreviousId.equalsIgnoreCase(id)) {
//            logger.error("Created new asset {} {}", mSheet, mSprite);
//        }
//        mPreviousId = id;
//
//        return AssetPool.getInstance().getOrCreateAsset(
//                id,
//                mSheet,
//                mSprite,
//                mStartingFrame,
//                mAnimationType
//        );
//    }

    public int getStartingFrame() { return mStartingFrame; }
    public String getAnimationType() { return mAnimationType; }
    public Animation getAnimation() { return mAnimation; }
}
