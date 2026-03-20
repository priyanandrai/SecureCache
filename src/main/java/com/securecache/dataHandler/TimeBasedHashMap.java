package com.securecache.dataHandler;

import java.util.HashMap;
import java.time.LocalDateTime;

public class TimeBasedHashMap<K, V> extends HashMap<K, ValueWithTimestamp<V>> {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// The expiration time in seconds
    private final long expirationTimeInSeconds;

    // Constructor to set the expiration time
    public TimeBasedHashMap(long expirationTimeInSeconds) {
        this.expirationTimeInSeconds = expirationTimeInSeconds;
    }

    @Override
    public ValueWithTimestamp<V> get(Object key) {
        // Check if the key exists in the map
        if (!containsKey(key)) {
            return null; // Return null if the key doesn't exist
        }
        
        // Get the timestamped value
        ValueWithTimestamp<V> valueWithTimestamp = super.get(key);
        
        // Check if the value has expired
        if (valueWithTimestamp != null && valueWithTimestamp.getTimestamp()
            .plusSeconds(expirationTimeInSeconds)
            .isBefore(LocalDateTime.now())) {
            remove(key); // Remove the expired entry
            return null; // Return null if the value has expired
        }
        
        // Return the value if it hasn't expired
        return valueWithTimestamp;
    }

    public ValueWithTimestamp<V> putValue(K key, V value) {
        // Wrap the value with its timestamp
        ValueWithTimestamp<V> valueWithTimestamp = new ValueWithTimestamp<>(value, LocalDateTime.now());
        return super.put(key, valueWithTimestamp); // Call the original put method
    }

    public V getValue(Object key) {
        ValueWithTimestamp<V> valueWithTimestamp = get(key);
        if (valueWithTimestamp == null) {
            return null;
        }
        return valueWithTimestamp.getValue();
    }

    public V removeValue(Object key) {
        ValueWithTimestamp<V> removed = super.remove(key);
        if (removed == null) {
            return null;
        }
        return removed.getValue();
    }

    public static void main(String[] args) {
        // Create a TimeBasedHashMap with 5 seconds expiration time
        TimeBasedHashMap<String, String> map = new TimeBasedHashMap<>(5);

        // Add some entries to the map
        map.putValue("key1", "value1");
        map.putValue("key2", "value2");

        // Wait for 3 seconds before checking expiration
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }

        // Get values before expiration
        System.out.println("Get key1: " + map.get("key1").getValue());  // Should print the value

        // Wait for another 3 seconds to let the keys expire
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }

        // Get values after expiration
        System.out.println("Get key1: " + map.get("key1"));  // Should print null since the entry has expired
    }
}

// Helper class to store a value with its timestamp
class ValueWithTimestamp<V> {
    private V value;
    private LocalDateTime timestamp;

    public ValueWithTimestamp(V value, LocalDateTime timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public V getValue() {
        return value;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

