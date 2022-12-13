package game.map;

public class MapWriter extends MapBuilder {

    @Override
    public TileMap build(int rows, int columns) {
        return null;
    }

//    private static final SplittableRandom random = new SplittableRandom();
//    private static final ELogger logger = ELoggerFactory.get().logger(getClass());
//
//    public static Entity[][] empty(int rows, int columns) {
//        logger.banner("Generating empty map");
//        List<Entity[]> map = new ArrayList<>();
//        for (int row = 0; row < rows; row++) {
//            List<Entity> tileRow = new ArrayList<>();
//            for (int column = 0; column < columns; column++) {
//                Entity entity = EntityBuilder.get().tile(row, column);
//                tileRow.add(entity);
//            }
//            map.add(tileRow.toArray(new Entity[0]));
//        }
//        return map.toArray(new Entity[0][]);
//    }
//
//    public static Entity[][] random(int rows, int columns) {
//        ELoggerManager.get().banner(MapWriter.class, "Generating random map");
//        List<Entity[]> map = new ArrayList<>();
//        int floor = random.nextInt(AssetPool.get().tileSprites());
//        int structure = random.nextInt(AssetPool.get().structureSprites());
//        ELoggerManager.get().log("Floor type: " + floor);
//        ELoggerManager.get().log("Structure type: " + structure);
//        for (int row = 0; row < rows; row++) {
//            List<Entity> tileRow = new ArrayList<>();
//            for (int column = 0; column < columns; column++) {
//                boolean placeStructure = random.nextFloat() < .1;
//                String tileData = floor + " " + (placeStructure ? structure : "0");
//                Entity entity = EntityBuilder.get().tile(row, column);
//                tileRow.add(entity);
//            }
//            map.add(tileRow.toArray(new Entity[0]));
//        }
//        return map.toArray(new Entity[0][]);
//    }
}
