package ru.yandex.javacource.kvitchenko.schedule.tests.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.javacource.kvitchenko.schedule.exceptions.ManagerSaveException;
import ru.yandex.javacource.kvitchenko.schedule.manager.FileBackedTaskManager;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    void beforeEach() {
        super.taskManager.deleteTasks();
        super.taskManager.deleteSubtasks();
        super.taskManager.deleteEpics();
    }

    @Test
    void TaskManagerTests() {
        super.getTasksList();
        super.getEpicsList();
        super.getSubtasksList();
        super.getPrioritizedTask();
        super.tasksDeletion();
        super.epicsDeletion();
        super.subtasksDeletion();
        super.getTaskById();
        super.getEpicById();
        super.getSubtaskById();
        super.addTask();
        super.addSubtask();
        super.taskUpdating();
        super.epicUpdating();
        super.subtaskUpdating();
        super.taskDeletion();
        super.epicDeletion();
        super.subtaskDeletion();
        super.getEpicSubtasksListByEpicId();
        super.getHistoryList();
    }

    @Test
    void testException() {
        assertThrows(ManagerSaveException.class,() -> {
            File file = new File("/test.csv");
            FileBackedTaskManager.loadFromFile(file);
        }, "Can't read from file");
    }

}
