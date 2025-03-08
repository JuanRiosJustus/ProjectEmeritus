package main.game.main.rendering;

import main.game.components.behaviors.Behavior;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameController;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import main.game.systems.texts.FloatingText;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderContext {
    private final List<Entity> mVisibleTiles = new ArrayList<>();
    private final List<Entity> mVisibleUnits = new ArrayList<>();
    private final List<Entity> mTilesWithUnits = new ArrayList<>();
    private final List<Entity> mTilesWithStructures = new ArrayList<>();
    private final List<Entity> mSelectedTiles = new ArrayList<>();
    private final List<Entity> mHoveredTiles = new ArrayList<>();
    private final List<FloatingText> mFloatingText = new ArrayList<>();
    private GameController mGameController = null;
    private String mCamera = null;

    private static final Map<String, RenderContext> mContextMap = new HashMap<>();
    private RenderContext(GameController gc, String camera) { mGameController = gc; mCamera = camera; }

    public static RenderContext create(GameController gc, String camera) {
        RenderContext context = new RenderContext(gc, camera);

        GameModel model = context.getGameModel();
        int startColumn = (int) Math.max(0, model.getVisibleStartOfColumns(camera));
        int startRow = (int) Math.max(0, model.getVisibleStartOfRows(camera));
        int endColumn = (int) Math.min(model.getColumns(), model.getVisibleEndOfColumns(camera) + 1);
        int endRow = (int) Math.min(model.getRows(), model.getVisibleEndOfRows(camera) + 1);

        for (int row = startRow; row < endRow; row++) {
            for (int column = startColumn; column < endColumn; column++) {
                // Builds the rendering context

                Entity tileEntity = model.tryFetchingEntityAt(row, column);
                Tile tile = tileEntity.get(Tile.class);
                context.mVisibleTiles.add(tileEntity);

                String unitEntityID = tile.getUnitID();
                Entity unitEntity = EntityStore.getInstance().get(unitEntityID);


                if (unitEntity != null) {
                    context.mVisibleUnits.add(unitEntity);
                    context.mTilesWithUnits.add(tileEntity);
                }

                String structureEntityID = tile.getStructureID();
                Entity structureEntity = EntityStore.getInstance().get(structureEntityID);
                if (structureEntity != null) {
                    context.mTilesWithStructures.add(tileEntity);
                }
            }
        }

        // Get Selected tiles
        JSONArray selectedTiles = model.getGameState().getSelectedTiles();
        for (int index = 0; index < selectedTiles.length(); index++) {
            String selectedTile = selectedTiles.getString(index);
            Entity entity = EntityStore.getInstance().get(selectedTile);
            context.mSelectedTiles.add(entity);
        }

        // Get Selected tiles
        JSONArray hoveredTiles = model.getGameState().getHoveredTiles();
        for (int index = 0; index < hoveredTiles.length(); index++) {
            String hoveredTile = hoveredTiles.getString(index);
            Entity entity = EntityStore.getInstance().get(hoveredTile);
            context.mHoveredTiles.add(entity);
        }

        // Get Floating texts
        Map<String, JSONObject> floatingTexts = model.getGameState().getFloatingTexts();
        for (String key : floatingTexts.keySet()) {
            JSONObject jsonObject = floatingTexts.get(key);
            FloatingText floatingText = (FloatingText) jsonObject;
            context.mFloatingText.add(floatingText);
        }

        return context;
    }

    public List<Entity> getAllVisibleTiles() {
        return mVisibleTiles;
    }

    public List<Entity> getAllVisibleUnits() {
        return mVisibleUnits;
    }

    public List<Entity> getTilesWithUnits() {
        return mTilesWithUnits;
    }

    public List<Entity> getTilesWithStructures() {
        return mTilesWithStructures;
    }

    public List<Entity> getHoveredTiles() {
        return mHoveredTiles;
    }

    public List<Entity> getSelectedTiles() {
        return mSelectedTiles;
    }

    public List<FloatingText> getFloatingText() {
        return mFloatingText;
    }

    public String getCamera() {
        return mCamera;
    }

    public GameModel getGameModel() {
        return mGameController.getGameModel();
    }
}
