package ru.yandex.javacource.kvitchenko.schedule.tests.util;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.manager.InMemoryHistoryManager;
import ru.yandex.javacource.kvitchenko.schedule.manager.InMemoryTaskManager;
import ru.yandex.javacource.kvitchenko.schedule.util.Managers;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    // убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
    @Test
    void getDefaultReturnsInitialisedInstanceOfInMemoryTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        assertTrue(taskManager instanceof InMemoryTaskManager);
        assertSame(InMemoryTaskManager.class, taskManager.getClass(), "Классы объектов не совпадают.");

    }

    @Test
    void getDefaultHistoryReturnsInitialisedInstanceOfInMemoryHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertTrue(historyManager instanceof InMemoryHistoryManager);
        assertSame(InMemoryHistoryManager.class, historyManager.getClass(), "Классы объектов не совпадают.");
    }


}