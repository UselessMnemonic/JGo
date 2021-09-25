package util;

import java.util.HashMap;

public class TupleMap<K1, K2, V> extends HashMap<K1, HashMap<K2, V>> {

    public void put(K1 key1, K2 key2, V value) {
        if (!containsKey(key1)) {
            put(key1, new HashMap<>());
        }
        HashMap<K2, V> next = get(key1);
        next.put(key2, value);
    }

    public V get(K1 key1, K2 key2) {
        if (!containsKey(key1)) {
            return null;
        }
        return get(key1).get(key2);
    }

    public boolean containsKeys(K1 key1, K2 key2) {
        return containsKey(key1) && get(key1).containsKey(key2);
    }

}
