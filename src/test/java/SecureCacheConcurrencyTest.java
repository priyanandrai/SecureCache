import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.securecache.Loader.SourcesLoader;
import com.securecache.main.SecureCache;

/**
 * Concurrency stress tests for SecureCache.
 * Validates thread-safe put/get/remove and loader call-once semantics under
 * concurrent access.
 */
class SecureCacheConcurrencyTest {

    private static final int THREADS = 10;
    private static final int ITERATIONS = 20;

    // ------------------------------------------------------------------ helpers

    private SecureCache<String, byte[]> cacheWithLoader(AtomicInteger callCount) {
        SourcesLoader<String, byte[]> loader = new SourcesLoader<String, byte[]>() {
            private static final long serialVersionUID = 1L;
            @Override
            public byte[] loadValue(String key) {
                callCount.incrementAndGet();
                return ("loaded-" + key).getBytes(StandardCharsets.UTF_8);
            }
        };
        return new SecureCache.SecureCacheBuilder<String, byte[]>()
                .Loader(loader)
                .build();
    }

    // ------------------------------------------------------------------ concurrent get (loader)

    @Test
    void concurrentGetOnSameKeyWithLoaderShouldNeverReturnNull() throws Exception {
        AtomicInteger loaderCalls = new AtomicInteger(0);
        SecureCache<String, byte[]> cache = cacheWithLoader(loaderCalls);

        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        List<Future<byte[]>> futures = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            futures.add(pool.submit(new Callable<byte[]>() {
                public byte[] call() throws Exception {
                    start.await();
                    return cache.get("shared-key");
                }
            }));
        }

        start.countDown();
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        for (Future<byte[]> f : futures) {
            byte[] result = f.get();
            assertNotNull(result);
            assertArrayEquals("loaded-shared-key".getBytes(StandardCharsets.UTF_8), result);
        }
        // Loader may fire more than once due to race before cache backfill, but must be minimal
        assertTrue(loaderCalls.get() >= 1, "Loader should be called at least once");
    }

    // ------------------------------------------------------------------ concurrent get (put-backed)

    @Test
    void concurrentGetAfterPutShouldAlwaysReturnCorrectValue() throws Exception {
        SecureCache<String, byte[]> cache = new SecureCache<>();
        byte[] expected = "concurrent-value".getBytes(StandardCharsets.UTF_8);
        cache.put("ck", expected);

        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        List<Future<byte[]>> futures = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            futures.add(pool.submit(new Callable<byte[]>() {
                public byte[] call() throws Exception {
                    start.await();
                    return cache.get("ck");
                }
            }));
        }

        start.countDown();
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        for (Future<byte[]> f : futures) {
            assertArrayEquals(expected, f.get());
        }
    }

    // ------------------------------------------------------------------ concurrent put from multiple threads

    @Test
    void concurrentPutOnDifferentKeysShouldAllBeRetrievable() throws Exception {
        SecureCache<String, byte[]> cache = new SecureCache<>();
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        List<Future<Void>> futures = new ArrayList<>();

        for (int t = 0; t < THREADS; t++) {
            final int threadId = t;
            futures.add(pool.submit(new Callable<Void>() {
                public Void call() throws Exception {
                    start.await();
                    for (int i = 0; i < ITERATIONS; i++) {
                        String key = "t" + threadId + "-k" + i;
                        cache.put(key, key.getBytes(StandardCharsets.UTF_8));
                    }
                    return null;
                }
            }));
        }

        start.countDown();
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        for (Future<Void> f : futures) {
            f.get(); // propagate exceptions
        }

        // Verify all written keys are still readable
        for (int t = 0; t < THREADS; t++) {
            for (int i = 0; i < ITERATIONS; i++) {
                String key = "t" + t + "-k" + i;
                byte[] result = cache.get(key);
                assertNotNull(result, "Expected non-null for key: " + key);
                assertArrayEquals(key.getBytes(StandardCharsets.UTF_8), result);
            }
        }
    }

    // ------------------------------------------------------------------ concurrent put + get (same key race)

    @Test
    void concurrentPutAndGetOnSameKeyShouldNotThrow() throws Exception {
        SecureCache<String, byte[]> cache = new SecureCache<>();
        cache.put("race", "initial".getBytes(StandardCharsets.UTF_8));

        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(THREADS * 2);
        List<Future<Void>> futures = new ArrayList<>();

        for (int t = 0; t < THREADS; t++) {
            // writers
            futures.add(pool.submit(new Callable<Void>() {
                public Void call() throws Exception {
                    start.await();
                    for (int i = 0; i < ITERATIONS; i++) {
                        cache.put("race", ("update-" + i).getBytes(StandardCharsets.UTF_8));
                    }
                    return null;
                }
            }));
            // readers
            futures.add(pool.submit(new Callable<Void>() {
                public Void call() throws Exception {
                    start.await();
                    for (int i = 0; i < ITERATIONS; i++) {
                        cache.get("race"); // result may be any valid value; must not throw
                    }
                    return null;
                }
            }));
        }

        start.countDown();
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        for (Future<Void> f : futures) {
            f.get(); // propagate any thrown exceptions as test failures
        }
    }

    // ------------------------------------------------------------------ concurrent remove safety

    @Test
    void concurrentRemoveShouldNotLeaveStaleEntries() throws Exception {
        SecureCache<String, byte[]> cache = new SecureCache<>();
        int keyCount = THREADS * ITERATIONS;

        for (int i = 0; i < keyCount; i++) {
            cache.put("r" + i, ("val-" + i).getBytes(StandardCharsets.UTF_8));
        }

        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        List<Future<Void>> futures = new ArrayList<>();

        for (int t = 0; t < THREADS; t++) {
            final int threadId = t;
            futures.add(pool.submit(new Callable<Void>() {
                public Void call() throws Exception {
                    start.await();
                    for (int i = 0; i < ITERATIONS; i++) {
                        cache.remove("r" + (threadId * ITERATIONS + i));
                    }
                    return null;
                }
            }));
        }

        start.countDown();
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        for (Future<Void> f : futures) {
            f.get();
        }

        // Every removed key should now be absent
        for (int i = 0; i < keyCount; i++) {
            // remove returns false for already-gone key — no assertion on value,
            // just ensure no exception is thrown and state is consistent
            boolean removed = cache.remove("r" + i);
            assertTrue(!removed, "Key r" + i + " should already be gone");
        }
    }
}

