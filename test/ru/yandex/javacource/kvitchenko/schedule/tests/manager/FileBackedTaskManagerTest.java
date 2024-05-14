package ru.yandex.javacource.kvitchenko.schedule.tests.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;
import ru.yandex.javacource.kvitchenko.schedule.util.Managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {

    @Test
    void createTestTaskSubtasksAndEpics() {

        TaskManager taskManager = Managers.getDefault();

        Epic epic = new Epic("Test epic with two subtasks","Test epic description");
        final int epicId = taskManager.addNewEpic(epic);
        Subtask subtask1 = new Subtask("Test subtask 1", "Subtask 1 description", epicId);
        Subtask subtask2 = new Subtask("Test subtask 2", "Subtask 2 description", epicId);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);
        final int subtask2Id = taskManager.addNewSubtask(subtask2);

        Task task = new Task("Test task", "Test task description");
        final int taskId = taskManager.addNewTask(task);

        TaskManager taskManager1 = Managers.getDefault();

        assertEquals(taskManager.getTask(epicId), taskManager1.getTask(epicId), "Epics not equals");
        assertEquals(taskManager.getTask(subtask1Id), taskManager1.getTask(subtask1Id), "Subtasks1 not equals");
        assertEquals(taskManager.getTask(subtask2Id), taskManager1.getTask(subtask2Id), "Subtasks2 not equals");
        assertEquals(taskManager.getTask(taskId), taskManager1.getTask(taskId), "Tasks not equals");
    }

}
