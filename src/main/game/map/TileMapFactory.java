package main.game.map;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import main.game.components.Tile;
import main.game.entity.Entity;
import main.game.map.builders.*;
import main.game.map.builders.utils.TileMapLayer;
import main.game.stores.factories.TileFactory;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class TileMapFactory {

    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(TileFactory.class);

//    public static TileMap create(TileMapBuilderProtoType configs) {
//
//        TileMapBuilder generator;
//
//        switch (configs.getType()) {
//            case 0 -> generator = new HauberkDungeonMap();
//            case 1 -> generator = new LargeContinousRoom();
//            case 2 -> generator = new NoBorderWithSmallRooms();
//            case 3 -> generator = new BorderedMapWithBorderedRooms();
//            case 4 -> generator = new LargeBorderedRoom();
//            default -> generator = new BasicOpenMap();
//        }
//
//        logger.info("Constructing {} style map", generator.getClass().getSimpleName());
//        return generator.build(configs);
//    }

    public static void save(TileMap map) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("HH-mm");
            String fileName = LocalDate.now() + "-" + formatter.format(new Date()) + ".json";
            PrintWriter out = new PrintWriter(new FileWriter(fileName, false), true);
            out.write(map.toJson().toJson());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static TileMap load(String path) {
        ELogger logger = ELoggerFactory.getInstance().getELogger(TileMapBuilder.class);
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            JsonObject jsonObject = (JsonObject) Jsoner.deserialize(reader);
            JsonArray tilemap = (JsonArray) jsonObject.get("data");

            JsonArray jsonArrayRow = (JsonArray) tilemap.get(0);
            TileMapLayer pathMap = new TileMapLayer(tilemap.size(), jsonArrayRow.size());
            TileMapLayer heightMap = new TileMapLayer(tilemap.size(), jsonArrayRow.size());
            TileMapLayer terrainMap = new TileMapLayer(tilemap.size(), jsonArrayRow.size());
            TileMapLayer liquidMap = new TileMapLayer(tilemap.size(), jsonArrayRow.size());
            TileMapLayer structureMap = new TileMapLayer(tilemap.size(), jsonArrayRow.size());

            Entity[][] raw = new Entity[tilemap.size()][];
            for (int row = 0; row < tilemap.size(); row++) {
                jsonArrayRow = (JsonArray) tilemap.get(row);
                raw[row] = new Entity[jsonArrayRow.size()];
                for (int column = 0; column < jsonArrayRow.size(); column++) {
                    JsonArray tileJson = (JsonArray) jsonArrayRow.get(column);
                    Entity entity = TileFactory.create(row, column);
                    raw[row][column] = entity;
                    Tile tile = entity.get(Tile.class);
                    int[] encoding = new int[tileJson.size()];
                    for (int i = 0; i < encoding.length; i++) {encoding[i] = tileJson.getInteger(i); }
                    tile.encode(encoding);
                    pathMap.set(row, column, tile.getPath());
                    heightMap.set(row, column, tile.getHeight());
                    terrainMap.set(row, column, tile.getTerrain());
                    liquidMap.set(row, column, tile.getLiquid());
                    structureMap.set(row, column, tile.getStructure());
                }
            }
            logger.info("Finished deserializing tilemap");
            return null;
//            return TileMapBuilder.createTileMap(pathMap, heightMap, terrainMap, liquidMap, structureMap);
        } catch (Exception ex) {
            logger.info("Unable to deserialize Json for tilemap");
        }
        return null;
    }
}
