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

public class AnimationPool {
    private static AnimationPool instance = null;
    public static AnimationPool getInstance() {
        if (instance == null) {
            instance = new AnimationPool();
        }
        return instance;
    }
    public static final String WALL_TILES = "wall_tiles";
    public static final String FLOOR_TILES = "floor_tiles";
    public static final String LIQUIDS_TILES = "liquids";
    private final SplittableRandom random = new SplittableRandom();
    private final LRUCache<String, Animation> mAnimationMap = new LRUCache<>(40000);
    public static final String FLICKER_ANIMATION = "flickering";
    public static final String SHEARING_ANIMATION = "shearing";
    public static final String TOP_SWAYING_ANIMATION = "swaying";
    public static final String SPINNING_ANIMATION = "spinning";
    public static final String STRETCH_Y_ANIMATION = "yStretch";
    public static final String STRETCH_ANIMATION = "stretch";
    public static final String STATIC_ANIMATION = "static";
    private static final EmeritusLogger logger = EmeritusLogger.create(AnimationPool.class);
    private static SpritesheetNamespace mSpritesheetNamespace;
    private AnimationPool() {
        logger.info("Started initializing {}", getClass().getSimpleName());
        mSpritesheetNamespace = new SpritesheetNamespace(
                "./res/graphics",
                Constants.NATIVE_SPRITE_SIZE,
                Constants.NATIVE_SPRITE_SIZE
        );
        logger.info("Finished initializing {}", getClass().getSimpleName());
    }

    public int getNativeSpriteSize() { return Constants.NATIVE_SPRITE_SIZE; }

//    public String getOrCreateDirectionalShadows(int width, int height, int frame, String id) {
//        return getOrCreateStaticAnimation("directional_shadows", width, height, frame, id);
//    }
    public String getOrCreateDepthShadows(int width, int height, int frame, String id) {
        return getOrCreateStatic("depth_shadows", width, height,  id);
    }


    public String getOrCreateStatic(String sheet, int width, int height, String id) {
        return getOrCreateAsset(sheet, width, height, STATIC_ANIMATION, id);
    }
    public String getOrCreateFlicker(String sheet, int width, int height, String id) {
        return getOrCreateAsset(sheet, width, height, FLICKER_ANIMATION, id);
    }
    public String getOrCreateTopSwaying(String sheet, int width, int height, String id) {
        return getOrCreateAsset(sheet, width, height, TOP_SWAYING_ANIMATION, id);
    }
    public String getOrCreateVerticalStretch(String sheet, int width, int height, String id) {
        return getOrCreateAsset(sheet, width, height, STRETCH_Y_ANIMATION, id);
    }
    public String getOrCreateCrop(String sheet, int width, int height, String id) {
        return getOrCreateAsset(sheet, width, height, STRETCH_ANIMATION, id);
    }

//    public String getOrCreateStaticAnimation(String sheet, int width, int height, int frame, String id) {
//        return getOrCreateAsset(sheet, width, height, STATIC_ANIMATION, frame, id);
//    }
//    public String getOrCreateFlickerAnimation(String sheet, int width, int height, int frame, String id) {
//        return getOrCreateAsset(sheet, width, height, FLICKER_ANIMATION, frame, id);
//    }
//    public String getOrCreateTopSwayingAnimation(String sheet, int width, int height, int frame, String id) {
//        return getOrCreateAsset(sheet, width, height, TOP_SWAYING_ANIMATION, frame, id);
//    }
//    public String getOrCreateVerticalStretchAnimation(String sheet, int width, int height, int frame, String id) {
//        return getOrCreateAsset(sheet, width, height, STRETCH_Y_ANIMATION, frame, id);
//    }
//    public String getOrCreateCropAnimation(String sheet, int width, int height, int frame, String id) {
//        return getOrCreateAsset(sheet, width, height, STRETCH_ANIMATION, frame, id);
//    }

    private String getOrCreateAsset(String sheet, int width, int height, String effect, String id) {
        // if this id exists, animation for this already exists
        Animation existingAnimation = mAnimationMap.get(id);
        if (existingAnimation != null) { return id; }
        // Get the spriteMap and sprite sheet to use
        Spritesheet spriteSheet = mSpritesheetNamespace.getAsset(sheet);
        spriteSheet.load();

        // Get random sprite frame if negative
//        int frameCount = spriteSheet.getFrameCount();
//        int originFrame = opacity < 0 || opacity > frameCount ? random.nextInt(frameCount) : opacity;
        BufferedImage rawImage = SwingFXUtils.fromFXImage(spriteSheet.getFrame(0), null);
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
            case FLICKER_ANIMATION -> frames = ImageUtils.createFlickeringAnimation(toProcess, 24, .02f);
            case SHEARING_ANIMATION -> frames = ImageUtils.createShearingAnimation(toProcess, 24, .04);
            case TOP_SWAYING_ANIMATION -> frames = ImageUtils.createTopSwayingAnimation(toProcess, 24, .05);
            case SPINNING_ANIMATION -> frames = ImageUtils.spinify(toProcess, .05f);
            case STRETCH_Y_ANIMATION -> frames = ImageUtils.createAnimationViaYStretch(toProcess, 24, toProcess.getHeight() * .025);
            case STRETCH_ANIMATION -> frames = ImageUtils.createAnimationViaStretch(toProcess, 24, 1);
            case STATIC_ANIMATION -> frames = new BufferedImage[] { toProcess };
            default -> { frames = new BufferedImage[] {toProcess}; logger.error("Animation not supported"); }
        }


