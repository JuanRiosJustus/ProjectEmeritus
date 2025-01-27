package main.game.main;

import java.awt.*;
import java.awt.Dimension;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.*;

import main.constants.*;
import main.game.main.rendering.*;
import main.game.stores.pools.ColorPalette;
import main.game.components.statistics.StatisticsComponent;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.graphics.GameUI;
import main.ui.panels.HealthBarDrawer;

public class GamePanel extends GameUI {

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
    private final Queue<Entity> tilesWithExits = new LinkedList<>();
    private final Queue<Entity> tilesWithOverlayAnimations = new LinkedList<>();
    private final Queue<Entity> unitsToRenderNameplatesFor = new LinkedList<>();
    private final Queue<Pair<String, int[]>> mUnitDirectionTags = new LinkedList<>();
    private final GameModel mGameModel;
    boolean isLoadoutMode = false;
    private final BasicStroke mOutlineStroke = new BasicStroke(5f);
    private final HealthBarDrawer mHealthBarDrawer = new HealthBarDrawer();
    private final Renderer mTileRenderer = new TileRenderer();
    private final Renderer mUnitRenderer = new UnitRenderer();
    private final Renderer mStructureRenderer = new StructureRenderer();
    private final Renderer mSelectedAndHoveredTileRenderer = new SelectedAndHoveredTileRenderer();
    private final Renderer mActionAndMovementPathingRenderer = new ActionAndMovementPathingRenderer();
    private final Renderer mFloatingTextRenderer = new FloatingTextRenderer();
    private final Renderer mHealthBarRenderer = new HealthBarRenderer();
    private final Point mEphemeralPoint = new Point();

    public GamePanel(GameModel gameModel, int width, int height) {
        super(width, height);
        setPreferredSize(new Dimension(width, height));
        mGameModel = gameModel;
    }


