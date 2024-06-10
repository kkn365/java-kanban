package ru.yandex.javacource.kvitchenko.schedule.tests.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;
import ru.yandex.javacource.kvitchenko.schedule.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    /*
     * Для HistoryManager — тесты для всех методов интерфейса. Граничные условия:
     *    a. Пустая история задач.
     *    b. Дублирование.
     *    c. Удаление из истории: начало, середина, конец.
     */

    final HistoryManager historyManager = Managers.getDefaultHistory();

    // void add(Task task);
    @Test
    void taskIsAddedToHistory() {
        Task task = new Task("Test task", "Test task description");
        task.setStartTime(LocalDateTime.of(2024, 6, 1, 10, 16));
        task.setDuration(Duration.ofMinutes(15));
        historyManager.add(task);

        assertEquals(task, historyManager.getHistory().toArray()[0], "Tasks not equals.");
    }

    // void remove(int id);
    @Test
    void taskIsRemovedFromHistoryById() {
        Task task1 = new Task("Test 1 task", "Test task 1 description");
        task1.setStartTime(LocalDateTime.of(2024, 6, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(15));
        task1.setId(1);
        historyManager.add(task1);

        Task task2 = new Task("Test task 2", "Test task 2 description");
        task2.setStartTime(LocalDateTime.of(2024, 6, 1, 10, 16));
        task2.setDuration(Duration.ofMinutes(15));
        task2.setId(2);
        historyManager.add(task2);

        Task task3 = new Task("Test task 3", "Test task 3 description");
        task3.setStartTime(LocalDateTime.of(2024, 6, 1, 10, 31));
        task3.setDuration(Duration.ofMinutes(15));
        task3.setId(3);
        historyManager.add(task3);

        historyManager.remove(task1.getId());
        assertNotEquals(task1, historyManager.getHistory().stream().filter(
                task -> task.getId().equals(task1.getId())
        ).collect(Collectors.toList()), "Task 1 does not removed from history.");

        historyManager.remove(task3.getId());
        assertNotEquals(task3, historyManager.getHistory().stream().filter(
                task -> task.getId().equals(task3.getId())
        ).collect(Collectors.toList()), "Task 3 does not removed from history.");

        historyManager.remove(task2.getId());
        assertNotEquals(task2, historyManager.getHistory().stream().filter(
                task -> task.getId().equals(task2.getId())
        ).collect(Collectors.toList()), "Task 2 does not removed from history.");

    }

    // List<Task> getHistory();
    @Test
    void historyDoesNotContainDuplications() {
        Task task1 = new Task("Test 1 task", "Test task 1 description");
        task1.setStartTime(LocalDateTime.of(2024, 6, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(15));
        task1.setId(1);
        historyManager.add(task1);

        Task task2 = new Task("Test task 2", "Test task 2 description");
        task2.setStartTime(LocalDateTime.of(2024, 6, 1, 10, 16));
        task2.setDuration(Duration.ofMinutes(15));
        task2.setId(2);
        historyManager.add(task2);

        Task task3 = new Task("Test task 3", "Test task 3 description");
        task3.setStartTime(LocalDateTime.of(2024, 6, 1, 10, 31));
        task3.setDuration(Duration.ofMinutes(15));
        task3.setId(3);
        historyManager.add(task3);

        for (int i = 0; i < 12; i++) {
            historyManager.add(task1);
            historyManager.add(task2);
            historyManager.add(task3);
        }

        List<Task> testTaskList = new ArrayList<>();
        testTaskList.add(task3);
        testTaskList.add(task2);
        testTaskList.add(task1);

        assertArrayEquals(testTaskList.toArray(), historyManager.getHistory().toArray(), "Arrays not equals.");
    }

}