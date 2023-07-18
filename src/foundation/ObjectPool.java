package foundation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class ObjectPool<T> {

    private Map<String,T> available = new HashMap<String, T>();
    private Map<String, T> unavailable = new HashMap<String, T>();

    public T checkout() {
        T t;

        if (available.size() > 0) {
            Set<String> keys = available.keySet();

        }
        // Iterator iter = available.keys();
        // if (available.size() > 0) {
        //     // t = available.rem
        // }  
    }
}
