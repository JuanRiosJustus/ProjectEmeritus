package main.graphics;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import main.constants.Constants;
import main.constants.LRUCache;
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
    private final LRUCache<String, Animation> mAssetMap = new LRUCache<>(40000);
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

    public int getNativeSpriteSize() { return Constants.NATIVE_SPRITE_SIZE; }

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

    public String getOrCreateAsset(int width, int height, String sheet, String effect, int frame, String id) {
        // if this id exists, animation for this already exists
        Animation existingAnimation = mAssetMap.get(id);
        if (existingAnimation != null) { return id; }
        // Get the spriteMap and sprite sheet to use
        SpriteSheet spriteSheet = mAssetNameSpace.getAsset(sheet);
        spriteSheet.load();
        // Get random sprite frame if negative
        int columns = spriteSheet.getColumns(0);
        int column = frame < 0 || frame > columns ? random.nextInt(columns) : frame;
        BufferedImage rawImage = SwingFXUtils.fromFXImage(spriteSheet.getSprite(0, column), null);
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
        createAsset(id, sheet, effect, column, frames);
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


    public Image getImage(String id) {
        Animation animation = mAssetMap.get(id);
        Image result = null;
        if (animation != null) {
            result = animation.toImage();
        }
        return result;
    }

    public void update(String id) {
        Animation animation = mAssetMap.get(id);
        if (animation == null) {
            return;
        }
        animation.update();
    }

    public void clearPool() { mAssetMap.clear(); }

    public List<String> getBucket(String bucket) {
        return mAssetNameSpace.getAssetBucket(bucket);
    }
    public List<String> getLiquidTileSets() { return mAssetNameSpace.getAssetBucket("liquids"); }
    public List<String> getFloorTileSets() { return mAssetNameSpace.getAssetBucket("floor_tiles"); }
    public List<String> getWallTileSets() { return mAssetNameSpace.getAssetBucket("wall_tiles"); }
    public List<String> getStructureTileSets() { return mAssetNameSpace.getAssetBucket("structures"); }
//    public Map<String, String> getMiscellaneous() { return mAssetNameSpace.getAssetBucket("misc"); }


    public String getOrCreateMergedAssets(int width, int height, List<String> assetIds, String id) {
        // If the given id already exists, return early
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

    public int getFrame(String id) {
        Animation animation = mAssetMap.getOrDefault(id, null);
        if (animation == null) {
            return -1;
        }

        return animation.getCurrentFrame();
    }
    private void createAsset(String id, String asset, String effect, int frame, BufferedImage image) {
        createAsset(id, asset, effect, frame, new BufferedImage[] { image });
    }

    private void createAsset(String id, String asset, String effect, int frame, BufferedImage[] images) {
        mAssetMap.put(id, new Animation(images));
    }
}