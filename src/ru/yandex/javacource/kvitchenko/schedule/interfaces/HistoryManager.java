package ru.yandex.javacource.kvitchenko.schedule.interfaces;

import java.util.List;

public interface HistoryManager<T> {

    void add(T task);

    void remove(int id);

    List<T> getHistory();

}
