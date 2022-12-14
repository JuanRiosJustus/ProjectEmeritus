package game.systems;

import game.entity.Entity;
import logging.Logger;
import logging.LoggerFactory;

import java.util.List;

public class DebuggingSystem {


    private static final Logger logger = LoggerFactory.instance().logger(DebuggingSystem.class);

    public static void debug(List<Entity> list) {
        logger.banner("Start Debugging list");
        for (Entity entity : list) {
            logger.log(entity.toString());
        }
        logger.banner("End Debugging List");
    }

    public static void debugMap(int[][] pathMap, String txt) { debugMap(pathMap, txt, 0); }
    public static void debugMap(int[][] pathMap, String txt, int mode) {
        logger.banner(txt);
        // Find the longest value / value with most digits in the map
        int longest = 0;
        StringBuilder sb = new StringBuilder();
        for (int[] row : pathMap) {
            for (int col : row) {
                sb.delete(0, sb.length());
                sb.append(col);
                if (sb.length() <= longest) { continue; }
                longest = sb.length();
            }
        }

        for (int[] row : pathMap) {
            for (int col : row) {
                if (mode == 0) {
                    System.out.print("[" + (col == 0 ? "X" : " ") + "]");
                } else if (mode == 1) {
                    sb.delete(0, sb.length());
                    sb.append(col == 0 ? "" : col);
                    // append spaces to the front if less than longest
                    while (sb.length() < longest) { sb.insert(0, " "); }
                    System.out.print("[" + sb + "]");
                }
            }
            System.out.println();
        }
        logger.banner("End Debugging PathMap");
    }

    public static void log(String value) {
        System.err.println("DEBUGGING SYSTEM");
        System.err.println(value);
    }
}
