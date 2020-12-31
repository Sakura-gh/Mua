package mua;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Variable {
    private Map<String, String> variablePool;

    public Variable() {
        variablePool = new HashMap<>();
        loadConstantVariable();
    }

    private void loadConstantVariable() {
        variablePool.put("pi", String.valueOf(Math.PI));
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

    public void clearVariable() {
        variablePool.clear();
    }

    public String getVarible() {
        String result = "[";
        for (String key : variablePool.keySet()) {
            result += key + " ";
        }
        result = result.trim() + "]";
        return result;
    }

    public void saveVariable(String fileName) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            for (Map.Entry<String, String> variable : variablePool.entrySet()) {
                out.write("make \"" + variable.getKey() + " " + variable.getValue() + "\n");
            }
            out.close();
        } catch (IOException e) {
            System.out.println("save error!");
        }
    }

}
