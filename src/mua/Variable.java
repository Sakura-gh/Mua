package mua;

import java.util.HashMap;
import java.util.Map;

public class Variable {
    private Map<String, String> variablePool;

    public Variable() {
        variablePool = new HashMap<>();
    }

    public String getValue(String key) {
        return variablePool.get(key);
    }

    public void setValue(String key, String value) {
        variablePool.put(key, value);
    }

    public void addMap(String key, String value) {
        variablePool.put(key, value);
    }

    public String removeMap(String key) {
        String value = variablePool.get(key);
        variablePool.remove(key);
        return value;
    }

    public String exsitKey(String key) {
        return String.valueOf(variablePool.containsKey(key));
    }
}
