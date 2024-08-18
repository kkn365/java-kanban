package ru.yandex.javacource.kvitchenko.schedule.util;

import ru.yandex.javacource.kvitchenko.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.managers.FileBackedTaskManager;
import ru.yandex.javacource.kvitchenko.schedule.managers.InMemoryHistoryManager;

import java.io.File;
import java.nio.file.Files;

public class Managers {

    private static final String HOME = System.getProperty("user.home");

    public static TaskManager getDefault() {
        File file = new File(HOME + File.separator + "java-kanban.csv");
        if (Files.exists(file.toPath())) {
            return FileBackedTaskManager.loadFromFile(file);
        }
        return new FileBackedTaskManager(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
