package ru.yandex.javacource.kvitchenko.schedule.tests.util;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.manager.InMemoryHistoryManager;
import ru.yandex.javacource.kvitchenko.schedule.manager.InMemoryTaskManager;
import ru.yandex.javacource.kvitchenko.schedule.util.Managers;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefaultReturnsInitialisedInstanceOfInMemoryTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertInstanceOf(InMemoryTaskManager.class, taskManager);
    }

    @Test
    void getDefaultHistoryReturnsInitialisedInstanceOfInMemoryHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertInstanceOf(InMemoryHistoryManager.class, historyManager);
    }


}