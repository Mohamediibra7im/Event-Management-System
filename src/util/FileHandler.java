package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler<T> implements DataAccessObject<T> {

    private static final String DATA_FOLDER = "../Data"; // Update this line

    @Override
    public void save(List<T> items, String fileName) {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs(); // Create the folder if it doesn't exist
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(folder, fileName)))) {
            oos.writeObject(items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> load(String fileName) {
        File file = new File(DATA_FOLDER, fileName);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
