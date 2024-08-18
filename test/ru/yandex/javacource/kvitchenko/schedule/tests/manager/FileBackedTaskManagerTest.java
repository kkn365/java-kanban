package ru.yandex.javacource.kvitchenko.schedule.tests.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.exceptions.ManagerSaveException;
import ru.yandex.javacource.kvitchenko.schedule.managers.FileBackedTaskManager;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private static final String HOME = System.getProperty("user.home");

    @Override
    protected FileBackedTaskManager createTaskManager() {
        File file = new File(HOME + File.separator + "java-kanban.csv");
        if (Files.exists(file.toPath())) {
            return FileBackedTaskManager.loadFromFile(file);
        }
        return new FileBackedTaskManager(file);
    }

    @Test
    void shouldThrowManagerSaveExceptionIfFileNotExists() {
        assertThrows(ManagerSaveException.class, () -> {
            File file = new File("/test.csv");
            FileBackedTaskManager.loadFromFile(file);
        }, "Can't read from file");
    }

}
