package game.map;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import game.components.Tile;
import game.entity.Entity;
import game.map.generators.*;
import game.map.generators.validation.SchemaConfigs;
import game.map.generators.validation.SchemaMap;
import game.stores.factories.TileFactory;
import logging.ELogger;
import logging.ELoggerFactory;

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

    public static TileMap create(SchemaConfigs configs) {

        TileMapGenerator generator;

        switch (configs.type) {
            case 0 -> generator = new HauberkDungeonGenerator();
            case 1 -> generator = new IndoorSquareRoomsGenerator();
            case 2 -> generator = new OutdoorSquareRoomsGenerator();
            case 3 -> generator = new BorderedMapWithBorderedRoomsGenerator();
            case 4 -> generator = new OpenMapWithBorderGenerator();
            default -> generator = new OpenMapGenerator();
        }

        logger.info("Constructing {} style map", generator.getClass().getSimpleName());
        return generator.build(configs);
    }

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
        ELogger logger = ELoggerFactory.getInstance().getELogger(TileMapGenerator.class);
        try {
            Reader reader = Files.newBufferedReader(Paths.get(path));
            JsonObject jsonObject = (JsonObject) Jsoner.deserialize(reader);
            JsonArray tilemap = (JsonArray) jsonObject.get("data");

            JsonArray jsonArrayRow = (JsonArray) tilemap.get(0);
            SchemaMap pathMap = new SchemaMap(tilemap.size(), jsonArrayRow.size());
            SchemaMap heightMap = new SchemaMap(tilemap.size(), jsonArrayRow.size());
            SchemaMap terrainMap = new SchemaMap(tilemap.size(), jsonArrayRow.size());
            SchemaMap liquidMap = new SchemaMap(tilemap.size(), jsonArrayRow.size());
            SchemaMap structureMap = new SchemaMap(tilemap.size(), jsonArrayRow.size());

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
            return TileMapGenerator.createTileMap(pathMap, heightMap, terrainMap, liquidMap, structureMap);
        } catch (Exception ex) {
            logger.info("Unable to deserialize Json for tilemap");
        }
        return null;
    }
}
