package main.ui.panels;

import java.awt.*;
import java.awt.Dimension;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import main.constants.Direction;
import main.game.stores.pools.ColorPalette;
import main.constants.Constants;
import main.constants.GameState;
import main.constants.Settings;
import main.game.camera.Camera;
import main.game.components.*;
import main.game.components.Vector3f;
import main.game.components.tile.Gem;
import main.game.components.Statistics;
import main.game.components.behaviors.UserBehavior;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.main.GameModel;
import main.game.stats.ResourceNode;
import main.game.stores.pools.asset.Asset;
import main.game.stores.pools.asset.AssetPool;
import main.game.stores.pools.FontPool;
import main.game.systems.texts.FloatingText;
import main.game.systems.texts.FloatingTextSystem;
import main.graphics.JScene;
import main.utils.ImageUtils;
import main.utils.MathUtils;

import javax.imageio.ImageIO;

public class GamePanel extends JScene {

    public static class Pair<X, Y> {
        public final X firstItem;
        public final Y secondItem;
        public Pair(X firstItem, Y secondItem) {
            this.firstItem = firstItem;
            this.secondItem = secondItem;
        }
    }

    private final Comparator<Entity> ordering = (tileEntity1, tileEntity2) -> {
        Tile tile1 = tileEntity1.get(Tile.class);
        Tile tile2 = tileEntity2.get(Tile.class);
        if (tile1 == null || tile2 == null) { return 0; }
        int rowDifference = tile1.getRow() - tile2.getRow();
        if (rowDifference != 0) { return rowDifference; }
        return tile1.getColumn() - tile2.getColumn();
    };

    private final PriorityQueue<Entity> tilesWithEntitiesWithNameplates = new PriorityQueue<>(ordering);
    private final PriorityQueue<Entity> tilesWithUnits = new PriorityQueue<>(ordering);

    private final Queue<Entity> tilesWithRoughTerrain = new LinkedList<>();
    private final Queue<Entity> tilesWithDestroyableBlocker = new LinkedList<>();
    private final Queue<Entity> tilesWithObstructions = new LinkedList<>();
    private final Queue<Entity> tilesWithGems = new LinkedList<>();
    private final Queue<Entity> tilesWithExits = new LinkedList<>();
    private final Queue<Entity> tilesWithOverlayAnimations = new LinkedList<>();
    private final Queue<Entity> unitsToRenderNameplatesFor = new LinkedList<>();
    private final Queue<Pair<String, int[]>> mUnitDirectionTags = new LinkedList<>();
    private final GameController gameController;
    private int currentSpriteSize = Constants.BASE_SPRITE_SIZE;
    boolean isLoadoutMode = false;
    private final BasicStroke mOutlineStroke = new BasicStroke(5f);

    public GamePanel(GameController controller, int width, int height) {
        super(width, height, GamePanel.class.getSimpleName());
        gameController = controller;

        setPreferredSize(new Dimension(width, height));
        setLayout(new GridBagLayout());
        setBackground(ColorPalette.TRANSPARENT);
        setOpaque(false);
    }

