package main.ui.panels;

import java.awt.*;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.*;

import main.game.stores.pools.ColorPalette;
import main.constants.Constants;
import main.constants.GameState;
import main.constants.Settings;
import main.game.camera.Camera;
import main.game.components.*;
import main.game.components.Vector;
import main.game.components.tile.Gem;
import main.game.components.Summary;
import main.game.components.behaviors.UserBehavior;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.main.GameModel;
import main.game.stats.ResourceNode;
import main.game.stores.pools.AssetPool;
import main.game.stores.pools.FontPool;
import main.graphics.JScene;
import main.utils.MathUtils;

public class GamePanel extends JScene {

    private final Comparator<Entity> ordering = (o1, o2) -> {
        Entity en1 = o1.get(Tile.class).mUnit;
        Entity en2 = o2.get(Tile.class).mUnit;
        if (en1 == null || en2 == null) { return 0; }
        Vector v1 = en1.get(Animation.class).getVector();
        Vector v2 = en2.get(Animation.class).getVector();
        return (int) (v1.y - v2.y);
    };

    private final PriorityQueue<Entity> tilesWithEntitiesWithNameplates = new PriorityQueue<>(ordering);
    private final PriorityQueue<Entity> tilesWithUnits = new PriorityQueue<>(ordering);

    private final Queue<Entity> tilesWithRoughTerrain = new LinkedList<>();
    private final Queue<Entity> tilesWithDestroyableBlocker = new LinkedList<>();
    private final Queue<Entity> tilesWithObstructions = new LinkedList<>();
    private final Queue<Entity> tilesWithGems = new LinkedList<>();
    private final Queue<Entity> tilesWithExits = new LinkedList<>();
    private final Queue<Entity> tilesWithOverlayAnimations = new LinkedList<>();
    private Entity currentlyMousedAtEntity = null;
    private final GameController gameController;
    private int currentSpriteSize = Constants.BASE_SPRITE_SIZE;

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
        g.dispose();
    }

    public void render(GameModel model, Graphics g) {

        currentSpriteSize = Settings.getInstance().getSpriteSize();
        collectAndQueueTileData(g, model);
    
        renderGems(g, model, tilesWithGems);
        renderStructures(g, model, tilesWithRoughTerrain);
        renderUnits(g, model, tilesWithUnits);
        renderStructures(g, model, tilesWithDestroyableBlocker);
        renderOverlayAnimations(g, model, tilesWithOverlayAnimations);
        renderNamePlates(g, tilesWithEntitiesWithNameplates);
        renderExits(g, model, tilesWithExits);
        renderCurrentlyMousedTile(g, model, currentlyMousedAtEntity);

        model.system.floatingText.render(g);
    }

    private void renderCurrentlyMousedTile(Graphics g, GameModel model, Entity entity) {
        if (currentlyMousedAtEntity == null) { return; }
        int tileX = Camera.getInstance().globalX(entity);
        int tileY = Camera.getInstance().globalY(entity);

        Animation animation = AssetPool.getInstance().getAnimation(AssetPool.getInstance().reticleId);

        int width = animation.toImage().getWidth();
        int height = animation.toImage().getHeight();
        Vector centered = Vector.center(Settings.getInstance().getSpriteSize(), tileX, tileY, width, height);
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

    private void renderNamePlates(Graphics g, PriorityQueue<Entity> queue) {
        while (!queue.isEmpty()) {
            Entity tileEntity = queue.poll();
            Entity entity = tileEntity.get(Tile.class).mUnit;
            if (entity == null) { continue; }
            drawHealthBar(g, entity);
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

    private final Map<Integer, Color> regionMap = new HashMap<>();
    private void collectAndQueueTileData(Graphics g, GameModel model) {
        int startColumn = (int) Math.max(0, model.getVisibleStartOfColumns());
        int startRow = (int) Math.max(0, model.getVisibleStartOfRows());
        int endColumn = (int) Math.min(model.getColumns(), model.getVisibleEndOfColumns() + 2);
        int endRow = (int) Math.min(model.getRows(), model.getVisibleEndOfRows() + 2);
        currentlyMousedAtEntity = model.tryFetchingTileMousedAt();
                
        for (int row = startRow; row < endRow; row++) {
            for (int column = startColumn; column < endColumn; column++) {
                Entity entity = model.tryFetchingTileAt(row, column);
                Tile tile = entity.get(Tile.class);

                int size = Settings.getInstance().getSpriteSize();
                int tileX = Camera.getInstance().globalX(entity);
                int tileY = Camera.getInstance().globalY(entity);

                if (tile.getLiquid() >= 0) {
                    Animation animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.LIQUID));
                    g.drawImage(animation.toImage(), tileX, tileY, null);
                    animation.update();
                } else {
                    Animation animation = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.TERRAIN));
                    g.drawImage(animation.toImage(), tileX, tileY, null);
                }

                int spawnRegion = tile.getSpawnRegion();
                if (spawnRegion >= 0) {
                    Color c = regionMap.get(spawnRegion);
                    if (c == null) {
                        c = ColorPalette.getRandomColor();
                        regionMap.put(spawnRegion, new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
                    }
//                    g.setColor(c);
//                    g.fillRect(tileX, tileY, size, size);
                }



//                Color c = (Color) tile.getProperty(Tile.SPAWN);
//                g.setColor(c);
//                g.fillRect(tileX, tileY, size, size);

//                for (BufferedImage heightShadow : tile.shadows) {
//                    g.drawImage(heightShadow, tileX, tileY, null);
//                }

//                SpriteSheetMap spriteMap = AssetPool.getInstance().getSpriteMap(Constants.TILES_SPRITESHEET_FILEPATH);
//                SpriteSheet sheet = spriteMap.get("directional_shadows");

//                for
                Set<String> shadowAssets = tile.getAssets(Tile.SHADOW);
                for (String asset : shadowAssets) {
                    String id = tile.getAsset(asset);
                    Animation animation = AssetPool.getInstance().getAnimation(id);
                    if (animation == null) { continue; }
                    g.drawImage(animation.toImage(), tileX, tileY, null);
                }
//                for (int shadowId : tile.shadowIds) {
//                    Animation animation = AssetPool.getInstance().getAssetAnimation(shadowId);
//                    if (animation == null) { continue; }
//                    g.drawImage(animation.toImage(), tileX, tileY, null);
//                }
//                int size = Settings.getInstance().getSpriteSize();
                g.setColor(ColorPalette.TRANSLUCENT_BLACK_V2);
                g.drawRect(tileX, tileY, size, size);

//                g.setColor(ColorPalette.WHITE);
//                g.setFont(FontPool.getInstance().getFont(12).deriveFont(Font.BOLD));
//                g.drawString(tile.row + ", " + tile.column, tileX + 32, tileY + 32);

                if (tile.mUnit != null) { tilesWithUnits.add(entity); }
                if (tile.mUnit != null) { tilesWithEntitiesWithNameplates.add(entity); }

                if (tile.getObstruction() != null) {
                    if (tile.isRoughTerrain()) {
                        tilesWithRoughTerrain.add(entity);
                    } else if (tile.isDestroyableBlocker()) {
                        tilesWithDestroyableBlocker.add(entity);
                    }
                }

//                if (tile.getGreaterStructure() >= 0) { tilesWithGreaterStructures.add(entity); }
//                if (tile.getLesserStructure() >= 0) { tilesWithLesserStructures.add(entity); }
                if (tile.getGem() != null) { tilesWithGems.add(entity); }
//                if (tile.getExit() > 0) { tilesWithExits.add(entity); }

                Overlay ca = entity.get(Overlay.class);
                if (ca.hasOverlay()) { tilesWithOverlayAnimations.add(entity); }
            }
        }
    }

    private void renderPerforatedTile2(Graphics graphics, Entity tile, Color main, Color outline) {
        int globalX = Camera.getInstance().globalX(tile);
        int globalY = Camera.getInstance().globalY(tile);
        int size = 4;
        int newSize = currentSpriteSize - (currentSpriteSize - size);
        graphics.setColor(outline);
        graphics.fillRect(globalX, globalY, currentSpriteSize, newSize);
        graphics.fillRect(globalX, globalY, newSize, currentSpriteSize);
        graphics.fillRect(globalX + currentSpriteSize - newSize, globalY, newSize, currentSpriteSize);
        graphics.fillRect(globalX, globalY + currentSpriteSize - newSize, currentSpriteSize, newSize);
        graphics.setColor(main);
        graphics.fillRect(globalX + size, globalY + size,
                currentSpriteSize - (size * 2), currentSpriteSize - (size * 2));
    }

    private void renderUiHelpers(Graphics graphics, GameModel model, Entity unit) {
        AbilityManager abilityManager = unit.get(AbilityManager.class);
        MovementManager movementManager = unit.get(MovementManager.class);

        boolean movementUiOpen = model.gameState.getBoolean(GameState.MOVEMENT_HUD_IS_SHOWING);
        boolean actionUiOpen = model.gameState.getBoolean(GameState.ACTION_HUD_IS_SHOWING);

        Entity entity = (Entity) model.gameState.getObject(GameState.CURRENTLY_SELECTED);
        Tile t = entity.get(Tile.class);
        boolean isCurrentTurnAndSelected = t.mUnit == model.speedQueue.peek();
        if (!actionUiOpen && (movementUiOpen || isCurrentTurnAndSelected)) {
            for (Entity tile : movementManager.range) {
                if (movementManager.path.contains(tile)) { continue; }
                renderPerforatedTile2(graphics, tile, ColorPalette.TRANSLUCENT_WHITE_V1, ColorPalette.BLACK);
                
            }
            if (unit.get(UserBehavior.class) != null) {
                for (Entity tile : movementManager.path) {
                    renderPerforatedTile2(graphics, tile, ColorPalette.TRANSLUCENT_WHITE_V1, ColorPalette.WHITE);
                }
            }
        } else if (actionUiOpen) {
            for (Entity tile : abilityManager.targets) {
                if (abilityManager.los.contains(tile)) { continue; }
                if (abilityManager.aoe.contains(tile)) { continue; }
                renderPerforatedTile2(graphics, tile, ColorPalette.TRANSLUCENT_BLACK_V3, ColorPalette.BLACK);
            }
            for (Entity tile : abilityManager.los) {
                if (abilityManager.aoe.contains(tile)) { continue; }
                renderPerforatedTile2(graphics, tile, ColorPalette.TRANSLUCENT_WHITE_V1, ColorPalette.WHITE);
            }
            for (Entity tile : abilityManager.aoe) {
                renderPerforatedTile2(graphics, tile, ColorPalette.TRANSLUCENT_RED_V1, ColorPalette.TRANSLUCENT_RED_V2);
            }
        }

        if (abilityManager.targeting != null) {
//            renderPerforatedTile2(graphics, manager.targeting, ColorPalette.BLACK, ColorPalette.BLACK);
        }
    }

    private void renderStructures(Graphics graphics, GameModel model, Queue<Entity> queue) {
        while(!queue.isEmpty()) {
            Entity entity = queue.poll();
            Tile tile = entity.get(Tile.class);
            int x = Camera.getInstance().globalX(entity);
            int y = Camera.getInstance().globalY(entity);
            Animation structure = AssetPool.getInstance().getAnimation(tile.getAsset(Tile.OBSTRUCTION));
            if (structure == null) { continue; }

            int width = structure.toImage().getWidth();
            int height = structure.toImage().getHeight();
            Vector centered = Vector.centerLimitOnY(Settings.getInstance().getSpriteSize(), x, y, width, height);
            x = (int) centered.x;
            y = (int) centered.y;

            graphics.drawImage(structure.toImage(), x, y, null);
            structure.update();
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

        if (model.gameState.getObject(GameState.CURRENTLY_SELECTED) != null) {
            Object object = model.gameState.getObject(GameState.CURRENTLY_SELECTED);
            if (object == null) { return; }
            Entity entity = (Entity) object;
            Tile tile = entity.get(Tile.class);
            // if (model.state.getBoolean(GameStateKey.UI_ACTION_PANEL_SHOWING)) {
            //     // Entity current = model.speedQueue.peek();
            //     // renderUiHelpers(graphics, model, tile.unit);
            // }
            if (tile.mUnit != null) { renderUiHelpers(graphics, model, tile.mUnit); }
        }

        while (!queue.isEmpty()) {
            Entity entity = queue.poll();
            Tile tile = entity.get(Tile.class);
            Entity unit = tile.getUnit();
            if (unit == null) { continue; } // TODO why is this null sometimes?
            Animation animation = unit.get(Animation.class);

            graphics.drawImage(
                    animation.toImage(),
                    Camera.getInstance().globalX(animation.getAnimatedX()),
                    Camera.getInstance().globalY(animation.getAnimatedY()),
                    null
            );

            Overlay ca = unit.get(Overlay.class);
            if (ca.hasOverlay()) {
                animation = ca.getAnimation();
                graphics.drawImage(
                        animation.toImage(),
                        Camera.getInstance().globalX(animation.getAnimatedX()),
                        Camera.getInstance().globalY(animation.getAnimatedY()),
                        null
                );
            }

            // nameplatesToDraw.add(unit);
        }
    }

    private void drawHealthBar(Graphics graphics, Entity unit) {
        // Check if we should render health or energy bar
        Summary summary = unit.get(Summary.class);
        ResourceNode mana = summary.getResourceNode(Summary.MANA);
        ResourceNode health = summary.getResourceNode(Summary.HEALTH);
//        ResourceNode stamina = summary.getResourceNode(Summary.STAMINA);
        if (health.getPercentage() == 1 && mana.getPercentage() == 1) { return; }
//        if (health.getPercentage() == 1 && energy.getPercentage() == 1 && stamina.getPercentage() == 1) { return; }

        Animation animation = unit.get(Animation.class);

        Vector vector = animation.getVector();

        int xPosition = (int) vector.x;
        int yPosition = (int) vector.y + currentSpriteSize;
        int newX = Camera.getInstance().globalX(xPosition);
        int newY = Camera.getInstance().globalY(yPosition);
        graphics.setColor(Color.WHITE);
        graphics.setFont(FontPool.getInstance().getFont(10));

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

        if (health.getPercentage() != 1 && health.getPercentage() != 0) {
            renderResourceBar(graphics, newX, newY - 6, currentSpriteSize, health.getPercentage(),
                    ColorPalette.BLACK, ColorPalette.RED, 8);
        }
        if (mana.getPercentage() != 1 || mana.getPercentage() != 0) {
//                stamina.getPercentage() != 1 || stamina.getPercentage() != 0) {
            renderResourceBar(graphics, newX, newY, (currentSpriteSize), mana.getPercentage(),
                    ColorPalette.BLACK, ColorPalette.BLUE, 8);
//            renderResourceBar(graphics, newX + (currentSpriteSize / 2), newY, (currentSpriteSize / 2), stamina.getPercentage(),
//                    ColorPalette.BLACK, ColorPalette.YELLOW, 8);
        }

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
