package main.game.stores.pools.asset;

import main.constants.Constants;
import main.constants.LRUCache;
import main.engine.Engine;
import main.graphics.Animation;
import main.game.main.GameModel;
import main.graphics.AssetNameSpace;
import main.graphics.SpriteSheetOG;
import main.graphics.Sprite;
import main.logging.ELogger;
import main.logging.ELoggerFactory;
import main.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.*;

public class AssetPool {

    public static final String WALL_TILES = "wall_tiles";
    public static final String FLOOR_TILES = "floor_tiles";
    public static final String LIQUIDS_TILES = "liquids";
    private final SplittableRandom random = new SplittableRandom();
    private final LRUCache<String, Asset> mAssetMap = new LRUCache<>(2000);
    public static final String FLICKER_ANIMATION = "flickering";
    public static final String SHEARING_ANIMATION = "shearing";
    public static final String TOP_SWAYING_ANIMATION = "swaying";
    public static final String SPINNING_ANIMATION = "spinning";
    public static final String STRETCH_Y_ANIMATION = "yStretch";
    public static final String STRETCH_ANIMATION = "stretch";
    public static final String STATIC_ANIMATION = "static";
    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(AssetPool.class);
    private static AssetNameSpace mAssetNameSpace;
    private AssetPool() {
        logger.info("Started initializing {}", getClass().getSimpleName());
        Engine.getHeapData();
        mAssetNameSpace = new AssetNameSpace(
                "./res/graphics",
                Constants.NATIVE_SPRITE_SIZE,
                Constants.NATIVE_SPRITE_SIZE
        );
        Engine.getHeapData();
        logger.info("Finished initializing {}", getClass().getSimpleName());
    }
    private static AssetPool instance = null;
    public static AssetPool getInstance() {
        if (instance == null) {
            instance = new AssetPool();
        }
        return instance;
    }

    public boolean contains(String assetId) {
        return getAsset(assetId) != null;
    }

    public String getOrCreateAsset(GameModel model, String name, String effect, int originFrame, String id) {
        int spriteWidth = model.getGameState().getSpriteWidth();
        int spriteHeight = model.getGameState().getSpriteHeight();
        return getOrCreateAsset(spriteWidth, spriteHeight, name, effect, originFrame, id);
    }

    public String getOrCreateAsset(int width, int height, String sprite, String effect, int originFrame) {
        String id = UUID.randomUUID().toString();
        return getOrCreateAsset(width, height, sprite, effect, originFrame, id);
    }

    public String getOrCreateAsset(int width, int height, String sprite, String effect, int frame, String id) {
        // if this id exists, animation for this already exists
        Asset existingAsset = mAssetMap.get(id);
        if (existingAsset != null) { return id; }
        // Get the spriteMap and sprite sheet to use
        Sprite spriteAsset = mAssetNameSpace.getAsset(sprite);
        // Get random sprite frame if negative
        int columns = spriteAsset.getColumns(0);
        int column = frame < 0 || frame > columns ? random.nextInt(columns) : frame;
        BufferedImage rawImage = spriteAsset.getSprite(0, column);
        // Correctly size the frame for the current width and height
        BufferedImage toProcess = ImageUtils.getResizedImage(rawImage, width, effect.equals(TOP_SWAYING_ANIMATION) ? (int) (height * 1.15) : height);
        // Create new animation for non-static assets. This was they will maintain their own frames.
        BufferedImage[] frames = null;
        // Each sprite sheet row is only a single sprite tall
        switch (effect) {
            case FLICKER_ANIMATION -> frames = ImageUtils.createFlickeringAnimation(toProcess, 15, .02f);
            case SHEARING_ANIMATION -> frames = ImageUtils.createShearingAnimation(toProcess, 24, .04);
            case TOP_SWAYING_ANIMATION -> frames = ImageUtils.createTopSwayingAnimation(toProcess, 24, .05);
            case SPINNING_ANIMATION -> frames = ImageUtils.spinify(toProcess, .05f);
            case STRETCH_Y_ANIMATION -> frames = ImageUtils.createAnimationViaYStretch(toProcess, 12, toProcess.getHeight() * .025);
            case STRETCH_ANIMATION -> frames = ImageUtils.createAnimationViaStretch(toProcess, 12, 1);
            case STATIC_ANIMATION -> frames = new BufferedImage[] {toProcess};
            default -> { frames = new BufferedImage[] {toProcess}; logger.error("Animation not supported"); }
        }
        // Create new asset with the provided criteria
        Asset newAsset = new Asset(id, sprite, effect, column, new Animation(frames));
        mAssetMap.put(id, newAsset);
        return id;
    }

