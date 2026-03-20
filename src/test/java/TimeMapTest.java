import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.securecache.dataHandler.TimeBasedHashMap;

class TimeMapTest {

    // ------------------------------------------------------------------ basic CRUD

    @Test
    void getValueShouldReturnStoredValueBeforeExpiry() {
        TimeBasedHashMap<String, String> map = new TimeBasedHashMap<String, String>(5);
        map.putValue("k1", "v1");
        assertEquals("v1", map.getValue("k1"));
    }

    @Test
    void getValueMissingKeyShouldReturnNull() {
        TimeBasedHashMap<String, String> map = new TimeBasedHashMap<String, String>(5);
        assertNull(map.getValue("missing"));
    }

    @Test
    void removeValueShouldReturnStoredValueThenNull() {
        TimeBasedHashMap<String, String> map = new TimeBasedHashMap<String, String>(5);
        map.putValue("k1", "v1");
        assertEquals("v1", map.removeValue("k1"));
        assertNull(map.removeValue("k1"));
    }

    @Test
    void removeValueOnMissingKeyShouldReturnNull() {
        TimeBasedHashMap<String, String> map = new TimeBasedHashMap<String, String>(5);
        assertNull(map.removeValue("absent"));
    }

    // ------------------------------------------------------------------ overwrite

    @Test
    void putValueShouldOverwriteExistingKey() {
        TimeBasedHashMap<String, String> map = new TimeBasedHashMap<String, String>(5);
        map.putValue("k", "old");
        map.putValue("k", "new");
        assertEquals("new", map.getValue("k"));
    }

    // ------------------------------------------------------------------ multiple keys

    @Test
    void multipleKeysShouldCoexistIndependently() {
        TimeBasedHashMap<String, String> map = new TimeBasedHashMap<String, String>(5);
        map.putValue("a", "alpha");
        map.putValue("b", "beta");
        map.putValue("c", "gamma");
        assertEquals("alpha", map.getValue("a"));
        assertEquals("beta",  map.getValue("b"));
        assertEquals("gamma", map.getValue("c"));
    }

    @Test
    void removeOnOneKeyShouldNotAffectOthers() {
        TimeBasedHashMap<String, String> map = new TimeBasedHashMap<String, String>(5);
        map.putValue("a", "alpha");
        map.putValue("b", "beta");
        map.removeValue("a");
        assertNull(map.getValue("a"));
        assertEquals("beta", map.getValue("b"));
    }

    // ------------------------------------------------------------------ containsKey / size

    @Test
    void containsKeyShouldBeTrueAfterPut() {
        TimeBasedHashMap<String, String> map = new TimeBasedHashMap<String, String>(5);
        map.putValue("k", "v");
        assertTrue(map.containsKey("k"));
    }

    @Test
    void sizeShouldReflectCurrentEntries() {
        TimeBasedHashMap<String, String> map = new TimeBasedHashMap<String, String>(5);
        assertEquals(0, map.size());
        map.putValue("k1", "v1");
        map.putValue("k2", "v2");
        assertEquals(2, map.size());
        map.removeValue("k1");
        assertEquals(1, map.size());
    }

    // ------------------------------------------------------------------ TTL / expiry

    @Test
    void getValueShouldExpireLazilyOnRead() throws Exception {
        TimeBasedHashMap<String, String> map = new TimeBasedHashMap<String, String>(1);
        map.putValue("k1", "v1");
        Thread.sleep(1200L);
        assertNull(map.getValue("k1"));
        assertNull(map.get("k1"));
    }

    @Test
    void entryStillInMapUntilRead_lazyExpiry() throws Exception {
        TimeBasedHashMap<String, String> map = new TimeBasedHashMap<String, String>(1);
        map.putValue("k1", "v1");
        Thread.sleep(1200L);
        // Entry still in underlying storage (no background eviction thread)
        assertTrue(map.containsKey("k1"));
        // First getValue access triggers eviction
        assertNull(map.getValue("k1"));
        // Now fully gone
        assertFalse(map.containsKey("k1"));
    }

    @Test
    void longTtlEntryShouldSurviveShortWait() throws Exception {
        TimeBasedHashMap<String, String> map = new TimeBasedHashMap<String, String>(60);
        map.putValue("k1", "v1");
        Thread.sleep(200L);
        assertNotNull(map.getValue("k1"));
        assertEquals("v1", map.getValue("k1"));
    }

    @Test
    void removedEntryIsImmediatelyGone() {
        TimeBasedHashMap<String, String> map = new TimeBasedHashMap<String, String>(5);
        map.putValue("k", "v");
        map.removeValue("k");
        assertNull(map.getValue("k"));
        assertFalse(map.containsKey("k"));
    }
}