        createAsset(sheet, width, height, effect, frames, id);

        return id;
    }


    public void setOpacity(String id, float opacity) {
        Animation animation = mAnimationMap.get(id);
        if (animation == null) {
            return;
        }
        animation.setOpacity(opacity);
    }

    public void setSpeed(String id, float speed) {
        Animation animation = mAnimationMap.get(id);
        if (animation == null) {
            return;
        }
        animation.setSpeed(speed);
    }

    public String getBlueReticleId(GameModel model) {
        int width = model.getGameState().getSpriteWidth();
        int height = model.getGameState().getSpriteHeight();

        String id = getOrCreateCrop(
                "blue_reticle",
                model.getGameState().getSpriteWidth(),
                model.getGameState().getSpriteHeight(),
                "blue_reticle" + "_" + width + "_" + height + "_reticle"
        );

        AnimationPool.getInstance().setSpeed(id, 0.5f);

        return id;
    }

    public String getRedReticleId(GameModel model) {
        int width = model.getGameState().getSpriteWidth();
        int height = model.getGameState().getSpriteHeight();
        String id = getOrCreateCrop(
                "red_reticle",
                width,
                height,
                "red_reticle" + "_" + width + "_" + height + "_reticle"
        );

        AnimationPool.getInstance().setSpeed(id, 0.5f);
        return id;
    }

    public String getYellowReticleId(GameModel model) {
        int width = model.getGameState().getSpriteWidth();
        int height = model.getGameState().getSpriteHeight();

        String id = getOrCreateCrop(
                "yellow_reticle",
                width,
                height,
                "yellow_reticle" + "_" + width + "_" + height + "_reticle"
        );


        AnimationPool.getInstance().setSpeed(id, 0.5f);
        return id;
    }


    public Animation getAnimation(String id) {
        Animation animation = mAnimationMap.get(id);
        return animation;
    }
    public Image getImage(String id) {
        Animation animation = mAnimationMap.get(id);
        Image result = null;
        if (animation != null) {
            result = animation.toImage();
        }
        return result;
    }

    public void update(String id, double deltaTime) {
        Animation animation = mAnimationMap.get(id);
        if (animation == null) {
            return;
        }

        animation.update(deltaTime);
//        System.out.println("aid " + id + " acf " + animation.getCurrentFrame() + " ap " + animation.getProgress());
//        System.out.println("aid " + id + " acf " + animation.getCurrentFrame() + " ap " + animation.getProgress());
    }

    public void clearPool() { mAnimationMap.clear(); }

    public List<String> getBucket(String bucket) {
        return mSpritesheetNamespace.getAssetBucket(bucket);
    }
    public List<String> getLiquidTileSets() { return mSpritesheetNamespace.getAssetBucket("liquids"); }
    public List<String> getFloorTileSets() { return mSpritesheetNamespace.getAssetBucket("floor_tiles"); }
    public List<String> getWallTileSets() { return mSpritesheetNamespace.getAssetBucket("wall_tiles"); }
    public List<String> getStructureTileSets() { return mSpritesheetNamespace.getAssetBucket("structures"); }
//    public Map<String, String> getMiscellaneous() { return mAssetNameSpace.getAssetBucket("misc"); }


    public String getOrCreateMergedAssets(int width, int height, List<String> assetIds, String id) {
        // If the given id already exists, return early
        Animation currentAsset = mAnimationMap.get(id);
        if (currentAsset != null) {
            return id;
        }

        // if the list of ids to merge is empty, return null / No id
        if (!assetIds.isEmpty()) {
            BufferedImage[] toMerge = new BufferedImage[assetIds.size()];
            for (int i = 0; i < assetIds.size(); i++) {
                String iteratedID = assetIds.get(i);
                Animation iteratedAsset = mAnimationMap.get(iteratedID);
                if (iteratedAsset == null) { continue; }
                BufferedImage iteratedImage = SwingFXUtils.fromFXImage(iteratedAsset.toImage(), null);
                toMerge[i] = iteratedImage;
            }
            BufferedImage[] mergedImage = new BufferedImage[] { ImageUtils.mergeImages(width, height, toMerge) };

            createAsset("merged_asset", width, height, "merged", mergedImage, id);

        } else {
            id = null;
        }

        return id;
    }

    public int getFrame(String id) {
        Animation animation = mAnimationMap.getOrDefault(id, null);
        if (animation == null) {
            return -1;
        }

        return animation.getCurrentFrame();
    }
//    private void createAsset(String id, String asset, String effect, int frame, BufferedImage image) {
//        createAsset(id, asset, effect, frame, new BufferedImage[] { image });
//    }

    private void createAsset(String sheet, int width, int height, String effect, BufferedImage[] frames, String id) {
        Animation animation = new Animation(frames);
        animation.setMetadata(sheet, width, height, effect);
        mAnimationMap.put(id, animation);
    }
}