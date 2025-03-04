package main.game.main.rendering;

import main.game.components.behaviors.Behavior;
import main.game.components.tile.Tile;
import main.game.entity.Entity;
import main.game.main.GameModel;
import main.game.stores.factories.EntityStore;
import main.game.systems.texts.FloatingText;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private RenderContext() { }
    public static RenderContext create(GameModel model) {
        RenderContext context = new RenderContext();

        int startColumn = (int) Math.max(0, model.getVisibleStartOfColumns());
        int startRow = (int) Math.max(0, model.getVisibleStartOfRows());
        int endColumn = (int) Math.min(model.getColumns(), model.getVisibleEndOfColumns() + 1);
        int endRow = (int) Math.min(model.getRows(), model.getVisibleEndOfRows() + 1);

        for (int row = startRow; row < endRow; row++) {
            for (int column = startColumn; column < endColumn; column++) {
                // Builds the rendering context

                Entity tileEntity = model.tryFetchingEntityAt(row, column);
                Tile tile = tileEntity.get(Tile.class);
                context.mVisibleTiles.add(tileEntity);

                String unitEntityID = tile.getUnitID();
                Entity unitEntity = EntityStore.getInstance().get(unitEntityID);
                if (unitEntity != null && unitEntity.get(Behavior.class) != null && unitEntity.get(Behavior.class).isUserControlled()) {
//                    System.out.println("ON " + tileEntity);
                }



                if (unitEntity != null) {
                    context.mVisibleUnits.add(unitEntity);
                    context.mTilesWithUnits.add(tileEntity);
                }

                String structureEntityID = tile.getStructureID();
                Entity structureEntity = EntityStore.getInstance().get(structureEntityID);
                if (structureEntity != null) {
                    context.mTilesWithStructures.add(tileEntity);
                }

//                Entity unitEntity = tile.getUnit();
//                if (unitEntity != null) {
//                    context.mVisibleUnits.add(unitEntity);
//                    context.mTilesWithUnits.add(tileEntity);
//                }


//                if (tile.getStructure() != null) {
//                    context.mTilesWithStructures.add(tileEntity);
//                }
            }
        }

        // Get Selected tiles
        JSONArray selectedTiles = model.getGameState().getSelectedTiles();
        for (int index = 0; index < selectedTiles.length(); index++) {
            String selectedTile = selectedTiles.getString(index);
            Entity entity = EntityStore.getInstance().get(selectedTile);
//            Tile tile = entity.get(Tile.class);
//            Entity entity = model.tryFetchingEntityAt(tile.getRow(), tile.getColumn());
            context.mSelectedTiles.add(entity);
        }

        // Get Selected tiles
        JSONArray hoveredTiles = model.getGameState().getHoveredTiles();
        for (int index = 0; index < hoveredTiles.length(); index++) {
            String hoveredTile = hoveredTiles.getString(index);
            Entity entity = EntityStore.getInstance().get(hoveredTile);
//            Tile tile = entity.get(Tile.class);
//            if (tile == null) { continue; }
//            Entity entity = model.tryFetchingEntityAt(tile.getRow(), tile.getColumn());
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

    public List<Entity> getAllVisibleTiles() { return mVisibleTiles; }
    public List<Entity> getAllVisibleUnits() { return mVisibleUnits; }
    public List<Entity> getTilesWithUnits() { return mTilesWithUnits; }
    public List<Entity> getTilesWithStructures() { return mTilesWithStructures; }

    public List<Entity> getHoveredTiles() { return mHoveredTiles; }
    public List<Entity> getSelectedTiles() { return mSelectedTiles; }
    public List<FloatingText> getFloatingText() { return mFloatingText; }
}
