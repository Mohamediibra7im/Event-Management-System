package util;

import java.util.List;

public interface DataAccessObject<T> {
    void save(List<T> items, String fileName);
    List<T> load(String fileName);
}