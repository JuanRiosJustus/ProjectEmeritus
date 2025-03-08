package main.game.stores.pools.asset;

import javafx.scene.image.Image;
import main.graphics.Animation;
import main.graphics.AnimationV2;

import java.awt.image.BufferedImage;

public class AssetV2 {
    //    public static final Asset DEFAULT = new Asset(null, null, null);
    private final String mId;
    private final String mAsset;
    private final int mOriginFrame;
    private final String mEffect;
    private final AnimationV2 mAnimation;

    public AssetV2(String id, String asset, String effect, int frame, BufferedImage image) {
        this(id, asset, effect, frame, new BufferedImage[]{ image });
    }

    public AssetV2(String id, String asset, String effect, int frame, BufferedImage[] images) {
        mId = id;
        mAsset = asset;
        mEffect = effect;
        mOriginFrame = frame;
        mAnimation = new AnimationV2(images);
    }

    public int getFrame() { return mOriginFrame; }
    public String getEffect() { return mEffect; }
    public AnimationV2 getAnimation() { return mAnimation; }
    public String getAsset() { return mAsset; }
}