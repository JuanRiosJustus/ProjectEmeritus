package main.state;

import com.alibaba.fastjson2.JSON;
import main.logging.EmeritusLogger;
import com.alibaba.fastjson2.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Manages user save data where each top-level JSON key
 * represents a separate "profile".
 */
public class UserSaveStateManager {
    private static final EmeritusLogger mLogger = EmeritusLogger.create(UserSaveStateManager.class);
    private static UserSaveStateManager mInstance = null;
    public static UserSaveStateManager getInstance() {
        if (mInstance == null) {
            mInstance = new UserSaveStateManager();
        }
        return mInstance;
    }

    private JSONObject mRawSaveState;           // Entire JSON of all profiles
    private JSONObject mSelectedProfile;
    private final String mSavePath = "UserSaveState.json";

    private UserSaveStateManager() {
        loadOrInitialize();
    }

    // -------------------------------------------------
    //  PUBLIC API
    // -------------------------------------------------

    /**
     * Retrieves the profile with the given name, or null if it does not exist.
     */
    public JSONObject getProfile(String profileName) {
        JSONObject profile = mRawSaveState.getJSONObject(profileName);
        return profile;
    }

    /**
     * Returns the profile if it exists, or creates a new one if it does not.
     */
    public JSONObject getOrCreateProfile(String profileName) {
        JSONObject profile = mRawSaveState.getJSONObject(profileName);
        if (profile == null) {
            JSONObject newProfile = new JSONObject();
            // Optionally add some default fields:
            // newProfile.put("someDefaultField", 123);
            mRawSaveState.put(profileName, newProfile);
            save();
            profile = newProfile;
        }
        return profile;
    }

    /**
     * Removes a profile entirely.
     */
//    public void removeProfile(String profileName) {
//        boolean hasProfile = mRawSaveState.has(profileName);
//        if (!hasProfile) { return; }
//        mRawSaveState.remove(profileName);
//        save();
//    }

    /**
     * Saves the entire JSON (all profiles) to the same location you loaded from.
     * If loading was from resources inside a JAR, you may need an alternate path (e.g., user home).
     */
    public void save() {
        // In many cases, you might want to save to a user directory or config location,
        // not back into the resource in the JAR.
        // So you can choose a fallback path to write to, like user home directory.
        Path fallbackPath = Paths.get(mSavePath); // or some config directory
        try {
//            Files.writeString(fallbackPath, mRawSaveState.toString(2), StandardCharsets.UTF_8);
            mLogger.info("Successfully saved user data to {}", fallbackPath.toAbsolutePath());
        } catch (Exception e) {
            mLogger.info("Failed to save user data: {}", e.getMessage());
        }
    }

    private static final String UNIT_COLLECTION = "unit.collection";
    public void saveUnitToCollection(JSONObject units) {
        boolean wasUpdated = false;
        JSONObject unitCollection = mSelectedProfile.getJSONObject(UNIT_COLLECTION);
        for (String unitKey : units.keySet()) {
            JSONObject unitData = units.getJSONObject(unitKey);
            unitCollection.put(unitKey, unitData);
        }
        if (!wasUpdated) { return; }
        save();
    }

    // -------------------------------------------------
    //  INTERNALS
    // -------------------------------------------------

    /**
     * Attempts to load the JSON from resources (if present).
     * If not found or fails, initializes a new empty JSON object.
     */
    private void loadOrInitialize() {
        try {
            mLogger.info("Started loading user save data");
            // Attempt to find the resource inside the JAR or classpath
            String saveData = Files.readString(Paths.get(mSavePath));
            mRawSaveState = JSON.parseObject(saveData);
            mSelectedProfile = mRawSaveState.getJSONObject(mRawSaveState.keySet().iterator().next());
            mLogger.info("Finished loading user save data");
        } catch (Exception ex) {
            // If we fail, fallback to an empty object
            mRawSaveState = new JSONObject();
            mLogger.info("Unable to load user save data: {}", ex.getMessage());
        }
    }
}