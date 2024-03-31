package ru.yandex.javacource.kvitchenko.schedule.tests.manager;

import ru.yandex.javacource.kvitchenko.schedule.util.Managers;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.enums.Status;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest {

    private final TaskManager taskManager = Managers.getDefault();

    @BeforeEach
    public void beforeEach() {
        taskManager.deleteTasks();
        taskManager.deleteEpics();
    }

    // Расчет статуса эпика по статусам подзадач
    @Test
    void calcEpicStatusBySubtaskStatuses() {
        final int epicId = taskManager.addNewEpic(new Epic("Test epic", "Test epic description"));
        final int subtask1Id = taskManager.addNewSubtask(new Subtask("Test subtask 1",
                "Subtask 1 description", epicId));
        final int subtask2Id = taskManager.addNewSubtask(new Subtask("Test subtask 2",
                "Subtask 2 description", epicId));

        final Subtask savedSubtask1 = taskManager.getSubtask(subtask1Id);
        final Subtask savedSubtask2 = taskManager.getSubtask(subtask2Id);

        savedSubtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(savedSubtask1);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(),
                "Статус эпика не расчитался.");

        savedSubtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(savedSubtask2);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(),
                "Статус эпика не расчитался.");

        savedSubtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(savedSubtask1);
        assertEquals(Status.DONE, taskManager.getEpic(epicId).getStatus(), "Статус эпика не расчитался.");
    }

    // проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    @Test
    void inMemoryTaskManagerCanFindAllTypesOfTasksById() {
        Task task = new Task("Test task", "Test task description");
        final int taskId = taskManager.addNewTask(task);
        Epic epic = new Epic("Test epic","Test epic description");
        final int epicId = taskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Test subtask", "Subtask description", epicId);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        final Task savedTask = taskManager.getTask(taskId);
        final Epic savedEpic = taskManager.getEpic(epicId);
        final Subtask savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
    }

    // неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void immutabilityOfTheTaskFieldsAfterBeingAddedToTheManager() {
        Task task = new Task("Test task", "Test task description");
        final int taskId = taskManager.addNewTask(task);

        final String fieldName = task.getName();
        final String fieldDescription = task.getDescription();

        taskManager.addNewTask(task);

        Task savedTask = taskManager.getTask(taskId);

        assertEquals(fieldName, savedTask.getName(), "Значение полей name не совпадают.");
        assertEquals(fieldDescription, savedTask.getDescription(), "Значение полей description не совпадают.");
    }

}