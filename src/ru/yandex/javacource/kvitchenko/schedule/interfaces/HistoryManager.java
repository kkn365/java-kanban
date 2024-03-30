package ru.yandex.javacource.kvitchenko.schedule.interfaces;

import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);

    ArrayList<Task> getHistory();
}
