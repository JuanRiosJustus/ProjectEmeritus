package main.game.state;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import main.game.components.Identity;
import main.game.components.SecondTimer;
import main.game.components.Statistics;
import main.game.entity.Entity;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class UserSavedData {
    
    private static UserSavedData instance = null;
    private static final ELogger logger = ELoggerFactory.getInstance().getELogger(UserSavedData.class);
    private JsonObject data = null;
    private String path = null;
    private final SecondTimer timer = new SecondTimer();

    private UserSavedData() { load("test.json"); }

    public static UserSavedData getInstance() {
        if (instance == null) {
            instance = new UserSavedData();
        }
        return instance;
    }

    public void save() { save(null); }

    public void save(Entity entity) {
        if (data == null) { data = new JsonObject(); }
        // Put the overall time at the root level
        data.put("play_time", Integer.parseInt(data.getOrDefault("play_time", 0).toString() + 1));
        timer.reset();

        // Put the current time at the root level
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String str = sdf.format(new Date());
        data.put("current_time", str);

        // ensure there is node to save unit data
        if (!data.containsKey("units")) { data.put("units", new JsonArray()); }

        // Create object representing the entity to save
        if (entity != null) {
            JsonArray units = (JsonArray) data.get("units");
            units.add(new JsonObject()
                    .putChain("name", entity.get(Identity.class).getName())
                    .putChain("unit", entity.get(Statistics.class).getUnit())
                    .putChain("uuid", entity.get(Identity.class).getUuid()));
        }
        saveToFile(path);
    }

    public JsonObject loadEntity(int index) {
        if (data == null) { return null; }
        JsonArray units = (JsonArray) data.get("units");
        return (JsonObject) units.get(0);
    }





    private boolean saveToFile(String filePath) {
        boolean successful = false;
        try {
            logger.info("Saving file to {}", filePath);
            PrintWriter out = new PrintWriter(new FileWriter(filePath, false), true);
            out.write(Jsoner.prettyPrint(data.toJson()));
            out.close();
            path = filePath;
            logger.info("Successfully saved file to {}", filePath);
            successful = true;
        } catch (Exception ex) {
            path = filePath;
            logger.warn("Failed to save file to {} {}", filePath, ex);
        }
        return successful;
    }

    public boolean load(String filePath) {
        boolean success = false;
        try {
            logger.info("Reading file from {}", filePath);
            FileReader reader = new FileReader(filePath);
            data = (JsonObject) Jsoner.deserialize(reader);
            path = filePath;
            logger.info("Successfully loaded file from {}", filePath);
            success = true;
        } catch (Exception ex) {
            data = new JsonObject();
            path = filePath;
            logger.warn("Failed to load file from {} {}", filePath, ex);
        }
        return success;
    }




    public boolean delete(String filePath) {
        boolean successful = false;
        try {
            logger.info("Deleting file from {}", filePath);
            File toDelete = new File(filePath);
            if (!toDelete.getCanonicalPath().endsWith(".json")) {
                throw new Exception("Unable to delete file");
            }
            path = filePath;
            logger.info("Succesfully deleted file from {}", filePath);
            successful = true;
        } catch (Exception ex) {
            logger.error("Failed to delete file from {} {}", filePath, ex);
            successful = false;
        }
        return successful;
    }
}
