package mua;

import java.util.HashMap;
import java.util.Map;

public class Variable {
    private static Map<String, String> variablePool = new HashMap<String, String>();

    public static String getValue(String key) {
        return Variable.variablePool.get(key);
    }

    public static void setValue(String key, String value) {
        Variable.variablePool.put(key, value);
    }

    public static void addMap(String key, String value) {
        Variable.variablePool.put(key, value);
    }
}
