package main.game.stores.pools.asset;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import main.constants.Constants;
import main.constants.LRUCache;
import main.graphics.*;
import main.game.main.GameModel;
import main.logging.EmeritusLogger;

import main.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.*;

public class AssetPool {
    private static AssetPool instance = null;
    public static AssetPool getInstance() {
        if (instance == null) {
            instance = new AssetPool();
        }
        return instance;
    }
    public static final String WALL_TILES = "wall_tiles";
    public static final String FLOOR_TILES = "floor_tiles";
    public static final String LIQUIDS_TILES = "liquids";
    private final SplittableRandom random = new SplittableRandom();
    private final LRUCache<String, Animation> mAssetMap = new LRUCache<>(4000);
    public static final String FLICKER_ANIMATION = "flickering";
    public static final String SHEARING_ANIMATION = "shearing";
    public static final String TOP_SWAYING_ANIMATION = "swaying";
    public static final String SPINNING_ANIMATION = "spinning";
    public static final String STRETCH_Y_ANIMATION = "yStretch";
    public static final String STRETCH_ANIMATION = "stretch";
    public static final String STATIC_ANIMATION = "static";
    private static final EmeritusLogger logger = EmeritusLogger.create(AssetPool.class);
    private static AssetNameSpace mAssetNameSpace;
    private AssetPool() {
        logger.info("Started initializing {}", getClass().getSimpleName());
        mAssetNameSpace = new AssetNameSpace(
                "./res/graphics",
                Constants.NATIVE_SPRITE_SIZE,
                Constants.NATIVE_SPRITE_SIZE
        );
        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public String getOrCreateDirectionalShadows(int width, int height, int frame, String id) {
        return getOrCreateStaticAsset(width, height, "directional_shadows", frame, id);
    }

    public String getOrCreateDepthShadows(int width, int height, int frame, String id) {
        return getOrCreateStaticAsset(width, height, "depth_shadows", frame, id);
    }

    public String getOrCreateStaticAsset(int width, int height, String asset, int frame, String id) {
        return getOrCreateAsset(width, height, asset, STATIC_ANIMATION, frame, id);
    }
    public String getOrCreateFlickerAsset(int width, int height, String asset, int frame, String id) {
        return getOrCreateAsset(width, height, asset, FLICKER_ANIMATION, frame, id);
    }
    public String getOrCreateTopSwayingAsset(int width, int height, String asset, int frame, String id) {
        return getOrCreateAsset(width, height, asset, TOP_SWAYING_ANIMATION, frame, id);
    }
    public String getOrCreateVerticalStretchAsset(int width, int height, String asset, int frame, String id) {
        return getOrCreateAsset(width, height, asset, STRETCH_Y_ANIMATION, frame, id);
    }
    public String getOrCreateVerticalAndHorizontalStretchAsset(int width, int height, String asset, int frame, String id) {
        return getOrCreateAsset(width, height, asset, STRETCH_ANIMATION, frame, id);
    }

    public String getOrCreateAsset(int width, int height, String sprite, String effect, int originFrame) {
        String id = UUID.randomUUID().toString();
        return getOrCreateAsset(width, height, sprite, effect, originFrame, id);
    }

    public String getOrCreateAsset(int width, int height, String sprite, String effect, int frame, String id) {
        // if this id exists, animation for this already exists
        Animation existingAsset = mAssetMap.get(id);
        if (existingAsset != null) { return id; }
        // Get the spriteMap and sprite sheet to use
        Sprite spriteAsset = mAssetNameSpace.getAsset(sprite);
        // Get random sprite frame if negative
        int columns = spriteAsset.getColumns(0);
        int column = frame < 0 || frame > columns ? random.nextInt(columns) : frame;
        BufferedImage rawImage = SwingFXUtils.fromFXImage(spriteAsset.getSprite(0, column), null);
        // Correctly size the frame for the current width and height
        BufferedImage toProcess = null;
        if (effect.equalsIgnoreCase(TOP_SWAYING_ANIMATION)) {
            toProcess = ImageUtils.getResizedImage(rawImage, width, (int) (height * 1.15));
        } else {
            toProcess = ImageUtils.getResizedImage(rawImage, width, height);
        }
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
        createAsset(id, sprite, effect, column, frames);
        return id;
    }

    public String getBlueReticleId(GameModel model) {
        int width = model.getGameState().getSpriteWidth();
        int height = model.getGameState().getSpriteHeight();

        String id = getOrCreateVerticalAndHorizontalStretchAsset(
                model.getGameState().getSpriteWidth(),
                model.getGameState().getSpriteHeight(),
                "blue_reticle",
                -1,
                "blue_reticle" + "_" + width + "_" + height + "_reticle"
        );
        return id;
    }

    public String getRedReticleId(GameModel model) {
        int width = model.getGameState().getSpriteWidth();
        int height = model.getGameState().getSpriteHeight();
//        getOrCreateVerticalAndHorizontalStretchAsset
        String id = getOrCreateVerticalAndHorizontalStretchAsset(
                width,
                height,
                "red_reticle",
                -1,
                "red_reticle" + "_" + width + "_" + height + "_reticle"
        );
        return id;
    }

    public String getYellowReticleId(GameModel model) {
        int width = model.getGameState().getSpriteWidth();
        int height = model.getGameState().getSpriteHeight();

        String id = getOrCreateVerticalAndHorizontalStretchAsset(
                width,
                height,
                "yellow_reticle",
                -1,
                "yellow_reticle" + "_" + width + "_" + height + "_reticle"
        );

        return id;
    }

//    public String getSprite(String id) {
//        Asset asset = mAssetMap.get(id);
//        String result = null;
//        if (asset != null) {
//            result = asset.getAsset();
//        }
//        return result;
//    }

//    public Animation getAnimation(String id) {
//        Asset asset = mAssetMap.get(id);
//        Animation result = null;
//        if (asset != null) {
//            result = asset.getAnimation();
//        }
//        return result;
//    }


    public Image getImage(String id) {
//        Asset asset = mAssetMap.get(id);
//        Image result = null;
//        if (asset != null) {
//            Animation animation = asset.getAnimation();
//            result = animation.toImage();
//        }
//        return result;

        Animation animation = mAssetMap.get(id);
        Image result = null;
        if (animation != null) {
            result = animation.toImage();
        }
        return result;
    }

    public void update(String id) {
//        Asset asset = mAssetMap.get(id);
//        if (asset == null) {
//            return;
//        }
//        Animation animation = asset.getAnimation();
//        animation.update();

        Animation animation = mAssetMap.get(id);
        if (animation == null) {
            return;
        }
        animation.update();
    }

//    public Animation getAbilityAnimation(GameModel model, String sprite) {
//        int spriteWidth = model.getGameState().getSpriteWidth();
//        int spriteHeight = model.getGameState().getSpriteHeight();
//        return getAbilityAnimation(model, sprite, spriteWidth, spriteHeight);
//    }
//    public Animation getAbilityAnimation(GameModel model, String sprite, int width, int height) {
//        Sprite sheet = mAssetNameSpace.getAsset(sprite);
////        Sprite sheet = mSpriteSheetMap.get(Constants.ABILITIES_SPRITEMAP_FILEPATH).get(animationName);
//        if (sheet == null) { return null; }
//        BufferedImage[] toCopy = sheet.getSpriteArray(0);
//        for (int i = 0; i < toCopy.length; i++) {
//            BufferedImage copy = ImageUtils.getResizedImage(toCopy[i], width, height);
//            toCopy[i] = ImageUtils.deepCopy(copy);
//        }
//        return new Animation(toCopy);
//    }

    public List<String> getBucket(String bucket) {
        return mAssetNameSpace.getAssetBucket(bucket).keySet().stream().toList();
    }

    public Map<String, String> getBucketV2(String bucket) {
        return mAssetNameSpace.getAssetBucket(bucket);
    }
    public Map<String, String> getLiquids() { return mAssetNameSpace.getAssetBucket("liquids"); }
    public Map<String, String> getFloors() { return mAssetNameSpace.getAssetBucket("floor_tiles"); }
    public Map<String, String> getMiscellaneous() { return mAssetNameSpace.getAssetBucket("misc"); }


    public String getOrCreateMergedAssets(int width, int height, List<String> assetIds, String id) {
        // If the given id already exists, return early
//        Asset currentAsset = mAssetMap.get(id);
        Animation currentAsset = mAssetMap.get(id);
        if (currentAsset != null) {
            return id;
        }

        // if the list of ids to merge is empty, return null / No id
        if (!assetIds.isEmpty()) {
            BufferedImage[] toMerge = new BufferedImage[assetIds.size()];
            for (int i = 0; i < assetIds.size(); i++) {
                String iteratedID = assetIds.get(i);
                Animation iteratedAsset = mAssetMap.get(iteratedID);
                if (iteratedAsset == null) { continue; }
                BufferedImage iteratedImage = SwingFXUtils.fromFXImage(iteratedAsset.toImage(), null);
                toMerge[i] = iteratedImage;
            }
            BufferedImage mergedImage = ImageUtils.mergeImages(width, height, toMerge);
            createAsset(id, "custom_merged_asset", "merged", 0, mergedImage);
        } else {
            id = null;
        }

        return id;
    }

    private void createAsset(String id, String asset, String effect, int frame, BufferedImage image) {
        createAsset(id, asset, effect, frame, new BufferedImage[] { image });
    }

    private void createAsset(String id, String asset, String effect, int frame, BufferedImage[] images) {
        mAssetMap.put(id, new Animation(images));
    }
}