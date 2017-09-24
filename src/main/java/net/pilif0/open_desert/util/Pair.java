package net.pilif0.open_desert.util;

/**
 * Represents a key-value pair
 *
 * @param <K> Key type
 * @param <V> Value type
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Pair<K, V> {
    /** The key */
    private K key;
    /** The value */
    private V value;

    /**
     * Construct a new pair
     *
     * @param key Key
     * @param value Value
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Return the key
     *
     * @return Key
     */
    public K getKey() {
        return key;
    }

    /**
     * Return the value
     *
     * @return Value
     */
    public V getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if(key != null ? !key.equals(pair.key) : pair.key != null){
            return false;
        }

        return value != null ? value.equals(pair.value) : pair.value == null;
    }

    @Override
    public String toString() {
        return key.toString() + "=" + value.toString();
    }
}
