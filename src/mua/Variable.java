package mua;

import java.util.HashMap;
import java.util.Map;

// public class Variable {
//     private static Map<String, String> variablePool = new HashMap<String, String>();

//     public static String getValue(String key) {
//         return Variable.variablePool.get(key);
//     }

//     public static void setValue(String key, String value) {
//         Variable.variablePool.put(key, value);
//     }

//     public static void addMap(String key, String value) {
//         Variable.variablePool.put(key, value);
//     }

//     public static String removeMap(String key) {
//         String value = Variable.variablePool.get(key);
//         Variable.variablePool.remove(key);
//         return value;
//     }

//     public static String exsitKey(String key) {
//         return String.valueOf(Variable.variablePool.containsKey(key));
//     }
// }

// class LocalVariable {
//     private Map<String, String> variablePool;

//     public LocalVariable() {
//         variablePool = new HashMap<>();
//     }

//     public String getValue(String key) {
//         return variablePool.get(key);
//     }

//     public void setValue(String key, String value) {
//         variablePool.put(key, value);
//     }

//     public void addMap(String key, String value) {
//         variablePool.put(key, value);
//     }

//     public String removeMap(String key) {
//         String value = variablePool.get(key);
//         variablePool.remove(key);
//         return value;
//     }

//     public String exsitKey(String key) {
//         return String.valueOf(variablePool.containsKey(key));
//     }
// }

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
