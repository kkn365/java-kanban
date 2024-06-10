package ru.yandex.javacource.kvitchenko.schedule.tests.task;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    // проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    void instancesOfTestClassWithTheSameIdMustBeEquivalent() {
        Task task1 = new Task("Test addNewTask 1", "Test addNewTask description 1");
        Task task2 = new Task("Test addNewTask 2", "Test addNewTask description 2");

        task1.setId(1);
        task2.setId(1);

        assertNotEquals(task1.toString(), task2.toString(), "Все поля задач совпадают");
        assertEquals(task1.getId(), task2.getId(), "Идентификаторы задач не совпадают.");
        assertEquals(task1, task2);
    }
}