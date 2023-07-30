package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import main.game.state.UserDataStore;

public class UserDataStoreTest {


    @Test
    public void successfulSavesJsonData() {
        String path = "newData.json";
//        UserDataStore.getInstance().create(path);
        File f = new File(path);
        assertTrue(f.exists());
        assertTrue(f.getAbsolutePath().endsWith(".json"));
        f.delete();
        assertFalse(f.exists());
    }
}
