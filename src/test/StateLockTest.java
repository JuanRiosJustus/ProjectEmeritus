package test;

import main.constants.StateLock;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StateLockTest {

    @Test
    public void correctly_adds_new_state() {
        StateLock stateLock = new StateLock();
        Assert.assertTrue(stateLock.isUpdated("test_state", 5, 6, 3, 6));
        Assert.assertFalse(stateLock.isUpdated("test_state", 5, 6, 3, 6));
        Assert.assertTrue(stateLock.isUpdated("test_state", 5, 1, 3, 6));
    }
}