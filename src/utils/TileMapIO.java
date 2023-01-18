package utils;

public final class TileMapIO {

//    public static final String TILE_MAP_KEY = "TileMapKey";
//    public static final String PRETTY_MAP_KEY = "PrettyMapKey";
//
//    public static TileMap decode(String path) {
//        try {
//            return getTileMap(Files.readAllLines(Paths.get(path)), TILE_MAP_KEY);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static void encode(TileMap tileMap) {
//        try {
//            SimpleDateFormat formatter = new SimpleDateFormat("HH-mm");
//            Date date = new Date();
//            String fileName = LocalDate.now() + "-" + formatter.format(date) + ".tilemap";
//            PrintWriter out = new PrintWriter(new FileWriter(fileName, false), true);
//            out.write(TILE_MAP_KEY + ": " + System.lineSeparator());
//            out.write(encode(tileMap, false, false));
//            out.write(System.lineSeparator());
//            out.write(PRETTY_MAP_KEY + ": " + System.lineSeparator());
//            out.write(encode(tileMap, true, true));
//            out.write(System.lineSeparator());
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static String encode(TileMap tileMap, boolean pretty, boolean headers ) {
//        // Get the longest columns for correct spacing
//        String[][] tileMapEncoding = pretty ? get2DFloorMapEncoding(tileMap) : get2DEncoding(tileMap);
//
//        StringBuilder sb = new StringBuilder();
//        int longestColumnLength = getLongestColumnLength(tileMapEncoding);
//        String[] rowLabels = getRowLabels(tileMapEncoding);
//        if (headers) {
//            // Recalculate the longest column by rechecking the additional columns
//            String[] columnLabels = getColumnLabels(tileMapEncoding, 0, pretty);
//            int longestLengthOfAdditionalColumns = getLongestColumnLength(columnLabels);
//            if (longestLengthOfAdditionalColumns > longestColumnLength) {
//                longestColumnLength = longestLengthOfAdditionalColumns;
//            }
//            // potentially append header row
//            sb.append(rowLabels[0].replaceAll("[0-9]", " "))
//                    .append(getRowElongatedRow(columnLabels, longestColumnLength))
//                    .append(System.lineSeparator());
//        }
//        // Construct table
//        for (int row = 0; row < tileMapEncoding.length; row++) {
//            String rowLabel = rowLabels[row];
//            String representation = getRowElongatedRow(tileMapEncoding[row], longestColumnLength, true);
//            if (headers) { sb.append(rowLabel); }
//            sb.append(representation).append(System.lineSeparator());
//        }
//        return sb.toString();
//    }
//
//    private static TileMap getTileMap(List<String> lines, String key) {
//        // Get the line that has the encoded tile map, and decode it
//        Queue<String> values = getValuesForTileMap(lines, key);
//        List<List<Entity>> rows = new ArrayList<>();
//
//        while (values.size() > 0) {
//            String rawLine = values.poll();
//            // Get the start of the map encoding
//            String sanitizedLine = rawLine.substring(rawLine.indexOf("[") + 1, rawLine.lastIndexOf("]")).trim();
//            StringTokenizer tokenizer = new StringTokenizer(sanitizedLine, "][");
//            List<Entity> row = new ArrayList<>();
//            // Create a tile from each token
//            while (tokenizer.hasMoreTokens()) {
//                String token = tokenizer.nextToken().trim();
//                Entity tile = TileFactory.create(rows.size(), row.size());
//                Tile details = tile.get(Tile.class);
//                String[] tokens = token.split(" ");
//                details.encode(new int[]{
//                        Integer.parseInt(tokens[0]),
//                        Integer.parseInt(tokens[1]),
//                        Integer.parseInt(tokens[2]),
//                        Integer.parseInt(tokens[3]),
//                        Integer.parseInt(tokens[4])
//                });
//
//                row.add(tile);
//            }
//            // Continue to the next row
//            rows.add(row);
//        }
//
//        // Convert tile entities/tiles to schema map
//        SchemaMap pathMap = new SchemaMap(rows.size(), rows.get(0).size());
//        SchemaMap heightMap = new SchemaMap(rows.size(), rows.get(0).size());
//        SchemaMap terrainMap = new SchemaMap(rows.size(), rows.get(0).size());
//        SchemaMap specialMap = new SchemaMap(rows.size(), rows.get(0).size());
//        SchemaMap structureMap = new SchemaMap(rows.size(), rows.get(0).size());
//
//        Entity[][] map = new Entity[rows.size()][rows.get(0).size()];
//        for (int row = 0; row < map.length; row++) {
//            for (int column = 0; column < map[row].length; column++) {
////                map[row][column] = rows.get(row).get(column);
//                Entity entity = rows.get(row).get(column);
//                Tile details = entity.get(Tile.class);
//                pathMap.set(row, column, details.getPath());
//                heightMap.set(row, column, details.getHeight());
//                terrainMap.set(row, column, details.getTerrain());
//                specialMap.set(row, column, details.getLiquid());
//                structureMap.set(row, column, details.getStructure());
//            }
//        }
//
//        return TileMapGenerator.createTileMap(pathMap, heightMap, terrainMap, specialMap, structureMap);
//    }
//
//    private static Queue<String> getValuesForTileMap(List<String> lines, String key) {
//        // Get only the values from the start of the key, to the next open line of white space
//        int index = getIndexOfKey(lines, key) + 1;
//        Queue<String> values = new LinkedList<>();
//        while (lines.get(index).length() > 1) {
//            values.add(lines.get(index));
//            index++;
//        }
//        return values;
//    }
//
//    private static int getIndexOfKey(List<String> lines, String key) {
//        for (int index = 0; index < lines.size(); index++) {
//            boolean contains = lines.get(index).contains(key);
//            if (!contains) { continue; }
//            return index;
//        }
//        return -1;
//    }
//
//    private static String[][] get2DEncoding(TileMap tileMap) {
//        String[][] encoding = new String[tileMap.data.length][tileMap.data[0].length];
//        for (int row = 0; row < tileMap.data.length; row++) {
//            for (int column = 0; column < tileMap.data[row].length; column++) {
//                encoding[row][column] = tileMap.data[row][column].getEncoding();
//            }
//        }
//        return encoding;
//    }
//
//    private static String[][] get2DFloorMapEncoding(TileMap tileMap) {
//        String[][] encoding = new String[tileMap.data.length][tileMap.data[0].length];
//        for (int row = 0; row < tileMap.data.length; row++) {
//            for (int column = 0; column < tileMap.data[row].length; column++) {
//                encoding[row][column] = tileMap.data[row][column].isWall() ? "X" : " ";
//            }
//        }
//        return encoding;
//    }
//
//    private static String[] getRowLabels(String[][] table) {
//        int mostAmountOfDigits = String.valueOf(table.length).length();
//        String[] rowLabels = new String[table.length];
//        for (int row = 0; row < table.length; row++) {
//            String digitsAsString = String.valueOf(row);
//            rowLabels[row] = " ".repeat(mostAmountOfDigits - digitsAsString.length()) + digitsAsString + " ";
//        }
//        return rowLabels;
//    }
//
//    private static String[] getColumnLabels(String[][] tileMapEncoding, int row, boolean pretty) {
//        String[] columnLabels = new String[tileMapEncoding[row].length];
//        for (int column = 0; column < tileMapEncoding[row].length; column++) {
//            columnLabels[column] = String.valueOf(pretty ? column % 10 : column);
//        }
//        return columnLabels;
//    }
//
//    private static String getRowElongatedRow(String[] row, int longestColumnLength) {
//        return getRowElongatedRow(row, longestColumnLength, false);
//    }
//
//    private static String getRowElongatedRow(String[] row, int longestColumnLength, boolean withSquareBrackets) {
//        StringBuilder sb = new StringBuilder();
//        for (String str : row) {
//
//            if (withSquareBrackets) {
//                sb.append("[");
//            } else {
//                sb.append(" ");
//            }
//
//            sb.append(" ".repeat(longestColumnLength - str.length())).append(str);
//
//            if (withSquareBrackets) {
//                sb.append("]");
//            } else {
//                sb.append(" ");
//            }
//
//        }
//        return sb.toString();
//    }
//
//    private static int getLongestColumnLength(String[][] rows) {
//        int max = 0;
//        for (String[] row : rows) {
//            int longest = getLongestColumnLength(row);
//            if (longest <= max) { continue; }
//            max = longest;
//        }
//        return max;
//    }
//    private static int getLongestColumnLength(String[] row) {
//        int max = 0;
//        for (String str : row) {
//            if (str == null || str.length() <= max) { continue; }
//            max = str.length();
//        }
//        return max;
//    }

}