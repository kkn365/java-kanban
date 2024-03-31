package ru.yandex.javacource.kvitchenko.schedule.tests.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.util.Managers;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    private final TaskManager taskManager = Managers.getDefault();

    @BeforeEach
    public void beforeEach() {
        taskManager.deleteEpics();
    }

    // проверьте, что наследники класса Task равны друг другу, если равен их id;
    @Test
    void instancesOfTestClassWithTheSameIdMustBeEquivalent() {
        Epic epic1 = new Epic("Test epic 1", "Test epic description 1");
        Epic epic2 = new Epic("Test epic 2", "Test epic description 2");

        final int epic1Id = taskManager.addNewEpic(epic1);
        final int epic2Id = taskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Test subtask 1", "Test subtask description 1", epic1Id);
        Subtask subtask2 = new Subtask("Test subtask 2", "Test subtask description 2", epic2Id);

        final int subtask1Id = taskManager.addNewSubtask(subtask1);
        final int subtask2Id = taskManager.addNewSubtask(subtask2);

        Subtask savedSubtask1 = taskManager.getSubtask(subtask1Id);
        Subtask savedSubtask2 = taskManager.getSubtask(subtask2Id);
        savedSubtask2.setId(subtask1Id);

        assertNotEquals(savedSubtask1.toString(), savedSubtask2.toString(), "Все поля задач совпадают");
        assertEquals(savedSubtask1.getId(), savedSubtask2.getId(), "Идентификаторы задач не совпадают.");
        assertTrue(savedSubtask1.equals(savedSubtask2));
    }

}