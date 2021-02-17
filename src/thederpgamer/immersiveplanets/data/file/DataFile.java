package thederpgamer.immersiveplanets.data.file;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * DataFile.java
 * <Description>
 * ==================================================
 * Created 02/17/2021
 * @author TheDerpGamer
 */
public abstract class DataFile implements Serializable {

    private File file;
    private HashMap<String, String> values = new HashMap<>();

    public DataFile(File file) {
        this.file = file;
        try {
            if(!file.exists()) file.createNewFile();
            loadValues();
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    public void saveDefault(String... defaultValues) {
        HashMap<String, String> defaultMap = new HashMap<>();
        for(String s : defaultValues) {
            String[] split = s.split(": ");
            if(split.length == 2) defaultMap.put(split[0], split[1]);
        }
        saveDefault(defaultMap);
    }

    public void saveDefault(HashMap<String, String> defaultValues) {
        for(String key : defaultValues.keySet()) {
            if(!values.containsKey(key)) values.put(key, defaultValues.get(key));
        }
    }

    public void overwriteValues(String... overwrites) {
        HashMap<String, String> overwriteMap = new HashMap<>();
        for(String s : overwrites) {
            String[] split = s.split(": ");
            overwriteMap.put(split[0], split[1]);
        }
        overwriteValues(overwriteMap);
    }

    public void overwriteValues(HashMap<String, String> overwrites) {
        values.clear();
        values = overwrites;
    }

    public void saveValues() throws IOException {
        FileWriter writer = new FileWriter(file);
        for(int i = 0; i < values.keySet().size(); i ++) {
            String key = (String) values.keySet().toArray()[i];
            String value = values.get(key);
            writer.write(key + ": " + value);
            if(i < values.keySet().size() - 1) writer.write("\n");
        }
        writer.close();
    }

    public void loadValues() throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        while(scanner.hasNext()) {
            String line = scanner.nextLine();
            if(line.length() > 0 && !line.startsWith("#")) {
                String[] split = line.split(": ");
                if(split.length == 2) values.put(split[0], split[1]);
            }
        }
    }

    public void setValue(String path, Object... value) {
        String valueString;
        if(value.length > 1) {
            StringBuilder builder = new StringBuilder();
            builder.append("{");
            for(int i = 0; i < value.length; i ++) {
                builder.append(value[i].toString());
                if(i < value.length - 1) builder.append(", ");
            }
            builder.append("}");
            valueString = builder.toString();
        } else {
            valueString = value[0].toString();
        }
        values.put(path, valueString);
    }

    public String getValue(String path) {
        return values.get(path);
    }

    public ArrayList<String> getList(String path) {
        ArrayList<String> stringList = new ArrayList<>();
        String s = getValue(path);
        if(!(s.contains("{") && s.contains("}"))) {
            stringList.add(s);
        } else {
            stringList.addAll(Arrays.asList(s.substring(1, s.length() - 1).split(", ")));
        }
        return stringList;
    }

    public File getFile() {
        return file;
    }
}
