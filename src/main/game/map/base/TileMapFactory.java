package main.game.map.base;

import main.constants.Constants;
import main.game.map.builders.*;
import com.alibaba.fastjson2.JSONObject;

public class TileMapFactory {
    public static final String BORDERED_MAP_WITH_BORDERED_ROOMS = "BORDERED_MAP_WITH_BORDERED_ROOMS";
    public static final String BORDERED_MAP_WITH_NO_ROOMS = "BORDERED_MAP_WITH_NO_ROOMS";
    public static final String LARGE_CONTINUOUS_ROOM = "LARGE_CONTINUOUS_ROOM";
    public static final String BASIC_OPEN_ROOM = "BASIC_OPEN_ROOM";
    public static final String LARGER_BORDERED_ROOM = "LARGER_BORDERED_ROOM";
    public static final String NO_BORDER_WITH_BORDERED_ROOMS = "NO_BORDER_WITH_BORDERED_ROOMS";

    public static TileMap create(int rows, int columns) {
        String spriteMap = Constants.TILES_SPRITEMAP_FILEPATH;
        TileMapParameters tileMapParameters = TileMapParameters.getDefaultParameters(rows, columns, spriteMap);
        return create(tileMapParameters);
    }

    public static TileMap create(TileMapParameters tileMapParameters) { return create(tileMapParameters, ""); }
    public static TileMap create(TileMapParameters tileMapParameters, String algorithm) {
//        return new TileMap(10, 10);
        return new TileMap(new JSONObject());

//        TileMapAlgorithm tileMapAlgorithm = null;
//        switch (algorithm) {
//            case BORDERED_MAP_WITH_NO_ROOMS -> tileMapAlgorithm = new BorderedMapWithNoRooms();
//            case BORDERED_MAP_WITH_BORDERED_ROOMS -> tileMapAlgorithm = new BorderedMapWithBorderedRooms();
//            case LARGE_CONTINUOUS_ROOM -> tileMapAlgorithm = new LargeContinuousRoom();
//            case LARGER_BORDERED_ROOM -> tileMapAlgorithm = new LargeBorderedRoom();
//            case NO_BORDER_WITH_BORDERED_ROOMS -> tileMapAlgorithm = new NoBorderWithBorderedRooms();
//            case BASIC_OPEN_ROOM ->  tileMapAlgorithm = new BasicOpenMap();
//            default -> tileMapAlgorithm = new BasicOpenMap();
//        }
//
//        return tileMapAlgorithm.evaluate(tileMapParameters);
    }
}

