package ru.yandex.javacource.kvitchenko.schedule.tests.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.enums.Status;
import ru.yandex.javacource.kvitchenko.schedule.exceptions.NotFoundException;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected abstract T createTaskManager();

    T taskManager = createTaskManager();

    private final Duration standartDuration = Duration.ofMinutes(15);

    @BeforeEach
    public void beforeEach() {
        taskManager.deleteTasks();
        taskManager.deleteEpics();
        taskManager.deleteSubtasks();
    }

    // List<Task> getTasks();
    @Test
    public void shouldReturnTasksList() {

        Task task1 = new Task(
                1001,
                "Test task",
                "Test task description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 1, 9, 0),
                standartDuration
        );

        Task task2 = new Task(
                1002,
                "Test task",
                "Test task description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 1, 9, 16),
                standartDuration
        );

        ArrayList<Task> testTasksList = new ArrayList<>();
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        testTasksList.add(task1);
        testTasksList.add(task2);

        assertArrayEquals(testTasksList.toArray(), taskManager.getTasks().toArray(), "Arrays not equal.");
    }

    // List<Epic> getEpics();
    @Test
    public void shouldReturnEpicsList() {

        Epic epic1 = new Epic(
                1001,
                "Test epic",
                "Test epic description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 2, 9, 0),
                standartDuration
        );

        Epic epic2 = new Epic(
                1002,
                "Test epic",
                "Test epic description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 2, 10, 0),
                standartDuration
        );

        ArrayList<Epic> testEpicsList = new ArrayList<>();
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        testEpicsList.add(epic1);
        testEpicsList.add(epic2);

        assertArrayEquals(testEpicsList.toArray(), taskManager.getEpics().toArray(), "Arrays not equal");
    }

    // List<Subtask> getSubtasks();
    @Test
    public void shouldReturnSubtasksList() {

        Epic epic = new Epic(
                1000,
                "Test epic with two subtasks",
                "Test epic description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 3, 9, 0),
                standartDuration
        );

        Subtask subtask1 = new Subtask(
                1001,
                "Test subtask 1",
                "Test subtask 1 description",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 6, 3, 9, 0),
                standartDuration,
                1000
        );

        Subtask subtask2 = new Subtask(
                1002,
                "Test subtask 2",
                "Test subtask 2 description",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 6, 3, 9, 16),
                standartDuration,
                1000
        );

        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        ArrayList<Subtask> testSubtasksList = new ArrayList<>();
        testSubtasksList.add(subtask1);
        testSubtasksList.add(subtask2);

        assertArrayEquals(testSubtasksList.toArray(), taskManager.getSubtasks().toArray(), "Arrays not equal");
    }

    // List<Task> getPrioritizedTasks();
    @Test
    public void shouldReturnPrioritizedTaskList() {

        Task task1 = new Task(
                1001,
                "Test task",
                "Test task description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 4, 9, 0),
                standartDuration
        );

        Task task2 = new Task(
                1002,
                "Test task",
                "Test task description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 4, 9, 16),
                standartDuration
        );

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        Task taskWithMaxPriority = (Task) taskManager.getPrioritizedTasks().toArray()[0];

        assertEquals(task1, taskWithMaxPriority, "Tasks not equal");
    }

    // void deleteTasks();
    @Test
    public void shouldDeleteAllTasks() {

        Task task1 = new Task(
                1001,
                "Test task",
                "Test task description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 5, 9, 0),
                standartDuration
        );
        taskManager.addNewTask(task1);
        taskManager.deleteTasks();

        assertTrue(taskManager.getTasks().isEmpty(), "Tasks doesn't deleted.");
    }

    // void deleteEpics();
    @Test
    public void shouldDeleteAllEpics() {

        Epic epic1 = new Epic(
                1001,
                "Test epic",
                "Test epic description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 6, 9, 0),
                standartDuration
        );

        Epic epic2 = new Epic(
                1002,
                "Test epic",
                "Test epic description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 6, 10, 0),
                standartDuration
        );

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.deleteEpics();

        assertTrue(taskManager.getEpics().isEmpty(), "Epics doesn't deleted.");
    }

    // void deleteSubtasks();
    @Test
    public void shouldDeleteAllSubtasks() {

        Epic epic = new Epic(
                1000,
                "Test epic with two subtasks",
                "Test epic description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 7, 9, 0),
                standartDuration
        );

        Subtask subtask1 = new Subtask(
                1001,
                "Test subtask 1",
                "Test subtask 1 description",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 6, 7, 9, 0),
                standartDuration,
                1000
        );

        Subtask subtask2 = new Subtask(
                1002,
                "Test subtask 2",
                "Test subtask 2 description",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 6, 20, 9, 16),
                standartDuration,
                1000
        );

        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.updateEpic(epic);

        taskManager.deleteSubtasks();

        assertTrue(taskManager.getSubtasks().isEmpty(), "Subtasks doesn't deleted.");
    }

    // Task getTask(int id);
    @Test
    public void shouldReturnTaskById() {

        Task task1 = new Task(
                1001,
                "Test task",
                "Test task description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 8, 9, 0),
                standartDuration
        );

        final int taskId = taskManager.addNewTask(task1);

        assertEquals(task1, taskManager.getTask(taskId), "Tasks not equal");
    }

    // Epic getEpic(int id);
    @Test
    public void shouldReturnEpicById() {

        Epic epic = new Epic(
                1000,
                "Test epic with two subtasks",
                "Test epic description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 9, 9, 0),
                standartDuration
        );

        final int epicId = taskManager.addNewEpic(epic);

        assertEquals(epic, taskManager.getEpic(epicId), "Epics not equal.");
    }

    // Subtask getSubtask(int id);
    @Test
    public void shouldReturnSubtaskById() {

        Epic epic = new Epic(
                1000,
                "Test epic with two subtasks",
                "Test epic description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 10, 9, 0),
                standartDuration
        );

        Subtask subtask1 = new Subtask(
                1001,
                "Test subtask 1",
                "Test subtask 1 description",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 6, 10, 9, 0),
                standartDuration,
                1000
        );

        taskManager.addNewEpic(epic);
        final int subtaskId = taskManager.addNewSubtask(subtask1);
        taskManager.updateEpic(epic);

        assertEquals(subtask1, taskManager.getSubtask(subtaskId), "Subtasks not equal.");
    }

    // void updateTask(Task task);
    @Test
    public void shouldUpdateExistedTask() {

        Task task1 = new Task(
                1001,
                "Test task",
                "Test task description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 11, 9, 0),
                standartDuration
        );

        taskManager.addNewTask(task1);
        final String newTestTaskName = "New test task name";
        task1.setName(newTestTaskName);
        taskManager.updateTask(task1);

        assertEquals(task1, taskManager.getTask(task1.getId()), "Tasks not equal.");
    }

    // void updateEpic(Epic epic);
    @Test
    public void shouldUpdateExistedEpic() {

        Epic epic = new Epic(
                1000,
                "Test epic with two subtasks",
                "Test epic description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 12, 9, 0),
                standartDuration
        );

        Subtask subtask1 = new Subtask(
                1001,
                "Test subtask 1",
                "Test subtask 1 description",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 6, 12, 9, 0),
                standartDuration,
                1000
        );

        final int epicId = taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask1);
        taskManager.updateEpic(epic);

        final String newEpicDescription = "New test epic description";
        epic.setDescription(newEpicDescription);
        taskManager.updateEpic(epic);

        assertEquals(newEpicDescription, taskManager.getEpic(epicId).getDescription(), "Fields not equal.");
    }

    // void updateSubtask(Subtask subtask);
    @Test
    public void shouldUpdateExistedSubtask() {

        Epic epic = new Epic(
                1000,
                "Test epic with two subtasks",
                "Test epic description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 13, 9, 0),
                standartDuration
        );

        Subtask subtask1 = new Subtask(
                1001,
                "Test subtask 1",
                "Test subtask 1 description",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 6, 13, 9, 0),
                standartDuration,
                1000
        );

        taskManager.addNewEpic(epic);
        final int subtaskId = taskManager.addNewSubtask(subtask1);
        taskManager.updateEpic(epic);

        final String newTestSubtaskName = "New test subtask name";
        subtask1.setName(newTestSubtaskName);
        taskManager.updateSubtask(subtask1);

        assertEquals(newTestSubtaskName, taskManager.getSubtask(subtaskId).getName(), "Fields not equal.");
    }

    // void deleteTask(int id);
    @Test
    public void shouldDeleteTaskById() {

        Task task1 = new Task(
                1001,
                "Test task",
                "Test task description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 14, 9, 0),
                standartDuration
        );

        final int task1Id = taskManager.addNewTask(task1);
        taskManager.deleteTask(task1Id);

        assertThrows(NotFoundException.class, () -> {
            taskManager.getTask(task1Id);
        }, "Task doesn't deleted.");

    }

    // void deleteEpic(int id);
    @Test
    public void shouldDeleteEpicById() {

        Epic epic = new Epic(
                1000,
                "Test epic with two subtasks",
                "Test epic description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 15, 9, 0),
                standartDuration
        );

        final int epicId = taskManager.addNewEpic(epic);
        taskManager.deleteEpic(epicId);

        assertThrows(NotFoundException.class, () -> {
            taskManager.getEpic(epicId);
        }, "Epic doesn't deleted.");

    }

    // void deleteSubtask(int id);
    @Test
    public void shouldDeleteSubtaskById() {

        Epic epic = new Epic(
                1000,
                "Test epic with two subtasks",
                "Test epic description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 16, 9, 0),
                standartDuration
        );

        Subtask subtask = new Subtask(
                1001,
                "Test subtask 1",
                "Test subtask 1 description",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 6, 16, 9, 0),
                standartDuration,
                1000
        );

        taskManager.addNewEpic(epic);
        final int subtaskId = taskManager.addNewSubtask(subtask);
        taskManager.deleteSubtask(subtaskId);

        assertThrows(NotFoundException.class, () -> {
            taskManager.getSubtask(subtaskId);
        }, "Subtask doesn't deleted.");

    }

    // List<Subtask> getEpicSubtasks(int epicId);
    @Test
    public void shouldReturnEpicSubtasksListByEpicId() {

        Epic epic = new Epic(
                1000,
                "Test epic with two subtasks",
                "Test epic description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 17, 9, 0),
                standartDuration
        );

        Subtask subtask1 = new Subtask(
                1001,
                "Test subtask 1",
                "Test subtask 1 description",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 6, 17, 9, 0),
                standartDuration,
                1000
        );

        Subtask subtask2 = new Subtask(
                1002,
                "Test subtask 2",
                "Test subtask 2 description",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 6, 17, 9, 16),
                standartDuration,
                1000
        );

        final int epicId = taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);

        assertArrayEquals(subtasks.toArray(), taskManager.getEpicSubtasks(epicId).toArray(),
                "Arrays not equal.");
    }

    // List<Task> getHistory();
    @Test
    public void shouldReturnHistoryList() {

        Task task1 = new Task(
                1001,
                "Test task 1",
                "Test task 1 description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 18, 9, 0),
                standartDuration
        );

        Task task2 = new Task(
                1002,
                "Test task 2",
                "Test task 2 description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 18, 9, 16),
                standartDuration
        );

        Task task3 = new Task(
                1001,
                "Test task 3",
                "Test task 3 description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 18, 9, 32),
                standartDuration
        );

        ArrayList<Task> testTasksList = new ArrayList<>();
        final int task1Id = taskManager.addNewTask(task1);
        final int task2Id = taskManager.addNewTask(task2);
        final int task3Id = taskManager.addNewTask(task3);

        testTasksList.add(task1);
        testTasksList.add(task2);
        testTasksList.add(task3);

        taskManager.getTask(task3Id);
        taskManager.getTask(task2Id);
        taskManager.getTask(task1Id);

        assertEquals(testTasksList.toArray()[0], taskManager.getHistory().toArray()[0], "History not equal.");
    }

    @Test
    void shouldCalculateEpicStatusBySubtaskStatuses() {

        Epic epic = new Epic(
                1000,
                "Test epic with two subtasks",
                "Test epic description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 19, 9, 0),
                standartDuration
        );

        Subtask subtask1 = new Subtask(
                1001,
                "Test subtask 1",
                "Test subtask 1 description",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 6, 19, 9, 0),
                standartDuration,
                1000
        );

        Subtask subtask2 = new Subtask(
                1002,
                "Test subtask 2",
                "Test subtask 2 description",
                Status.DONE,
                LocalDateTime.of(2024, 6, 19, 9, 16),
                standartDuration,
                1000
        );

        final int epicId = taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        // all NEW
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(),
                "Epic status was calculated incorrectly");

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        // 2xNEW & 2xDONE
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(),
                "Epic status was calculated incorrectly");

        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);

        // all DONE
        assertEquals(Status.DONE, taskManager.getEpic(epicId).getStatus(),
                "Epic status was calculated incorrectly");

    }

    @Test
    void shouldCalculateEpicTimeParametersFromIncludedSubtasks() {

        Epic epic = new Epic(
                1000,
                "Test epic with two subtasks",
                "Test epic description",
                Status.NEW,
                LocalDateTime.of(2024, 6, 20, 18, 0),
                standartDuration
        );

        Subtask subtask1 = new Subtask(
                1001,
                "Test subtask 1",
                "Test subtask 1 description",
                Status.IN_PROGRESS,
                LocalDateTime.of(2024, 6, 20, 18, 0),
                standartDuration,
                1000
        );

        Subtask subtask2 = new Subtask(
                1002,
                "Test subtask 2",
                "Test subtask 2 description",
                Status.DONE,
                LocalDateTime.of(2024, 6, 20, 18, 16),
                standartDuration,
                1000
        );

        Subtask subtask3 = new Subtask(
                1003,
                "Test subtask 3",
                "Test subtask 3 description",
                Status.DONE,
                LocalDateTime.of(2024, 6, 20, 18, 32),
                standartDuration,
                1000
        );

        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);
        taskManager.updateEpic(epic);

        assertEquals(epic.getStartTime(),subtask1.getStartTime(), "Epic start time calculates incorrect.");
        assertEquals(epic.getEndTime(),subtask3.getEndTime(), "Epic end time calculates incorrect.");
    }

}
