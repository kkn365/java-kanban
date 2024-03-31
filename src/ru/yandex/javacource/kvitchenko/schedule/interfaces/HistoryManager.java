package ru.yandex.javacource.kvitchenko.schedule.interfaces;

import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}
