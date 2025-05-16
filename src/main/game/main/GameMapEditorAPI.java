package main.game.main;

import main.game.components.tile.StructureComponent;
import main.game.components.tile.TileComponent;
import main.game.entity.Entity;
import main.game.stores.EntityStore;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

public class GameMapEditorAPI {
    private final GameModel mGameModel;
    public GameMapEditorAPI(GameModel gameModel) { mGameModel = gameModel; }

    public void setMapEditorHoveredTilesCursorSizeAPI(GameModel gameModel, JSONObject request) {
        int cursorSize = request.getIntValue("cursor_size");
        GameState gameState = gameModel.getGameState();
        gameState.setMapEditorCursorSize(cursorSize);
    }

    public JSONObject getHoveredTile() {
        String hoveredTileID = mGameModel.getHoveredTileID();
        if (hoveredTileID == null) { return null; }

        Entity entity = getEntityWithID(hoveredTileID);
        TileComponent tile = entity.get(TileComponent.class);

        String structureID = tile.getStructureID();
        entity = getEntityWithID(structureID);
        JSONObject structure = null;
        if (entity != null) {
            StructureComponent structureComponent = entity.get(StructureComponent.class);
            String name = structureComponent.getName();

            structure = new JSONObject();
            structure.put("asset", name);
        }

        JSONObject hoveredTile = new JSONObject();
        hoveredTile.put("row", tile.getRow());
        hoveredTile.put("column", tile.getColumn());
        hoveredTile.put("base_elevation", tile.getBaseElevation());
        hoveredTile.put("modified_elevation", tile.getModifiedElevation());
        hoveredTile.put("layers", tile.getLayers());
        hoveredTile.put("structure", structure);


        return hoveredTile;
    }

    public void addLayersToHoveredTileIDs(String asset, String state, String depth) {
//        JSONArray hoveredTileIDs = mGameModel.getHoveredTileIDs();
//        for (int i = 0; i < hoveredTileIDs.size(); i++) {
//            String hoveredTileID = hoveredTileIDs.getString(i);
//            Entity entity = getEntityWithID(hoveredTileID);
//            TileComponent tileComponent = entity.get(TileComponent.class);
//            tileComponent.addLayer(asset, state, Integer.parseInt(depth));
//        }
    }

    public void removeLayersOfHoveredTileIDs(String depth) {
//        JSONArray hoveredTileIDs = mGameModel.getHoveredTileIDs();
//        for (int i = 0; i < hoveredTileIDs.size(); i++) {
//            String hoveredTileID = hoveredTileIDs.getString(i);
//            Entity entity = getEntityWithID(hoveredTileID);
//            TileComponent tileComponent = entity.get(TileComponent.class);
//            tileComponent.removeLayer(Integer.parseInt(depth));
//        }
    }

    private Entity getEntityWithID(String id) { return EntityStore.getInstance().get(id); }
}
