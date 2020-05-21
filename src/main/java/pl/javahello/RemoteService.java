package pl.javahello;

import java.util.List;

public interface RemoteService<T> {
    T save(T t);

    void delete(T t);

    List<T> findAll();
}
