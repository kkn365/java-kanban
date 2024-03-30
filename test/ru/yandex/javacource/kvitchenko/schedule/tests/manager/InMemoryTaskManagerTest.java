package ru.yandex.javacource.kvitchenko.schedule.tests.manager;

import ru.yandex.javacource.kvitchenko.schedule.util.Managers;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.enums.Status;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

class InMemoryTaskManagerTest {

    private final Managers manager = new Managers();
    private final TaskManager taskManager = manager.getDefault();

    @BeforeEach
    public void beforeEach() {
        taskManager.deleteTasks();
        taskManager.deleteEpics();
    }

    // Создание задачи.
    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.addNewTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    // Создание эпика с двумя подзадачами
    @Test
    void addNewEpicWithTwoSubtasks() {
        Epic epic = new Epic("Test addNewEpicWithTwoSubtasks",
                "Test addNewEpicWithTwoSubtasks description");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test subtask 1", "Subtask 1 description", epicId);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test subtask 2", "Subtask 2 description", epicId);
        final int subtask2Id = taskManager.addNewSubtask(subtask2);

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final ArrayList<Integer> subtasks = epic.getSubtasksIds();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач.");

        final Subtask savedSubtask1 = taskManager.getSubtask(subtask1Id);

        assertNotNull(savedSubtask1, "Подзадача 1 не найдена.");
        assertEquals(subtask1, savedSubtask1, "Подзадачи 1 не совпадают.");

        final Subtask savedSubtask2 = taskManager.getSubtask(subtask2Id);

        assertNotNull(savedSubtask2, "Подзадача 2 не найдена.");
        assertEquals(subtask2, savedSubtask2, "Подзадачи 2 не совпадают.");
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

    // 2. Распечатайте списки эпиков, задач и подзадач через System.out.println(..).
    // 3.1. Измените статусы созданных объектов, распечатайте их.
    // 3.2. Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
    // 4.1. Удаление задачи
    // 4.2. Удаление эпика
    // Удаление всех объектов
}