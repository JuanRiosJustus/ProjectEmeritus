package test;

import main.utils.MathUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MathUtilsTest {

    @Test
    public void getMappedValueWithinExpectations() {
        int value = 10;
        int actual = (int) MathUtils.map(value, 0, 20, 0, 100);
        assertEquals(50, actual);
    }

    @Test
    public void getMappedValueWithinExpectations2() {
//        int value = 21;
//        int actual = (int) MathUtils.mapToRange(value, 0, 20, 0, 10);
//        assertEquals(100, actual);
    }
}
