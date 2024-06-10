package ru.yandex.javacource.kvitchenko.schedule.tests.task;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.enums.Status;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    private final TaskManager taskManager = Managers.getDefault();

    /*
     * 1. Для расчёта статуса Epic. Граничные условия:
     *    a. Все подзадачи со статусом NEW.
     *    b. Все подзадачи со статусом DONE.
     *    c. Подзадачи со статусами NEW и DONE.
     *    d. Подзадачи со статусом IN_PROGRESS.
     */
    @Test
    void calculateEpicStatusBySubtaskStatuses() {

        LocalDateTime subtask1StartTime = LocalDateTime.now();
        LocalDateTime subtask2StartTime = subtask1StartTime.plusMinutes(15);
        LocalDateTime subtask3StartTime = subtask1StartTime.plusMinutes(30);
        LocalDateTime subtask4StartTime = subtask1StartTime.plusMinutes(45);

        Duration standartDuration = Duration.ofMinutes(15);

        Epic epic = new Epic("Test epic with four subtasks", "Test epic description");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test subtask 1", "Subtask 1 description", epicId);
        subtask1.setStartTime(subtask1StartTime);
        subtask1.setDuration(standartDuration);
        taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test subtask 2", "Subtask 2 description", epicId);
        subtask2.setStartTime(subtask2StartTime);
        subtask2.setDuration(standartDuration);
        taskManager.addNewSubtask(subtask2);

        Subtask subtask3 = new Subtask("Test subtask 3", "Subtask 3 description", epicId);
        subtask3.setStartTime(subtask3StartTime);
        subtask3.setDuration(standartDuration);
        taskManager.addNewSubtask(subtask3);

        Subtask subtask4 = new Subtask("Test subtask 4", "Subtask 4 description", epicId);
        subtask4.setStartTime(subtask4StartTime);
        subtask4.setDuration(standartDuration);
        taskManager.addNewSubtask(subtask4);

        // all NEW
        assertEquals(Status.NEW, taskManager.getEpic(epicId).getStatus(),
                "Epic status was calculated incorrectly");

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        // 2xNEW & 2xDONE
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(),
                "Epic status was calculated incorrectly");

        subtask3.setStatus(Status.DONE);
        subtask4.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask3);
        taskManager.updateSubtask(subtask4);

        // all DONE
        assertEquals(Status.DONE, taskManager.getEpic(epicId).getStatus(),
                "Epic status was calculated incorrectly");

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        // 1xIN_PROGRESS
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epicId).getStatus(),
                "Epic status was calculated incorrectly");

    }

}