package ru.yandex.javacource.kvitchenko.schedule.tests.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class InMemoryTaskManagerTest extends TaskManagerTest {

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
    void checkIntersectionCalculatesIsCorrect() {
        Duration standartDuration = Duration.ofMinutes(15);
        Task task1 = new Task("Test task 1", "Test task 1 description");
        task1.setStartTime(LocalDateTime.of(2024, 6, 1, 10, 15));
        task1.setDuration(standartDuration);
        super.taskManager.addNewTask(task1);
        Task task2 = new Task("Test task 2", "Test task 2 description");
        task2.setStartTime(LocalDateTime.of(2024, 6, 1, 10, 20));
        task2.setDuration(standartDuration);
        super.taskManager.addNewTask(task2);
        Task task3 = new Task("Test task 3", "Test task 3 description");
        task3.setStartTime(LocalDateTime.of(2024, 6, 1, 10, 31));
        task3.setDuration(standartDuration);
        super.taskManager.addNewTask(task3);

        List<Task> testTasksList = new ArrayList<>();
        testTasksList.add(task1);
        testTasksList.add(task3);

        assertArrayEquals(testTasksList.toArray(), super.taskManager.getPrioritizedTasks().toArray(),
                "Arrays not equals");
    }

}