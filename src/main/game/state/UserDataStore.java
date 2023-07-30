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

import main.game.components.SecondTimer;
import main.logging.ELogger;
import main.logging.ELoggerFactory;

public class UserDataStore {
    
    private static UserDataStore instance = null;
    private static ELogger logger = ELoggerFactory.getInstance().getELogger(UserDataStore.class);
    private JsonObject loadedData = null;
    private String loadedPath = null;
    private SecondTimer timer = null;

    private UserDataStore() {  loadedData = new JsonObject(); }

    public static UserDataStore getInstance() {
        if (instance == null) {
            instance = new UserDataStore();
        }
        return instance;
    }

    public boolean load(String path) {
        boolean isLoaded = read(path);
        return isLoaded;
    }

    public void createOrRead(String filepath) {
        boolean loadedExistingData = read(filepath);
        if (loadedExistingData == false) {
            create(filepath);
        }
    }
        
    private boolean create(String filePath) {
        boolean successful = false;
        try {
            logger.info("Saving file to {}", filePath);
            PrintWriter out = new PrintWriter(new FileWriter(filePath, false), true);
            out.write(loadedData.toJson());
            out.close();
            loadedPath = filePath;
            timer = new SecondTimer();
            logger.info("Succesfully saved file to {}", filePath);
            successful = true;
        } catch (Exception ex) {
            logger.error("Failed to save file to {} {}", filePath, ex);
            successful = false;
        }
        return successful;
    }

    public boolean read(String filePath) {
        boolean successful = false;
        try {
            logger.info("Reading file from {}", filePath);
            FileReader reader = new FileReader(filePath);
            loadedData = (JsonObject) Jsoner.deserialize(reader);
            loadedPath = filePath;
            timer = new SecondTimer();
            logger.info("Succesfully read file from {}", filePath);
            successful = true;
        } catch (Exception ex) {
            logger.error("Failed to read file from {} {}", filePath, ex);
            successful = false;
        }
        return successful;
    }
 
    public void update() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String str = sdf.format(new Date());
        loadedData.put("playtime", "????");
        loadedData.put("currenttime", str);
        // if (loadedPath != null) {
        //     create(loadedPath);
        // }
    }

    public boolean delete(String filePath) {
        boolean successful = false;
        try {
            logger.info("Deleting file from {}", filePath);
            File toDelete = new File(filePath);
            if (!toDelete.getCanonicalPath().endsWith(".json")) {
                throw new Exception("Unable to delete file");
            }
            loadedPath = filePath;
            logger.info("Succesfully deleted file from {}", filePath);
            successful = true;
        } catch (Exception ex) {
            logger.error("Failed to delete file from {} {}", filePath, ex);
            successful = false;
        }
        return successful;
    }
}