    @Override
    public void jSceneUpdate(GameModel model) {
        revalidate();
        repaint();
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!gameController.isRunning()) { return; }
        render(gameController.getModel(), g);
    }

    public void render(GameModel model, Graphics g) {

        currentSpriteSize = Settings.getInstance().getSpriteSize();
        isLoadoutMode = model.isLoadOutMode();

        renderBackground(g);
        collectAndQueueTileData(g, model);
    
        renderGems(g, model, tilesWithGems);
//        renderStructures(g, model, tilesWithRoughTerrain);
        renderUnits(g, model, tilesWithUnits);
        renderObstructions(g, model, tilesWithDestroyableBlocker);
        renderOverlayAnimations(g, model, tilesWithOverlayAnimations);
        renderNamePlates(g, tilesWithEntitiesWithNameplates);
        renderExits(g, model, tilesWithExits);
        renderCurrentlySelectedTile(g, model);
        renderCurrentlyMousedTile(g, model);
        renderUnitDirectionTags(g, model, mUnitDirectionTags);
        renderFloatingText(g, model);

        renderDebugMaterials(g, model);
        createBlurredBackground(model);
    }

    private void renderCurrentlySelectedTile(Graphics g, GameModel model) {
        Entity currentlySelectedEntity = model.getGameState().getSelectedEntity();
        if (currentlySelectedEntity == null) { return; }
        Tile tile = currentlySelectedEntity.get(Tile.class);
        Vector3f coordinate = model.getCamera().getGlobalCoordinates(
                tile.getColumn() * model.getSettings().getSpriteWidth(),
                tile.getRow() * model.getSettings().getSpriteHeight()
        );

        String reticleId = AssetPool.getInstance().getYellowReticleId(model);
        Asset asset = AssetPool.getInstance().getAsset(reticleId);
        Animation animation = asset.getAnimation();
        int tileX = (int) coordinate.x;
        int tileY = (int) coordinate.y;

        if (isLoadoutMode) {
            int spriteWidth = model.getSettings().getSpriteWidth();
            int spriteHeight = model.getSettings().getSpriteHeight();

            tileX = tile.column * spriteWidth;
            tileY = tile.row * spriteHeight;

            int width = animation.toImage().getWidth();
            int height = animation.toImage().getHeight();

            Vector3f centered = Vector3f.center(spriteWidth, spriteHeight, tileX, tileY, width, height);
            tileX = (int) centered.x;
            tileY = (int) centered.y;

            g.drawImage(animation.toImage(), tileX, tileY, null);
            animation.update();

            return;
        }

        int width = animation.toImage().getWidth();
        int height = animation.toImage().getHeight();
        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();
        Vector3f centered = Vector3f.center(spriteWidth, spriteHeight, tileX, tileY, width, height);
        tileX = (int) centered.x;
        tileY = (int) centered.y;

        g.drawImage(animation.toImage(), tileX, tileY, null);
        animation.update();
    }

    private BufferedImage mBlurredImage;
    private int mIteration = 0;
    private void renderBackground(Graphics g) {
        if (mBlurredImage != null) {
            g.drawImage(mBlurredImage, 0, 0, null);
        }
    }
    private void createBlurredBackground(GameModel model) {
        mIteration++;
        // Create background after first iteration where the image is guaranteed to have something
//        if (mBlurredImage != null || mIteration <= 1) { return; }
        if (mBlurredImage != null) { return; }

        BufferedImage bImg = new BufferedImage(
                (int) getPreferredSize().getWidth(),
                (int) getPreferredSize().getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D cg = bImg.createGraphics();

        collectAndQueueTileData(cg, model);
        try {
            mBlurredImage = ImageUtils.createBlurredImage(bImg);
            ImageIO.write(mBlurredImage, "png", new File("./output_image.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mBlurredImage = null;
        }
    }

    private void renderDebugMaterials(Graphics g, GameModel model) {
        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();
        boolean isDebugMode = model.getSettings().isDebugMode();

        int startColumn = (int) Math.max(0, model.getVisibleStartOfColumns());
        int startRow = (int) Math.max(0, model.getVisibleStartOfRows());
        int endColumn = (int) Math.min(model.getColumns(), model.getVisibleEndOfColumns() + 1);
        int endRow = (int) Math.min(model.getRows(), model.getVisibleEndOfRows() + 1);

        if (isLoadoutMode) {
            startRow = 0;
            startColumn = 0;
            endRow = model.getRows();;
            endColumn = model.getColumns();
        }

        for (int row = startRow; row < endRow; row++) {
            for (int column = startColumn; column < endColumn; column++) {
                Entity tileEntity = model.tryFetchingTileAt(row, column);
                Assets assets = tileEntity.get(Assets.class);
                Tile tile = tileEntity.get(Tile.class);
                // When panel needs to be fit to screen, cont use camera
                Vector3f xAndY = model.getCamera().getGlobalCoordinates(
                        tile.getColumn() * spriteWidth,
                        tile.getRow() * spriteHeight
                );
                int tileX = (int) xAndY.x;
                int tileY = (int) xAndY.y;
                // If fitting to screen, render tiles using the entire panels width and height
                if (isLoadoutMode) {
                    tileX = column * spriteWidth;
                    tileY = row * spriteHeight;
                }

                if (isDebugMode) {
                    g.setColor(ColorPalette.WHITE);
                    g.setFont(FontPool.getInstance().getFont(8).deriveFont(Font.BOLD));
                    int debugX = (int) (tileX + (spriteWidth * .2));
                    int debugY = (int) (tileY + (spriteHeight * .3));
                    g.drawString("(X,Y) " + tile.row + ", " + tile.column, debugX, debugY);
                    g.drawString("(HITE) " + tile.getHeight(), debugX, debugY + 10);
                    g.drawString("(OBS) " + (tile.getObstruction() != null), debugX, debugY + 20);
                }
            }
        }
    }

    private void renderUnitDirectionTags(Graphics g, GameModel model, Queue<Pair<String,int[]>> mUnitDirectionTags) {
        while (!mUnitDirectionTags.isEmpty()) {
            Pair<String, int[]> directionalTag = mUnitDirectionTags.poll();
            String str = directionalTag.firstItem;
            int x = directionalTag.secondItem[0];
            int y = directionalTag.secondItem[1];
            drawStrokedText((Graphics2D) g, x, y, str);
        }
    }

    private void renderFloatingText(Graphics gg, GameModel model) {
        Graphics2D g = (Graphics2D) gg;

        FloatingTextSystem floatingTextSystem = model.mSystem.floatingText;

        for (FloatingText floatingText : floatingTextSystem.getFloatingText()) {
            g.setFont(floatingTextSystem.getFont());
            int x = Camera.getInstance().globalX(floatingText.getX());
            int y = Camera.getInstance().globalY(floatingText.getY() - (floatingText.getHeight() / 2));

//            floatingText.debug(g);

            // remember the original settings
            extracted(g, x, y, floatingText, floatingTextSystem);
        }
    }

    private static void extracted(Graphics2D g, int x, int y, FloatingText floatingText,  FloatingTextSystem floatingTextSystem) {
        Color originalColor = g.getColor();
        Stroke originalStroke = g.getStroke();
        RenderingHints originalHints = g.getRenderingHints();
        AffineTransform originalTransform = g.getTransform();

        // create a glyph vector from your text, then get the shape object
        GlyphVector glyphVector = g.getFont().createGlyphVector(g.getFontRenderContext(), floatingText.getValue());
        Shape textShape = glyphVector.getOutline();

        g.setColor(floatingText.getBackground());
        g.setStroke(floatingTextSystem.getOutlineStroke());
        g.translate(x, y);
        g.draw(textShape); // draw outline

        g.setColor(floatingText.getForeground());
        g.fill(textShape); // fill the shape

        // reset to original settings after painting
        g.setColor(originalColor);
        g.setStroke(originalStroke);
        g.setRenderingHints(originalHints);
        g.setTransform(originalTransform);
    }

    private void drawStrokedText(Graphics2D g, int x, int y, String txt) {
        Color originalColor = g.getColor();
        Stroke originalStroke = g.getStroke();
        RenderingHints originalHints = g.getRenderingHints();
        AffineTransform originalTransform = g.getTransform();

        // create a glyph vector from your text, then get the shape object
        GlyphVector glyphVector = g.getFont().createGlyphVector(g.getFontRenderContext(), txt);
        Shape textShape = glyphVector.getOutline();

        g.setColor(Color.BLACK);
        g.setStroke(mOutlineStroke);
        g.translate(x, y);
        g.draw(textShape); // draw outline

        g.setColor(Color.WHITE);
        g.fill(textShape); // fill the shape

        // reset to original settings after painting
        g.setColor(originalColor);
        g.setStroke(originalStroke);
        g.setRenderingHints(originalHints);
        g.setTransform(originalTransform);
    }

    private void renderCurrentlyMousedTile(Graphics g, GameModel model) {
        Entity currentlyMousedAtEntity = model.tryFetchingTileMousedAt();
        if (currentlyMousedAtEntity == null) { return; }
        Tile tile = currentlyMousedAtEntity.get(Tile.class);
        Vector3f coordinate = model.getCamera().getGlobalCoordinates(
                tile.getColumn() * model.getSettings().getSpriteWidth(),
                tile.getRow() * model.getSettings().getSpriteHeight()
        );

        String reticleId = AssetPool.getInstance().getBlueReticleId(model);
        Asset asset = AssetPool.getInstance().getAsset(reticleId);
        Animation animation = asset.getAnimation();
        int tileX = (int) coordinate.x;
        int tileY = (int) coordinate.y;

        if (isLoadoutMode) {
            int spriteWidth = model.getSettings().getSpriteWidth();
            int spriteHeight = model.getSettings().getSpriteHeight();

            tileX = tile.column * spriteWidth;
            tileY = tile.row * spriteHeight;

            int width = animation.toImage().getWidth();
            int height = animation.toImage().getHeight();

            Vector3f centered = Vector3f.center(spriteWidth, spriteHeight, tileX, tileY, width, height);
            tileX = (int) centered.x;
            tileY = (int) centered.y;

            g.drawImage(animation.toImage(), tileX, tileY, null);
            animation.update();

            return;
        }

        int width = animation.toImage().getWidth();
        int height = animation.toImage().getHeight();
        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();
        Vector3f centered = Vector3f.center(spriteWidth, spriteHeight, tileX, tileY, width, height);
        tileX = (int) centered.x;
        tileY = (int) centered.y;

        g.drawImage(animation.toImage(), tileX, tileY, null);
        animation.update();
    }

    private void renderExits(Graphics g, GameModel model, Queue<Entity> queue) {
        while (!queue.isEmpty()) {
            Entity entity = queue.poll();
            Tile tile = entity.get(Tile.class);
            int tileX = Camera.getInstance().globalX(entity);
            int tileY = Camera.getInstance().globalY(entity);
            g.setColor(ColorPalette.TRANSLUCENT_BLACK_V1);
            g.fillRect(tileX, tileY, currentSpriteSize, currentSpriteSize);
        }

//        g.setColor(Color.blue);
//        int x = (int) DigitalDifferentialAnalysis.startV.x; //Camera.getInstance().globalX((int) DigitalDifferentialAnalysis.startV.x);
//        int y = (int) DigitalDifferentialAnalysis.startV.y;//Camera.getInstance().globalY((int) DigitalDifferentialAnalysis.startV.y);
//
//        g.fillOval(x+32, y+32, 50, 50);
//
//        int x2 = (int) DigitalDifferentialAnalysis.endV.x; //Camera.getInstance().globalX((int) DigitalDifferentialAnalysis.endV.x);
//        int y2 = (int) DigitalDifferentialAnalysis.endV.x; //Camera.getInstance().globalY((int) DigitalDifferentialAnalysis.endV.y);
//
//        g.drawLine(x+32, y+32, x2 + 32, y2+32);
//
//        g.setColor(Color.RED);
//        g.fillOval(x2, y2, 50, 50);
//
//        System.out.println(StringFormatter.format("p1 (X:{},Y:{})", x, y));
//        System.out.println(StringFormatter.format("p2 (X:{},Y:{})", x2, y2));
    }

    private void renderNamePlates(Graphics graphics, PriorityQueue<Entity> queue) {
        while (!queue.isEmpty()) {
            Entity tileEntity = queue.poll();
            Tile tile = tileEntity.get(Tile.class);
            Entity unitEntity = tile.getUnit();
            if (unitEntity == null) { continue; }
            drawHealthBar(graphics, unitEntity);

            Statistics statistics = unitEntity.get(Statistics.class);
            ResourceNode mana = statistics.getResourceNode(Statistics.MANA);
            ResourceNode health = statistics.getResourceNode(Statistics.HEALTH);

            if (health.getPercentage() == 1 && mana.getPercentage() == 1) { continue; }
            if (health.getPercentage() != 1 && health.getPercentage() != 0) {
//                renderResourceBar(graphics, newX, newY - 6, currentSpriteSize, health.getPercentage(),
//                        ColorPalette.BLACK, ColorPalette.RED, 8);
            }
            if (mana.getPercentage() != 1 || mana.getPercentage() != 0) {
//                stamina.getPercentage() != 1 || stamina.getPercentage() != 0) {
//                renderResourceBar(graphics, newX, newY, (currentSpriteSize), mana.getPercentage(),
//                        ColorPalette.BLACK, ColorPalette.BLUE, 8);
//            renderResourceBar(graphics, newX + (currentSpriteSize / 2), newY, (currentSpriteSize / 2), stamina.getPercentage(),
//                    ColorPalette.BLACK, ColorPalette.YELLOW, 8);
            }
        }      
    }

    private void renderOverlayAnimations(Graphics g, GameModel model, Queue<Entity> queue) {
        while(!queue.isEmpty()) {
            Entity entity = queue.poll();
            int tileX = Camera.getInstance().globalX(entity);
            int tileY = Camera.getInstance().globalY(entity);
            Overlay ca = entity.get(Overlay.class);
            if (ca.hasOverlay()) {
                BufferedImage image = ca.getAnimation().toImage();
                g.drawImage(image, tileX, tileY,  null);
            }
        }
    }

    private final Map<String, Color> regionMap = new HashMap<>();

    private void collectAndQueueTileData(Graphics g, GameModel model) {

        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();

        int startColumn = (int) Math.max(0, model.getVisibleStartOfColumns());
        int startRow = (int) Math.max(0, model.getVisibleStartOfRows());
        int endColumn = (int) Math.min(model.getColumns(), model.getVisibleEndOfColumns() + 1);
        int endRow = (int) Math.min(model.getRows(), model.getVisibleEndOfRows() + 1);

        if (isLoadoutMode) {
            startRow = 0;
            startColumn = 0;
            endRow = model.getRows();;
            endColumn = model.getColumns();
        }

        for (int row = startRow; row < endRow; row++) {
            for (int column = startColumn; column < endColumn; column++) {
                Entity tileEntity = model.tryFetchingTileAt(row, column);
                Assets assets = tileEntity.get(Assets.class);
                Tile tile = tileEntity.get(Tile.class);
                // When panel needs to be fit to screen, cont use camera
                Vector3f coordinates = model.getCamera().getGlobalCoordinates(
                        tile.getColumn() * spriteWidth,
                        tile.getRow() * spriteHeight
                );
                int tileX = (int) coordinates.x;
                int tileY = (int) coordinates.y;
                // If fitting to screen, render tiles using the entire panels width and height
                if (isLoadoutMode) {
                    tileX = column * spriteWidth;
                    tileY = row * spriteHeight;
                }


                if (tile.getLiquid() != null) {
                    String id = assets.getId(Assets.LIQUID_ASSET);
                    Asset asset = AssetPool.getInstance().getAsset(id);
                    if (asset != null) {
                        Animation animation = asset.getAnimation();
                        g.drawImage(animation.toImage(), tileX, tileY, null);
                    }
                } else {
                    String id = assets.getId(Assets.TERRAIN_ASSET);
                    Asset asset = AssetPool.getInstance().getAsset(id);
                    if (asset != null) {
                        Animation animation = asset.getAnimation();
                        g.drawImage(animation.toImage(), tileX, tileY, null);
                    }
                }

                String spawnRegion = (String) tile.get(Tile.SPAWN_REGION);
                if (spawnRegion != null) {
                    Color c = regionMap.get(spawnRegion);
                    if (c == null) {
                        c = ColorPalette.getRandomColor();
                        regionMap.put(spawnRegion, new Color(c.getRed(), c.getGreen(), c.getBlue(), 150));
                    }

                    g.setColor(c);
                    g.fillRect(tileX, tileY, spriteWidth, spriteHeight);
                    g.setColor(c.darker().darker().darker());
                    for (int i = 0; i < 5; i++) {
                        g.drawRect(tileX + i, tileY + i, spriteWidth - (i * 2), spriteHeight - (i * 2));
                    }
                }

                // Draw the directional shadows
                List<String> directionalShadowIds = assets.getIds(Assets.DIRECTIONAL_SHADOWS_ASSET);
                if (!directionalShadowIds.isEmpty()) {
                    for (String id : directionalShadowIds) {
                        Animation animation = AssetPool.getInstance().getAnimation(id);
                        if (animation != null) {
                            g.drawImage(animation.toImage(), tileX, tileY, null);
                        }
                    }
                }

                if (!tile.isWall()) {
                    String id = assets.getId(Assets.DEPTH_SHADOWS_ASSET);
                    Asset asset = AssetPool.getInstance().getAsset(id);
                    if (asset != null) {
                        Animation animation = asset.getAnimation();
                        g.drawImage(animation.toImage(), tileX, tileY, null);
                    }
                }

                if (tile.mUnit != null) { tilesWithUnits.add(tileEntity); }
                if (tile.mUnit != null) { tilesWithEntitiesWithNameplates.add(tileEntity); }

                String obstruction = tile.getObstruction();
                if (obstruction != null) {
                    if (tile.isRoughTerrain()) {
                        tilesWithRoughTerrain.add(tileEntity);
                    } else if (tile.isDestroyableBlocker()) {
                        tilesWithDestroyableBlocker.add(tileEntity);
                    } else {
                        tilesWithDestroyableBlocker.add(tileEntity);
                    }
                }

//                if (tile.getGreaterStructure() >= 0) { tilesWithGreaterStructures.add(entity); }
//                if (tile.getLesserStructure() >= 0) { tilesWithLesserStructures.add(entity); }
                if (tile.getGem() != null) { tilesWithGems.add(tileEntity); }
//                if (tile.getExit() > 0) { tilesWithExits.add(entity); }

                Overlay ca = tileEntity.get(Overlay.class);
                if (ca.hasOverlay()) { tilesWithOverlayAnimations.add(tileEntity); }
            }
        }
    }


    private void renderPerforatedTile2(Graphics graphics, GameModel model, Entity tileEntity, Color main, Color outline) {
        renderPerforatedTile2(graphics, model, tileEntity, main, outline, false);
    }
    private void renderPerforatedTile2(Graphics graphics, GameModel model,
                                       Entity tileEntity, Color inside, Color outside, boolean flip) {
        Tile tile = tileEntity.get(Tile.class);
        int spriteWidth = model.getSettings().getSpriteWidth();
        int spriteHeight = model.getSettings().getSpriteHeight();
        Vector3f coordinate = model.getCamera().getGlobalCoordinates(
                tile.getColumn() * spriteWidth,
                tile.getRow() * spriteHeight
        );
//        coordinate = model.getCamera().getGlobalCoordinates(
//                model,
//                tile.getRow(),
//                tile.getColumn()
//        );
        int globalX = (int) coordinate.x;
        int globalY = (int) coordinate.y;
        int size = 4;
        int newSize = currentSpriteSize - (currentSpriteSize - size);

        if (flip) {
            Color temp = inside;
            inside = outside;
            outside = temp;
        }

        graphics.setColor(outside);
        graphics.fillRect(globalX, globalY, currentSpriteSize, newSize);
        graphics.fillRect(globalX, globalY, newSize, currentSpriteSize);
        graphics.fillRect(globalX + currentSpriteSize - newSize, globalY, newSize, currentSpriteSize);
        graphics.fillRect(globalX, globalY + currentSpriteSize - newSize, currentSpriteSize, newSize);
        graphics.setColor(inside);
        graphics.fillRect(globalX + size, globalY + size,
                currentSpriteSize - (size * 2), currentSpriteSize - (size * 2));
    }

    private void renderUiHelpers(Graphics graphics, GameModel model, Entity unit) {
        ActionManager actionManager = unit.get(ActionManager.class);
        MovementManager movementManager = unit.get(MovementManager.class);

        boolean showSelectedMovementPathing = model.getGameStateBoolean(GameState.SHOW_SELECTED_UNIT_MOVEMENT_PATHING);
        boolean showSelectedActionPathing = model.getGameStateBoolean(GameState.SHOW_SELECTED_UNIT_ACTION_PATHING);

        if (showSelectedMovementPathing) {
            for (Entity tile : movementManager.tilesInRange) {
                if (movementManager.tilesInPath.contains(tile)) { continue; }
                renderPerforatedTile2(graphics, model, tile, ColorPalette.TRANSLUCENT_WHITE_V1, ColorPalette.BLACK);
                
            }
            if (unit.get(UserBehavior.class) != null) {
                for (Entity tile : movementManager.tilesInPath) {
                    renderPerforatedTile2(graphics, model, tile, ColorPalette.BLACK, ColorPalette.WHITE, true);
                }
            }
        } else if (showSelectedActionPathing) {
            for (Entity tile : actionManager.mTargets) {
                if (actionManager.mLineOfSight.contains(tile)) { continue; }
                if (actionManager.mAreaOfEffect.contains(tile)) { continue; }
                renderPerforatedTile2(graphics, model, tile, ColorPalette.TRANSLUCENT_BLACK_V3, ColorPalette.BLACK);
            }
            for (Entity tile : actionManager.mLineOfSight) {
//                if (actionManager.mAreaOfEffect.contains(tile)) { continue; }
                renderPerforatedTile2(graphics, model, tile, ColorPalette.TRANSPARENT, ColorPalette.WHITE, true);
            }
            for (Entity tile : actionManager.mAreaOfEffect) {
                renderPerforatedTile2(graphics, model, tile, ColorPalette.TRANSLUCENT_RED_V1, ColorPalette.TRANSLUCENT_RED_V2);
            }
        }

        if (actionManager.targeting != null) {
//            renderPerforatedTile2(graphics, manager.targeting, ColorPalette.BLACK, ColorPalette.BLACK);
        }
    }

    private void renderObstructions(Graphics graphics, GameModel model, Queue<Entity> queue) {

        int configuredSpriteHeight = model.getSettings().getSpriteHeight();
        int configuredSpriteWidth = model.getSettings().getSpriteWidth();

        while(!queue.isEmpty()) {
            Entity entity = queue.poll();
            Assets assets = entity.get(Assets.class);
            String id = assets.getId(Assets.OBSTRUCTION_ASSET);
            Asset asset = AssetPool.getInstance().getAsset(id);;
            if (asset == null) { continue; }
            Animation animation = asset.getAnimation();
            String animationType = asset.getAnimationType();

            Tile tile = entity.get(Tile.class);
            int x = Camera.getInstance().globalX(tile.column * configuredSpriteWidth);
            int y = Camera.getInstance().globalY(tile.row * configuredSpriteHeight);
            if (isLoadoutMode) {
                x = tile.getColumn() * configuredSpriteWidth;
                y = tile.getRow() * configuredSpriteHeight;
            }

            if (animationType.equalsIgnoreCase(AssetPool.STRETCH_Y_ANIMATION)) {
                y += configuredSpriteHeight - animation.toImage().getHeight();
            }

            graphics.drawImage(animation.toImage(), x, y, null);
        }
    }


    private void renderGems(Graphics graphics, GameModel model, Queue<Entity> queue) {
        while(!queue.isEmpty()) {
            Entity entity = queue.poll();
            Tile tile = entity.get(Tile.class);
            int tileX = Camera.getInstance().globalX(entity);
            int tileY = Camera.getInstance().globalY(entity);
            Gem gem = tile.getGem();
            if (gem == null) { continue; }
            graphics.setColor(ColorPalette.TRANSLUCENT_WHITE_V2);
            graphics.fillRect(tileX, tileY, currentSpriteSize, currentSpriteSize);
            graphics.setFont(FontPool.getInstance().getFont(10));
            graphics.setColor(ColorPalette.BLACK);
            graphics.drawString(gem.name().substring(0, Math.min(gem.name().length(), 8)), tileX, tileY + currentSpriteSize / 2);
//            Animation animation = AssetPool.getInstance().getAnimation(gem.animationId);
//            graphics.drawImage(animation.toImage(), tileX, tileY, null);
//            animation.update();
        }
    }


    private void renderUnits(Graphics graphics, GameModel model, Queue<Entity> queue) {

        int configuredSpriteHeight = model.getSettings().getSpriteHeight();
        int configuredSpriteWidth = model.getSettings().getSpriteWidth();

        if (model.mGameState.getObject(GameState.CURRENTLY_SELECTED) != null) {
            Object object = model.mGameState.getObject(GameState.CURRENTLY_SELECTED);
            if (object == null) { return; }
            Entity entity = (Entity) object;
            Tile tile = entity.get(Tile.class);
            if (tile.mUnit != null) { renderUiHelpers(graphics, model, tile.mUnit); }
        }

        while (!queue.isEmpty()) {
            Entity tileEntity = queue.poll();
            Tile tile = tileEntity.get(Tile.class);
            Entity unitEntity = tile.getUnit();
            if (unitEntity == null) { continue; } // TODO why is this null sometimes?

            Assets unitAssets = unitEntity.get(Assets.class);
            String id = unitAssets.getId(Assets.UNIT_ASSET);
            Asset asset = AssetPool.getInstance().getAsset(id);
            if (asset == null) { continue; }
            String animationType = asset.getAnimationType();
            Animation animation = asset.getAnimation();

            // Default origin with not animation consideration
            int x = Camera.getInstance().globalX(tile.column * configuredSpriteWidth);
            int y = Camera.getInstance().globalY(tile.row * configuredSpriteHeight);
            if (isLoadoutMode) {
                x = tile.getColumn() * configuredSpriteWidth;
                y = tile.getRow() * configuredSpriteHeight;
            }

            MovementManager movementManager = unitEntity.get(MovementManager.class);
            MovementTrack movementTrack = unitEntity.get(MovementTrack.class);
            if (movementManager.moved) {
                x = model.getCamera().globalX((int) movementTrack.location.x);
                y = model.getCamera().globalY((int) movementTrack.location.y);
            }
            if (animationType.equalsIgnoreCase(AssetPool.STRETCH_Y_ANIMATION)) {
                  y += configuredSpriteHeight - animation.toImage().getHeight();
            }

            graphics.drawImage(animation.toImage(), x, y, null);
            unitsToRenderNameplatesFor.add(tileEntity);

            DirectionalFace directionalFace = unitEntity.get(DirectionalFace.class);
            graphics.setFont(FontPool.getInstance().getDefaultFont());
            String str = directionalFace.getFacingDirection().name();
            if (str.equalsIgnoreCase(Direction.North.name())) {
                str = "North ↑";
            } else if (str.equalsIgnoreCase(Direction.East.name())) {
                str = "East →";
            } else if (str.equalsIgnoreCase(Direction.South.name())) {
                str = "South ↓";
            } else if (str.equalsIgnoreCase(Direction.West.name())) {
                str = "West ←";
            }
            graphics.setColor(ColorPalette.WHITE);
            graphics.setFont(FontPool.getInstance().getFont(12).deriveFont(Font.BOLD));

            mUnitDirectionTags.add(new Pair<>(str, new int[]{ x, y }));
//            drawStrokedText((Graphics2D) graphics, x, y, str);

//            Overlay ca = unit.get(Overlay.class);
//            if (ca.hasOverlay()) {
//                animation = ca.getAnimation();
//                graphics.drawImage(
//                        animation.toImage(),
//                        Camera.getInstance().globalX(animation.getAnimatedX()),
//                        Camera.getInstance().globalY(animation.getAnimatedY()),
//                        null
//                );
//            }

            // nameplatesToDraw.add(unit);
        }
    }

    private void drawHealthBar(Graphics graphics, Entity unit) {
        // Check if we should render health or energy bar
        Statistics statistics = unit.get(Statistics.class);
        ResourceNode mana = statistics.getResourceNode(Statistics.MANA);
        ResourceNode health = statistics.getResourceNode(Statistics.HEALTH);
//        ResourceNode stamina = summary.getResourceNode(Summary.STAMINA);
        if (health.getPercentage() == 1 && mana.getPercentage() == 1) { return; }
//        if (health.getPercentage() == 1 && energy.getPercentage() == 1 && stamina.getPercentage() == 1) { return; }

//        Animation animation = unit.get(Animation.class);
//
//        Vector3f vector = animation.getVector();

//        int xPosition = (int) vector.x;
//        int yPosition = (int) vector.y + currentSpriteSize;
//        int newX = Camera.getInstance().globalX(xPosition);
//        int newY = Camera.getInstance().globalY(yPosition);
//        graphics.setColor(Color.WHITE);
//        graphics.setFont(FontPool.getInstance().getFont(10));

//        int x = Camera.getInstance().globalX(tile.column * configuredSpriteWidth);
//        int y = Camera.getInstance().globalY(tile.row * configuredSpriteHeight);
//        if (isLoadoutMode) {
//            x = tile.getColumn() * configuredSpriteWidth;
//            y = tile.getRow() * configuredSpriteHeight;
//        }

        // Render the energy and health resource bars
//        if (health.getPercentage() != 1 && health.getPercentage() != 0) {
//            renderResourceBar(graphics, newX, newY - 6, currentSpriteSize, health.getPercentage(),
//                    ColorPalette.BLACK, ColorPalette.RED, 4);
//        }
//        if (energy.getPercentage() != 1 || energy.getPercentage() != 0 ||
//                stamina.getPercentage() != 1 || stamina.getPercentage() != 0) {
//            renderResourceBar(graphics, newX, newY - 1, (currentSpriteSize), energy.getPercentage(),
//                    ColorPalette.BLACK, ColorPalette.BLUE, 4);
//            renderResourceBar(graphics, newX, newY + 4, (currentSpriteSize), stamina.getPercentage(),
//                    ColorPalette.BLACK, ColorPalette.YELLOW, 4);
//        }

//        if (health.getPercentage() != 1 && health.getPercentage() != 0) {
//            renderResourceBar(graphics, newX, newY - 6, currentSpriteSize, health.getPercentage(),
//                    ColorPalette.BLACK, ColorPalette.RED, 8);
//        }
//        if (mana.getPercentage() != 1 || mana.getPercentage() != 0) {
////                stamina.getPercentage() != 1 || stamina.getPercentage() != 0) {
//            renderResourceBar(graphics, newX, newY, (currentSpriteSize), mana.getPercentage(),
//                    ColorPalette.BLACK, ColorPalette.BLUE, 8);
////            renderResourceBar(graphics, newX + (currentSpriteSize / 2), newY, (currentSpriteSize / 2), stamina.getPercentage(),
////                    ColorPalette.BLACK, ColorPalette.YELLOW, 8);
//        }

//        if (health.getPercentage() != 1 && health.getPercentage() != 0) {
//            renderResourceBar(graphics, newX, newY - 6, currentSpriteSize, health.getPercentage(),
//                    ColorPalette.BLACK, ColorPalette.RED, 8);
//        }
//        if (energy.getPercentage() != 1 && energy.getPercentage() != 0) {
//            renderResourceBar(graphics, newX, newY, (currentSpriteSize / 2), energy.getPercentage(),
//                    ColorPalette.BLACK, ColorPalette.BLUE, 8);
//        }
//        if (stamina.getPercentage() != 1 && stamina.getPercentage() != 0) {
//            renderResourceBar(graphics, newX + (currentSpriteSize / 2), newY, (currentSpriteSize / 2), stamina.getPercentage(),
//                    ColorPalette.BLACK, ColorPalette.YELLOW, 8);
//        }
//        renderResourceBar(graphics, newX, newY - 6, currentSpriteSize, health.getPercentage(),
//                ColorPalette.BLACK, ColorPalette.RED, 8);
//        renderResourceBar(graphics, newX, newY, (currentSpriteSize / 2), energy.getPercentage(),
//                ColorPalette.BLACK, ColorPalette.BLUE, 8);
//        renderResourceBar(graphics, newX + (currentSpriteSize / 2), newY, (currentSpriteSize / 2), stamina.getPercentage(),
//                ColorPalette.BLACK, ColorPalette.YELLOW, 8);
    }

    public static void renderResourceBar(Graphics graphics, int x, int y, int size, 
        float amt, Color bg, Color fg, int height) {
        graphics.setColor(bg);
        graphics.fillRoundRect(x, y, size, height, 2, 2);
        float barWidth = MathUtils.map(amt, 0, 1, 0, size - 4);
        graphics.setColor(fg);
        graphics.fillRoundRect(x + 2, y + 2, (int) barWidth, height / 2, 2, 2);
    }

    public void renderCoordinates(Graphics g, int tileX, int tileY, Dimension dimension, Entity tile) {
        Tile details = tile.get(Tile.class);
        g.setColor(ColorPalette.BLACK);
        g.drawRect(tileX, tileY, dimension.width, dimension.height);

        if (details.isOccupied()) {
            g.setColor(Color.RED);
        } else if (details.isNotNavigable()) {
            g.setColor(Color.GREEN);
        } else if (details.isWall()) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.DARK_GRAY);
        }
        g.fillRect(tileX, tileY, (int)dimension.width, (int)dimension.height);
        g.setColor(Color.BLACK);
        g.drawString(
                details.row + ", " + details.column,
                tileX + (currentSpriteSize / 6),
                tileY + (currentSpriteSize / 2)
        );
    }
}
