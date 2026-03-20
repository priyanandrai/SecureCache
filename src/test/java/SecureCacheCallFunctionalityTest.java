import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.securecache.Loader.SourcesLoader;
import com.securecache.main.SecureCache;
import com.securecache.secureinterface.JumbleFunctionInterface;

class SecureCacheCallFunctionalityTest {

    private static final class SampleValue implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String name;
        private final int count;

        private SampleValue(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }

    @Test
    void getShouldCallLoaderOnceThenUseCache() {
        AtomicInteger loadCalls = new AtomicInteger(0);

        SourcesLoader<String, byte[]> loader = new SourcesLoader<String, byte[]>() {
            private static final long serialVersionUID = 1L;

            @Override
            public byte[] loadValue(String key) {
                loadCalls.incrementAndGet();
                return ("from-loader-" + key).getBytes(StandardCharsets.UTF_8);
            }
        };

        SecureCache<String, byte[]> cache = new SecureCache.SecureCacheBuilder<String, byte[]>()
                .Loader(loader)
                .build();

        byte[] first = cache.get("alpha");
        byte[] second = cache.get("alpha");

        assertArrayEquals("from-loader-alpha".getBytes(StandardCharsets.UTF_8), first);
        assertArrayEquals(first, second);
        assertEquals(1, loadCalls.get(), "Loader should be called once for same key");
    }

    @Test
    void getShouldReturnNullWhenNoLoaderAndNoCacheEntry() {
        SecureCache<String, byte[]> cache = new SecureCache<>();
        assertNull(cache.get("missing"));
    }

    @Test
    void removeShouldReturnTrueOnlyForExistingKey() {
        SecureCache<String, byte[]> cache = new SecureCache<>();

        cache.put("k1", "value1".getBytes(StandardCharsets.UTF_8));

        assertTrue(cache.remove("k1"));
        assertFalse(cache.remove("k1"));
    }

    @Test
    void putThenGetShouldRoundTripSerializableObject() {
        SecureCache<String, SampleValue> cache = new SecureCache<>();
        SampleValue value = new SampleValue("n1", 42);

        cache.put("obj", value);
        SampleValue fromCache = cache.get("obj");

        assertEquals("n1", fromCache.name);
        assertEquals(42, fromCache.count);
    }

    @Test
    void loaderReturningNullShouldNotPopulateCache() {
        AtomicInteger calls = new AtomicInteger(0);
        SourcesLoader<String, byte[]> loader = new SourcesLoader<String, byte[]>() {
            private static final long serialVersionUID = 1L;

            @Override
            public byte[] loadValue(String key) {
                calls.incrementAndGet();
                return null;
            }
        };

        SecureCache<String, byte[]> cache = new SecureCache.SecureCacheBuilder<String, byte[]>()
                .Loader(loader)
                .build();

        assertNull(cache.get("missing"));
        assertNull(cache.get("missing"));
        assertEquals(2, calls.get(), "Null loader values are not cached");
    }

    @Test
    void putNullShouldNotStoreValue() {
        SecureCache<String, byte[]> cache = new SecureCache<>();

        cache.put("k", null);

        assertNull(cache.get("k"));
        assertFalse(cache.remove("k"));
    }

    @Test
    void removeOnMissingKeyShouldReturnFalse() {
        SecureCache<String, byte[]> cache = new SecureCache<>();
        assertFalse(cache.remove("no-key"));
    }

    @Test
    void loaderShouldBeCalledPerDistinctKeyOnly() {
        AtomicInteger calls = new AtomicInteger(0);
        SourcesLoader<String, byte[]> loader = new SourcesLoader<String, byte[]>() {
            private static final long serialVersionUID = 1L;

            @Override
            public byte[] loadValue(String key) {
                calls.incrementAndGet();
                return ("value-" + key).getBytes(StandardCharsets.UTF_8);
            }
        };

        SecureCache<String, byte[]> cache = new SecureCache.SecureCacheBuilder<String, byte[]>()
                .Loader(loader)
                .build();

        cache.get("a");
        cache.get("a");
        cache.get("b");
        cache.get("b");

        assertEquals(2, calls.get());
    }

    @Test
    void setJumbleFunctionShouldUseProvidedReversibleFunction() {
        SecureCache<String, byte[]> cache = new SecureCache<>();
        ArrayList<JumbleFunctionInterface> custom = new ArrayList<JumbleFunctionInterface>();
        custom.add(new JumbleFunctionInterface() {
            @Override
            public byte[] jumbleData(byte[] data) {
                byte[] out = data.clone();
                for (int i = 0; i < out.length / 2; i++) {
                    byte tmp = out[i];
                    out[i] = out[out.length - 1 - i];
                    out[out.length - 1 - i] = tmp;
                }
                return out;
            }

            @Override
            public byte[] reassemble(byte[] data) {
                return jumbleData(data);
            }
        });

        cache.setJumbleFunction(custom);
        byte[] value = "custom-jumble".getBytes(StandardCharsets.UTF_8);
        cache.put("jk", value);

        assertArrayEquals(value, cache.get("jk"));
    }

    @Test
    void nonSerializableValuesShouldFailClosedAndReturnNull() {
        SecureCache<String, Object> cache = new SecureCache<>();

        cache.put("obj", new Object());

        assertNull(cache.get("obj"));
    }
}

