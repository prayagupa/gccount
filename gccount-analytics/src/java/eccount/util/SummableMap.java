package eccount.util;

import java.util.HashMap;

/**
 * User: prayag
 * Date: 02/11/13
 * Time: 02:26
 */
public class SummableMap<K, V> extends HashMap<K, V> {

    public V put(K key, V value,String separator) {
        if (!this.containsKey(key)) {
            return super.put(key, value);
        } else {
            V val = this.get(key);
            if (val instanceof Float) {
                Float v = ((Float) val).floatValue() + ((Float) value).floatValue();
                return super.put(key, (V) v);
            } else if (val instanceof String) {
                String v = ((String) val) + separator + ((String) value);
                return super.put(key, (V) v);
            } else if (val instanceof Integer) {
                Integer v = ((Integer) val) + ((Integer) value);
                return super.put(key, (V) v);
            }
            else if(val instanceof Long){
                Long v=((Long) val) +((Long) value);
                return super.put(key,(V) v);
            }
            else
                throw new RuntimeException("The value type " + value.getClass().getName() + " is not supported in this map");
        }
    }

    @Override
    public V put(K key, V value) {
        return put(key,value,";");
    }
}

