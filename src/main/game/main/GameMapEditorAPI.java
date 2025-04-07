package main.game.main;

import main.game.components.TileComponent;
import main.game.entity.Entity;
import main.game.stores.EntityStore;
import org.json.JSONArray;
import org.json.JSONObject;

public class GameMapEditorAPI {
    public void setHoveredTilesCursorSizeAPI(GameModel gameModel, JSONObject request) {
        int cursorSize = request.getInt("cursor_size");
        GameState gameState = gameModel.getGameState();
        gameState.setHoveredTilesCursorSize(cursorSize);
    }

    public JSONArray getHoveredTileDetailsFromGameMapEditorAPI(GameModel mGameModel) {
        JSONArray response = new JSONArray();
        GameState gameState = mGameModel.getGameState();
        JSONArray hoveredTileIDs = gameState.getHoveredTileIDs();
        for (int i = 0; i < hoveredTileIDs.length(); i++) {
            String id = hoveredTileIDs.getString(i);
            Entity tileEntity = EntityStore.getInstance().get(id);
            TileComponent tile = tileEntity.get(TileComponent.class);

            JSONObject details = new JSONObject();
            details.put("tile_modified_elevation", tile.getModifiedElevation() + "");
            details.put("tile_base_elevation", tile.getBaseElevation() + "");
            details.put("tile_total_elevation", tile.getTotalElevation() + "");
            details.put("tile_row", tile.getRow() + "");
            details.put("tile_column", tile.getColumn() + "");
            details.put("tile_layer_count", tile.getLayerCount() + "");
            details.put("top_layer_asset", tile.getTopLayerAsset());
            details.put("top_layer_depth", tile.getTopLayerDepth() + "");
            details.put("top_layer_state", tile.getTopLayerState());

            response.put(details);
        }
        return response;
    }
}
