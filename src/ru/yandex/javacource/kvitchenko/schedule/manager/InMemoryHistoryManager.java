package ru.yandex.javacource.kvitchenko.schedule.manager;

import ru.yandex.javacource.kvitchenko.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_HISTORY_SIZE = 10;
    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        history.add(task.copy());
        if (MAX_HISTORY_SIZE < history.size()) {
            history.removeFirst();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }
}