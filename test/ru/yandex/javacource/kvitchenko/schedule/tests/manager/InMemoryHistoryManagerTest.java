package ru.yandex.javacource.kvitchenko.schedule.tests.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;
import ru.yandex.javacource.kvitchenko.schedule.util.Managers;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private final TaskManager taskManager = Managers.getDefault();

    // убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void historyManagerStoresPreviousVersionOfTheTask() {
        String name1 = "Test task";
        String name2 = "Test task name version 2";
        String description1 = "Test task description";
        String description2 = "Test task description version 2";

        Task task = new Task(name1, description1);
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTask(taskId);
        savedTask.setName(name2);
        savedTask.setDescription(description2);
        taskManager.updateTask(savedTask);

        ArrayList<Task> savedHistory = taskManager.getHistory();

        assertEquals(name1, savedHistory.getFirst().getName(), "Значение поля name в сохранной задаче изменилось");
        assertEquals(description1, savedHistory.getFirst().getDescription(),
                "Значение поля description в сохранной задаче изменилось");
    }

}