package go.builtin;

import go.tuple.Couple;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Map<K extends GoObject, V extends GoObject> extends GoObject {

    private HashMap<K, V> map;

    /**
     * Constructs a nil map with the given key and value types.
     * In Go, only value types are acceptable as keys.
     * @param keyType The type for this map's key.
     * @param valueType The type of data retrieved by the map.
     */
    public Map(GoClass keyType, GoClass valueType) {
        super(GoClass.forMap(keyType, valueType));
        this.map = null;
    }

    // Private easy cloning constructor
    private Map(Map<K, V> mother) {
        super(mother.getGoClass());
        this.map = mother.map;
    }

    /**
     * Gets the value corresponding to the given key.
     * If the key does not exist in the map, a default value is returned.
     * @param key The key
     * @return A tuple of:
     *   - The value corresponding to the key
     *   - True if the key exists in the map; False, otherwise
     */
    public Couple<V, Bool> get(K key) {
        boolean exists = map.containsKey(key);
        V value;
        if (exists) {
            value = (V) map.get(key).clone();
        }
        else {
            value = (V) this.getGoClass().getElementType().newDefaultValue();
        }
        return new Couple<>(value, new Bool(exists));
    }

    /**
     * Inserts the given key into the map
     * @param key The key to insert
     * @param value The value corresponding to the key
     */
    public void put(K key, V value) {
        if (map.containsKey(key)) {
            map.get(key).assign(value);
        }
        else map.put(key, (V) value.clone());
    }

    /**
     * Deletes the given key from the map.
     * @param key The key to delete
     */
    public void delete(K key) {
        map.remove(key);
    }

    @Override
    public void assign(GoObject other) {
        if (other instanceof Map) {
            assign((Map<K, V>) other);
        }
        throw new IllegalArgumentException();
    }

    public void assign(Map<K, V> other) {
        if (other.getGoClass() != this.getGoClass()) {
            throw new IllegalArgumentException();
        }
        this.map = other.map;
    }

    @Override
    public Map<K, V> clone() {
        return new Map<>(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Map) {
            return equals((Map<K, V>) other);
        }
        return false;
    }

    public boolean equals(Map<K, V> other) {
        return this.map == other.map;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(map);
    }

    @Override
    public String toString() {
        List<String> entries = map.entrySet().stream()
                .map((e) -> String.format("%s:%s", e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        if (entries.isEmpty()) return "map[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');

        for (int i = 0; i < entries.size() - 1; i++) {
            sb.append(entries.get(i)).append(' ');
        }
        sb.append("[").append(entries.get(entries.size() - 1)).append("]");
        return sb.toString();
    }

    public static <K extends GoObject, V extends GoObject> Map<K, V> make(GoClass keyType, GoClass valueType) {
        Map<K, V> result = new Map<>(keyType, valueType);
        result.map = new HashMap<>();
        return result;
    }
}
