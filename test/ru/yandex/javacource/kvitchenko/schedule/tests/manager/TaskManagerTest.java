package ru.yandex.javacource.kvitchenko.schedule.tests.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @BeforeEach
    public void beforeEach() {
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
    }

    // List<Task> getTasks();
    @Test
    public void shouldReturnTasksList() {
        Task task1 = new Task("Test task 1", "Test task 1 description");
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(15));
        Task task2 = new Task("Test task 2", "Test task 2 description");
        task2.setStartTime(LocalDateTime.now().plusMinutes(20));
        task2.setDuration(Duration.ofMinutes(15));
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
        Epic epic1 = new Epic("Test epic 1", "Test epic 1 description");
        epic1.setStartTime(LocalDateTime.now().plusMinutes(40));
        epic1.setDuration(Duration.ofMinutes(15));
        //Epic epic2 = new Epic("Test epic 2", "Test epic 2 description");
        ArrayList<Epic> testEpicsList = new ArrayList<>();
        taskManager.addNewEpic(epic1);
        //taskManager.addNewEpic(epic2);
        testEpicsList.add(epic1);
        //testEpicsList.add(epic2);

        assertArrayEquals(testEpicsList.toArray(), taskManager.getEpics().toArray(), "Arrays not equal");
    }

    // List<Subtask> getSubtasks();
    @Test
    public void shouldReturnSubtasksList() {

        Epic epic = new Epic("Test epic with two subtasks", "Test epic description");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test subtask 1", "Subtask 1 description", epicId);
        subtask1.setStartTime(LocalDateTime.now().plusMinutes(60));
        subtask1.setDuration(Duration.ofMinutes(15));
        taskManager.addNewSubtask(subtask1);

        ArrayList<Subtask> testSubtasksList = new ArrayList<>();
        testSubtasksList.add(subtask1);

        assertArrayEquals(testSubtasksList.toArray(), taskManager.getSubtasks().toArray(), "Arrays not equal");
    }

    // List<Task> getPrioritizedTasks();
    @Test
    public void shouldReturnPrioritizedTaskList() {
        Duration standartDuration = Duration.ofMinutes(15);
        Task task1 = new Task("Test task 1", "Test task 1 description");
        task1.setStartTime(LocalDateTime.of(2024, 6, 1, 10, 16));
        task1.setDuration(standartDuration);
        taskManager.addNewTask(task1);
        Task task2 = new Task("Test task 2", "Test task 2 description");
        task2.setStartTime(LocalDateTime.of(2024, 6, 1, 9, 16));
        task2.setDuration(standartDuration);
        taskManager.addNewTask(task2);
        Task taskWithMaxPriority = (Task) taskManager.getPrioritizedTasks().toArray()[0];

        assertEquals(task2, taskWithMaxPriority, "Tasks not equal");
    }

    // void deleteTasks();
    @Test
    public void shouldDeleteAllTasks() {
        Task task = new Task("Test task 1", "Test task 1 description");
        task.setStartTime(LocalDateTime.now().plusMinutes(80));
        task.setDuration(Duration.ofMinutes(15));
        taskManager.addNewTask(task);
        taskManager.deleteTasks();

        assertTrue(taskManager.getTasks().isEmpty(), "Tasks doesn't deleted.");
    }

    // void deleteEpics();
    @Test
    public void shouldDeleteAllEpics() {
        Epic epic1 = new Epic("Test epic 1", "Test epic 1 description");
        Epic epic2 = new Epic("Test epic 2", "Test epic 2 description");
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.deleteEpics();

        assertTrue(taskManager.getEpics().isEmpty(), "Epics doesn't deleted.");
    }

    // void deleteSubtasks();
    @Test
    public void shouldDeleteAllSubtasks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime subtask1StartTime = now.plusMinutes(100);
        LocalDateTime subtask2StartTime = now.plusMinutes(120);
        Duration standartDuration = Duration.ofMinutes(15);

        Epic epic = new Epic("Test epic with two subtasks", "Test epic description");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test subtask 1", "Subtask 1 description", epicId);
        subtask1.setStartTime(subtask1StartTime);
        subtask1.setDuration(standartDuration);
        taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test subtask 2", "Subtask 2 description", epicId);
        subtask2.setStartTime(subtask2StartTime);
        subtask2.setDuration(standartDuration);
        taskManager.addNewSubtask(subtask2);

        taskManager.deleteSubtasks();

        assertTrue(taskManager.getSubtasks().isEmpty(), "Subtasks doesn't deleted.");
    }

    // Task getTask(int id);
    @Test
    public void shouldReturnTaskById() {
        Task task = new Task("Test task 1", "Test task 1 description");
        task.setStartTime(LocalDateTime.now().plusMinutes(140));
        task.setDuration(Duration.ofMinutes(15));
        final int taskId = taskManager.addNewTask(task);

        assertEquals(task, taskManager.getTask(taskId), "Tasks not equal");
    }

    // Epic getEpic(int id);
    @Test
    public void shouldReturnEpicById() {
        Epic epic = new Epic("Test epic 1", "Test epic 1 description");
        final int epicId = taskManager.addNewEpic(epic);

        assertEquals(epic, taskManager.getEpic(epicId), "Epics not equal.");
    }

    // Subtask getSubtask(int id);
    @Test
    public void shouldReturnSubtaskById() {
        LocalDateTime subtaskStartTime = LocalDateTime.now().plusMinutes(160);
        Duration standartDuration = Duration.ofMinutes(15);

        Epic epic = new Epic("Test epic", "Test epic description");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test subtask", "Subtask description", epicId);
        subtask.setStartTime(subtaskStartTime);
        subtask.setDuration(standartDuration);
        final int subtaskId = taskManager.addNewSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtask(subtaskId), "Subtasks not equal.");
    }

    // int addNewTask(Task task);
    @Test
    public void shouldAddNewTask() {
        Task task = new Task("Test task 1", "Test task 1 description");
        task.setStartTime(LocalDateTime.now().plusMinutes(180));
        task.setDuration(Duration.ofMinutes(15));
        taskManager.addNewTask(task);

        assertEquals(task, taskManager.getTask(task.getId()), "Tasks not equal.");
    }

    // int addNewEpic(Epic epic);
    @Test
    public void shouldAddNewEpic() {
        Epic epic = new Epic("Test epic", "Test epic description");
        taskManager.addNewEpic(epic);

        assertEquals(epic, taskManager.getEpic(epic.getId()), "Epics not equal.");
    }

    // Integer addNewSubtask(Subtask subtask);
    @Test
    public void shouldAddNewSubtask() {
        LocalDateTime subtaskStartTime = LocalDateTime.now().plusMinutes(200);
        Duration standartDuration = Duration.ofMinutes(15);

        Epic epic = new Epic("Test epic", "Test epic description");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test subtask", "Subtask description", epicId);
        subtask.setStartTime(subtaskStartTime);
        subtask.setDuration(standartDuration);
        taskManager.addNewSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtask(subtask.getId()), "Epics not equal.");
    }

    // void updateTask(Task task);
    @Test
    public void shouldUpdateExistedTask() {
        Task task = new Task("Test task", "Test task description");
        task.setStartTime(LocalDateTime.now().plusMinutes(220));
        task.setDuration(Duration.ofMinutes(15));
        taskManager.addNewTask(task);
        final String newTestTaskName = "New test task name";
        task.setName(newTestTaskName);
        taskManager.updateTask(task);

        assertEquals(task, taskManager.getTask(task.getId()), "Tasks not equal.");
    }

    // void updateEpic(Epic epic);
    @Test
    public void shouldUpdateExistedEpic() {
        Epic epic = new Epic("Test epic", "Test epic description");
        taskManager.addNewEpic(epic);
        final String newTestEpicName = "New test epic name";
        epic.setName(newTestEpicName);
        taskManager.updateTask(epic);

        assertEquals(epic, taskManager.getEpic(epic.getId()), "Epics not equal.");
    }

    // void updateSubtask(Subtask subtask);
    @Test
    public void shouldUpdateExistedSubtask() {
        LocalDateTime subtaskStartTime = LocalDateTime.now().plusMinutes(240);
        Duration standartDuration = Duration.ofMinutes(15);

        Epic epic = new Epic("Test epic", "Test epic description");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test subtask", "Subtask description", epicId);
        subtask.setStartTime(subtaskStartTime);
        subtask.setDuration(standartDuration);
        taskManager.addNewSubtask(subtask);

        final String newTestSubtaskName = "New test subtask name";
        subtask.setName(newTestSubtaskName);
        taskManager.updateSubtask(subtask);

        assertEquals(subtask, taskManager.getSubtask(subtask.getId()), "Subtasks not equal.");
    }

    // void deleteTask(int id);
    @Test
    public void shouldDeleteTaskById() {
        Task task1 = new Task("Test task 1", "Test task 1 description");
        task1.setStartTime(LocalDateTime.now().plusMinutes(260));
        task1.setDuration(Duration.ofMinutes(15));
        Task task2 = new Task("Test task 2", "Test task 2 description");
        task2.setStartTime(LocalDateTime.now().plusMinutes(280));
        task2.setDuration(Duration.ofMinutes(15));
        final int task1Id = taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.deleteTask(task1Id);

        assertNull(taskManager.getTask(task1Id), "Task doesn't deleted.");
    }

    // void deleteEpic(int id);
    @Test
    public void shouldDeleteEpicById() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime subtask1StartTime = now.plusMinutes(300);
        LocalDateTime subtask2StartTime = now.plusMinutes(320);
        Duration standartDuration = Duration.ofMinutes(15);

        Epic epic = new Epic("Test epic with two subtasks", "Test epic description");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test subtask 1", "Subtask 1 description", epicId);
        subtask1.setStartTime(subtask1StartTime);
        subtask1.setDuration(standartDuration);
        taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test subtask 2", "Subtask 2 description", epicId);
        subtask2.setStartTime(subtask2StartTime);
        subtask2.setDuration(standartDuration);
        taskManager.addNewSubtask(subtask2);

        taskManager.deleteEpic(epicId);

        assertNull(taskManager.getEpic(epicId), "Epic doesn't deleted.");
    }

    // void deleteSubtask(int id);
    @Test
    public void shouldDeleteSubtaskById() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime subtask1StartTime = now.plusMinutes(340);
        LocalDateTime subtask2StartTime = now.plusMinutes(360);
        Duration standartDuration = Duration.ofMinutes(15);

        Epic epic = new Epic("Test epic with two subtasks", "Test epic description");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test subtask 1", "Subtask 1 description", epicId);
        subtask1.setStartTime(subtask1StartTime);
        subtask1.setDuration(standartDuration);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test subtask 2", "Subtask 2 description", epicId);
        subtask2.setStartTime(subtask2StartTime);
        subtask2.setDuration(standartDuration);
        taskManager.addNewSubtask(subtask2);

        taskManager.deleteSubtask(subtask1Id);

        assertNull(taskManager.getSubtask(subtask1Id), "Subtask doesn't deleted.");
    }

    // List<Subtask> getEpicSubtasks(int epicId);
    @Test
    public void shouldReturnEpicSubtasksListByEpicId() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime subtask1StartTime = now.plusMinutes(380);
        LocalDateTime subtask2StartTime = now.plusMinutes(400);
        Duration standartDuration = Duration.ofMinutes(15);

        Epic epic = new Epic("Test epic with two subtasks", "Test epic description");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test subtask 1", "Subtask 1 description", epicId);
        subtask1.setStartTime(subtask1StartTime);
        subtask1.setDuration(standartDuration);
        taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test subtask 2", "Subtask 2 description", epicId);
        subtask2.setStartTime(subtask2StartTime);
        subtask2.setDuration(standartDuration);
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
        Task task1 = new Task("Test task 1", "Test task 1 description");
        task1.setStartTime(LocalDateTime.now().plusMinutes(420));
        task1.setDuration(Duration.ofMinutes(15));
        Task task2 = new Task("Test task 2", "Test task 2 description");
        task2.setStartTime(LocalDateTime.now().plusMinutes(440));
        task2.setDuration(Duration.ofMinutes(15));
        ArrayList<Task> testTasksList = new ArrayList<>();
        final int task1Id = taskManager.addNewTask(task1);
        final int task2Id = taskManager.addNewTask(task2);
        testTasksList.add(task1);
        testTasksList.add(task2);
        taskManager.getTask(task2Id);
        taskManager.getTask(task1Id);

        assertEquals(testTasksList.toArray()[0], taskManager.getHistory().toArray()[0], "History not equal.");
    }

}
