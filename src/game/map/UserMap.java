package game.map;

import game.entity.Entity;
import game.stores.factories.TileFactory;
import logging.Logger;
import logging.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UserMap extends MapBuilder {

    private final static Logger logger = LoggerFactory.instance().logger(UserMap.class);

    public static Entity[][] read(String path) {
        Entity[][] tiles = null;
        try {
            logger.log("Reading map from " + path);
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);

            List<String> lines = Files.readAllLines(Path.of(path));
            List<Entity[]> map = new ArrayList<>();

            for (int row = 0; row < lines.size(); row++) {
                String line = lines.get(row);
                Entity[] decoded = decodeLine(line, row);
                map.add(decoded);
            }

            br.close();
            tiles = map.toArray(new Entity[0][]);
        } catch (Exception ex) {
            // to do something? idk
        }
        return tiles;
    }

    private static Entity[] decodeLine(String line, int row) {
        String[] cells = line.split(",");
        List<Entity> entityList = new ArrayList<>();
        for (String cell : cells) {
            // sanitize
            cell = cell.strip();

            // determine if floor tile
            Entity entity = TileFactory.create(row, entityList.size());

//            MapBuilder.decode(entity, cell);

            entityList.add(entity);
        }
        return entityList.toArray(new Entity[0]);
    }

    @Override
    public TileMap build(int rows, int columns) {
        return null;
    }
}
