package ru.yandex.javacource.kvitchenko.schedule.tests.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.util.Managers;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

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

        int epic1Id = taskManager.addNewEpic(epic1);
        int epic2Id = taskManager.addNewEpic(epic2);

        Task savedEpic1 = taskManager.getEpic(epic1Id);
        Task savedEpic2 = taskManager.getEpic(epic2Id);
        savedEpic1.setId(epic2Id);

        assertNotEquals(savedEpic1.toString(), savedEpic2.toString(), "Все поля задач совпадают");
        assertEquals(savedEpic1.getId(), savedEpic2.getId(), "Идентификаторы задач не совпадают.");
        assertEquals(savedEpic1, savedEpic2);
    }

    // проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
    @Test
    void instanceOfEpicClassCannotBeAddedToItselfAsASubtask() {
        Epic epic = new Epic("Test epic", "Test epic description");

        final int epicId = taskManager.addNewEpic(epic);
        final Object savedEpic = taskManager.getEpic(epicId);

        assertThrows(ClassCastException.class, () -> taskManager.addNewSubtask((Subtask) savedEpic));
    }
}