    public String getBlueReticleId(GameModel model) {
        int width = model.getGameState().getSpriteWidth();
        int height = model.getGameState().getSpriteHeight();

        String id = getOrCreateAsset(
                model,
                "blue_reticle",
                STRETCH_ANIMATION,
                -1,
                "blue_reticle" + "_" + width + "_" + height + "_reticle"
        );
        return id;
    }

    public String getRedReticleId(GameModel model) {
        int width = model.getGameState().getSpriteWidth();
        int height = model.getGameState().getSpriteHeight();

        String id = getOrCreateAsset(
                model,
                "red_reticle",
                STRETCH_ANIMATION,
                -1,
                "red_reticle" + "_" + width + "_" + height + "_reticle"
        );
        return id;
    }

    public String getYellowReticleId(GameModel model) {
        int width = model.getGameState().getSpriteWidth();
        int height = model.getGameState().getSpriteHeight();

        String id = getOrCreateAsset(
                model,
                "yellow_reticle",
                STRETCH_ANIMATION,
                -1,
                "yellow_reticle" + "_" + width + "_" + height + "_reticle"
        );
        return id;
    }

    public String getSprite(String id) {
        Asset asset = mAssetMap.get(id);
        String result = null;
        if (asset != null) {
            result = asset.getSprite();
        }
        return result;
    }

    public Animation getAnimation(String id) {
        Asset asset = mAssetMap.get(id);
        Animation result = null;
        if (asset != null) {
            result = asset.getAnimation();
        }
        return result;
    }

    public BufferedImage getImage(String id) {
        Animation animation = getAnimation(id);
        BufferedImage image = null;
        if (animation != null) {
            image = animation.toImage();
        }
        return image;
    }

    public int getOriginFrame(String id) {
        Asset asset = AssetPool.getInstance().getAsset(id);
        int frame = -1;
        if (asset != null) {
            frame = asset.getOriginFrame();
        }
        return frame;
    }

    public Asset getAsset(String id) { return mAssetMap.get(id); }
    public Animation getAbilityAnimation(GameModel model, String sprite) {
        int spriteWidth = model.getGameState().getSpriteWidth();
        int spriteHeight = model.getGameState().getSpriteHeight();
        return getAbilityAnimation(model, sprite, spriteWidth, spriteHeight);
    }
    public Animation getAbilityAnimation(GameModel model, String sprite, int width, int height) {
        Sprite sheet = mAssetNameSpace.getAsset(sprite);
//        Sprite sheet = mSpriteSheetMap.get(Constants.ABILITIES_SPRITEMAP_FILEPATH).get(animationName);
        if (sheet == null) { return null; }
        BufferedImage[] toCopy = sheet.getSpriteArray(0);
        for (int i = 0; i < toCopy.length; i++) {
            BufferedImage copy = ImageUtils.getResizedImage(toCopy[i], width, height);
            toCopy[i] = ImageUtils.deepCopy(copy);
        }
        return new Animation(toCopy);
    }
    public SpriteSheetOG getSpriteMap(String name) {
        return null;
//        return mSpriteSheetMap.get(name);
    }
    public List<String> getBucket(String bucket) {
        return mAssetNameSpace.getAssetBucket(bucket).keySet().stream().toList();
    }

    public Map<String, String> getBucketV2(String bucket) {
        return mAssetNameSpace.getAssetBucket(bucket);
    }
    public Map<String, String> getLiquids() { return mAssetNameSpace.getAssetBucket("liquids"); }
    public Map<String, String> getFloors() { return mAssetNameSpace.getAssetBucket("floor_tiles"); }
    public Map<String, String> getMiscellaneous() { return mAssetNameSpace.getAssetBucket("misc"); }


    public String createPortrait(String subjectID, String backgroundID) {
        Asset subjectAsset = mAssetMap.get(subjectID);
        Asset backgroundAsset = mAssetMap.get(backgroundID);

        String assetId = subjectID + backgroundAsset;
        Asset existingAsset = mAssetMap.get(assetId);

        if (existingAsset != null) { System.out.println("EXISTING!"); return assetId; }

        BufferedImage[] frames = ImageUtils.mergeAnimationWithBackground(
                backgroundAsset.getAnimation().getFrame(0),
                subjectAsset.getAnimation().getContent(),
                0,
                .9
        );

        Asset newAsset = new Asset(assetId, "merged", "merged", 0, new Animation(frames));
        mAssetMap.put(assetId, newAsset);
        return assetId;
    }
}