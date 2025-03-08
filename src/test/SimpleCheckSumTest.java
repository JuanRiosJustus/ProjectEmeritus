package test;

import main.constants.SimpleCheckSum;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleCheckSumTest {
    private SimpleCheckSum checksum;

    @Before
    public void setUp() {
        checksum = new SimpleCheckSum();
    }

    @Test
    public void testChecksumUpdatesOnFirstCall() {
        boolean hasChanged = checksum.update("A", 1, 3.14, true);
        assertTrue("Checksum should change on the first update.", hasChanged);
    }

    @Test
    public void testChecksumDoesNotChangeForSameValues() {
        checksum.update("A", 1, 3.14, true);
        boolean hasChanged = checksum.update("A", 1, 3.14, true);
        assertFalse("Checksum should not change when given the same values.", hasChanged);
    }

    @Test
    public void testChecksumChangesForDifferentValues() {
        checksum.update("A", 1, 3.14, true);
        boolean hasChanged = checksum.update("A", 2, 6.28, false);
        assertTrue("Checksum should change when different values are used.", hasChanged);
    }

    @Test
    public void testChecksumChangesForDifferentOrder() {
        checksum.update("A", 1, "B", 3.14, true);
        boolean hasChanged = checksum.update("A", "B", 1, 3.14, true);
        assertTrue("Checksum should change when the order of values is different.", hasChanged);
    }

    @Test
    public void testGetChecksumValue() {
        checksum.update("Test", 42, 2.71);
        int firstChecksum = checksum.get("Test");

        checksum.update("Test", 99, 1.23);
        int secondChecksum = checksum.get("Test");

        assertNotEquals("Checksum values should differ for different inputs.", firstChecksum, secondChecksum);
    }

    @Test
    public void testEmptyChecksumRemainsZero() {
        assertEquals("Initial checksum value should be zero.", 0, checksum.get());
    }

    @Test
    public void testMultipleKeysStoreDifferentChecksums() {
        checksum.update("key1", "Value1", 123);
        checksum.update("key2", "Value2", 456);

        int checksum1 = checksum.get("key1");
        int checksum2 = checksum.get("key2");

        assertNotEquals("Different keys should store different checksum values.", checksum1, checksum2);
    }

    @Test
    public void testUpdateForSameKeyReplacesChecksum() {
        checksum.update("key1", "Value1", 123);
        int initialChecksum = checksum.get("key1");

        checksum.update("key1", "NewValue", 789);
        int updatedChecksum = checksum.get("key1");

        assertNotEquals("Updating the same key with different values should change the checksum.", initialChecksum, updatedChecksum);
    }

    @Test
    public void testGetForUnknownKeyReturnsZero() {
        assertEquals("Fetching a checksum for an unknown key should return null.", 0, checksum.get("unknownKey"));
    }

    @Test
    public void testDefaultKeyBehavior() {
        checksum.update("default_value");
        int defaultChecksum = checksum.get();

        checksum.update("non_default_key", "Test");
        int nonDefaultChecksum = checksum.get("non_default_key");

        assertNotEquals("Default checksum should be separate from explicitly keyed checksums.", defaultChecksum, nonDefaultChecksum);
    }
}