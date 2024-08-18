package ru.yandex.javacource.kvitchenko.schedule.tests.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.enums.Status;
import ru.yandex.javacource.kvitchenko.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.kvitchenko.schedule.managers.InMemoryTaskManager;
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

        Duration standartDuration = Duration.ofMinutes(15);
        super.taskManager.deleteTasks();
        super.taskManager.deleteEpics();

        Task task1 = new Task(
                1001,
                "Test task",
                "Test task description",
                Status.NEW,
                LocalDateTime.of(2024, 06, 20, 9, 0),
               standartDuration
        );

        Task task2 = new Task(
                1002,
                "Test task",
                "Test task description",
                Status.NEW,
                LocalDateTime.of(2024, 06, 20, 9, 2),
                standartDuration
        );

        assertThrows(TaskValidationException.class, () -> {
            super.taskManager.addNewTask(task1);
            super.taskManager.addNewTask(task2);
        }, "Intersection detected");
    }

}