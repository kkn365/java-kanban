package ru.yandex.javacource.kvitchenko.schedule.tests.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.kvitchenko.schedule.manager.InMemoryTaskManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldThrowTaskValidationExceptionIfTaskHasIntersections() {
        assertThrows(TaskValidationException.class, () -> {
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
        }, "Intersection detected");
    }

}