    @Override
    public void gameUpdate(GameModel model) { revalidate(); repaint(); }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!mGameModel.isRunning()) { return; }

        render(mGameModel, g);
    }

    public void render(GameModel model, Graphics g) {

        isLoadoutMode = model.isLoadOutMode();

        renderBackground(g, model);

//        collectAndQueueTileData(g, model);
//        collectAndQueueTileDataV2(g, model);

        RenderContext renderContext = RenderContext.create(model);

        mTileRenderer.render(g, model, renderContext);
        mActionAndMovementPathingRenderer.render(g, model, renderContext);
        mUnitRenderer.render(g, model, renderContext);
        mStructureRenderer.render(g, model, renderContext);
        mSelectedAndHoveredTileRenderer.render(g, model, renderContext);
        mFloatingTextRenderer.render(g, model, renderContext);
        mHealthBarRenderer.render(g, model, renderContext);




        renderOverlayAnimations(g, model, tilesWithOverlayAnimations);


//        renderNamePlates(g, tilesWithEntitiesWithNameplates);
        renderExits(g, model, tilesWithExits);
//        renderCurrentlySelectedTileV2(g, model);
//        renderCurrentlySelectedTile(g, model);
//        renderCurrentlyMousedTile(g, model);
        renderUnitDirectionTags(g, mUnitDirectionTags);
        mHealthBarDrawer.renderHealthBars(g, model);


//        createBlurredBackground(model);
//        setBackgroundWallpaper(model);
//        setBackground(Color.BLUE);
    }

    private BufferedImage mBlurredImage;
    private void renderBackground(Graphics g, GameModel model) {
        if (mBlurredImage != null) {
            g.drawImage(mBlurredImage, 0, 0, null);
        } else {
            // Poll for background wallpaper
            mBlurredImage = model.getBackgroundWallpaper();
        }
    }

    private void renderUnitDirectionTags(Graphics g, Queue<Pair<String,int[]>> mUnitDirectionTags) {
        while (!mUnitDirectionTags.isEmpty()) {
            Pair<String, int[]> directionalTag = mUnitDirectionTags.poll();
            String str = directionalTag.first;
            int x = directionalTag.second[0];
            int y = directionalTag.second[1];
            drawStrokedText((Graphics2D) g, x, y, str);
        }
    }

    private void renderTextWithOutline(Graphics2D g, int x, int y, String str, Color fg, Color bg) {
        Color originalColor = g.getColor();
        Stroke originalStroke = g.getStroke();
        RenderingHints originalHints = g.getRenderingHints();
        AffineTransform originalTransform = g.getTransform();

        // create a glyph vector from your text, then get the shape object
        GlyphVector glyphVector = g.getFont().createGlyphVector(g.getFontRenderContext(), str);
        Shape textShape = glyphVector.getOutline();

        g.setColor(bg);
        g.setStroke(mOutlineStroke);
        g.translate(x, y);
        g.draw(textShape); // draw outline

        g.setColor(fg);
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

    private void renderExits(Graphics g, GameModel model, Queue<Entity> queue) {
        while (!queue.isEmpty()) {
            Entity entity = queue.poll();
            Tile tile = entity.get(Tile.class);
//            int tileX = Camera.getInstance().globalX(entity);
//            int tileY = Camera.getInstance().globalY(entity);
            g.setColor(ColorPalette.TRANSLUCENT_BLACK_LEVEL_1);
//            g.fillRect(tileX, tileY, currentSpriteSize, currentSpriteSize);
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

    private void renderOverlayAnimations(Graphics g, GameModel model, Queue<Entity> queue) {
        while(!queue.isEmpty()) {
            Entity entity = queue.poll();
//            int tileX = Camera.getInstance().globalX(entity);
//            int tileY = Camera.getInstance().globalY(entity);
//            Overlay ca = entity.get(Overlay.class);
//            if (ca.hasOverlay()) {
//                BufferedImage image = ca.getAnimation().toImage();
//                g.drawImage(image, tileX, tileY,  null);
//            }
        }
    }

    private final Map<String, Color> regionMap = new HashMap<>();

    private void renderTileRangeWithOutline(Graphics g, GameModel model, Entity entity, Color bg, Color fg, Set<Entity> set) {
        Tile tile = entity.get(Tile.class);
        int spriteWidth = model.getGameState().getSpriteWidth();
        int spriteHeight = model.getGameState().getSpriteHeight();
        Vector3f coordinate = tile.getWorldVector(model);

        int tileX = (int) coordinate.x;
        int tileY = (int) coordinate.y;
        float multiplier = 0.05f;
        int spriteSubWidth = (int) (spriteWidth * multiplier);
        int spriteSubHeight = (int) (spriteHeight * multiplier);

        // Fill the entire tile
        g.setColor(bg);
        g.fillRect(tileX, tileY, spriteWidth, spriteHeight);

        // Handle tile edges
        g.setColor(fg);
        for (Direction direction : Direction.cardinal) {
            Entity adjacent = model.tryFetchingEntityAt(tile.getRow() + direction.y, tile.getColumn() + direction.x);
            if (adjacent == null || set.contains(adjacent)) continue;

            int x = tileX + (direction == Direction.East ? spriteWidth - spriteSubWidth : 0);
            int y = tileY + (direction == Direction.South ? spriteHeight - spriteSubHeight : 0);
            int width = (direction == Direction.East || direction == Direction.West) ? spriteSubWidth : spriteWidth;
            int height = (direction == Direction.North || direction == Direction.South) ? spriteSubHeight : spriteHeight;

            g.fillRect(x, y, width, height);
        }
    }

    private void renderTile(Graphics graphics, GameModel model, Entity tileEntity, Color inside, Color outside) {
        Tile tile = tileEntity.get(Tile.class);
        int spriteWidth = model.getGameState().getSpriteWidth();
        int spriteHeight = model.getGameState().getSpriteHeight();
        Vector3f coordinate = tile.getWorldVector(model);

        int tileX = (int) coordinate.x;
        int tileY = (int) coordinate.y;
        float multiplier = .1f;
        int spriteSubWidth = (int) (spriteWidth * multiplier);
        int spriteSubHeight = (int) (spriteHeight * multiplier);

        graphics.setColor(inside);
        // entire tile
//        graphics.fillRect(tileX, tileY, spriteWidth, spriteHeight);
        // center tile
        graphics.fillRect(
                tileX + spriteSubWidth,
                tileY + spriteSubHeight,
                spriteWidth - (spriteSubWidth * 2),
                spriteHeight - (spriteSubHeight * 2)
        );
        graphics.setColor(outside);
        // left bar
        graphics.fillRect(tileX, tileY, spriteSubWidth, spriteHeight);
        // top bar
        graphics.fillRect(tileX, tileY, spriteWidth, spriteSubHeight);
        // right bar
        graphics.fillRect(tileX + spriteWidth - spriteSubWidth, tileY, spriteSubWidth, spriteHeight);
        // bottom bar
        graphics.fillRect(tileX, tileY + spriteHeight - spriteSubHeight, spriteWidth, spriteSubHeight);
    }

    private Color arrowColor = Color.BLACK; // Color of the arrow
    private int startX = 50; // Starting X position
    private int startY = 50; // Starting Y position
    private int endX = 150; // Ending X position
    private int endY = 150; // Ending Y position
    private int arrowHeadSize = 10; // Size of the arrowhead


    private void drawHealthBar(Graphics graphics, Entity unit) {
        // Check if we should render health or energy bar
        StatisticsComponent statisticsComponent = unit.get(StatisticsComponent.class);
//        ResourceNode mana = statisticsComponent.getResourceNode(StatisticsComponent.MANA);
//        ResourceNode health = statisticsComponent.getResourceNode(StatisticsComponent.HEALTH);
//        ResourceNode stamina = summary.getResourceNode(Summary.STAMINA);
//        if (health.getPercentage() == 1 && mana.getPercentage() == 1) { return; }
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

//    public static void renderResourceBar(Graphics graphics, int x, int y, int size,
//        float amt, Color bg, Color fg, int height) {
//        graphics.setColor(bg);
//        graphics.fillRoundRect(x, y, size, height, 2, 2);
//        float barWidth = MathUtils.map(amt, 0, 1, 0, size - 4);
//        graphics.setColor(fg);
//        graphics.fillRoundRect(x + 2, y + 2, (int) barWidth, height / 2, 2, 2);
//    }
}
