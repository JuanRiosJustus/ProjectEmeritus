package test;

import main.constants.SimpleCheckSum;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SimpleCheckSumTest {

    @Test
    public void correctly_adds_new_state() {
        SimpleCheckSum simpleCheckSum = new SimpleCheckSum();
        Assert.assertTrue(simpleCheckSum.isUpdated("test_state", 5, 6, 3, 6));
        Assert.assertFalse(simpleCheckSum.isUpdated("test_state", 5, 6, 3, 6));
        Assert.assertTrue(simpleCheckSum.isUpdated("test_state", 5, 1, 3, 6));
    }
}