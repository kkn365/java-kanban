package ru.yandex.javacource.kvitchenko.schedule.tests.task;

import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    public void beforeEach() {
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
    }

    @Test
    void shouldCalculateEpicStatusBySubtaskStatuses() {

        LocalDateTime subtask1StartTime = LocalDateTime.now();
        LocalDateTime subtask2StartTime = subtask1StartTime.plusMinutes(20);
        LocalDateTime subtask3StartTime = subtask1StartTime.plusMinutes(40);
        LocalDateTime subtask4StartTime = subtask1StartTime.plusMinutes(60);

        Duration standartDuration = Duration.ofMinutes(15);

        Epic epic = new Epic("Test epic with four subtasks", "Test epic description");
        epic.setStartTime(subtask1StartTime.minusMinutes(10));
        epic.setDuration(standartDuration);
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

    @Test
    void shouldCalculateEpicTimeParametersFromIncludedSubtasks() {
        LocalDateTime subtask1StartTime = LocalDateTime.now();
        LocalDateTime subtask2StartTime = subtask1StartTime.plusMinutes(20);
        LocalDateTime subtask3StartTime = subtask1StartTime.plusMinutes(40);
        LocalDateTime subtask4StartTime = subtask1StartTime.plusMinutes(60);

        Duration standartDuration = Duration.ofMinutes(15);

        Epic epic = new Epic("Test epic with four subtasks", "Test epic description");
        epic.setStartTime(subtask1StartTime.minusMinutes(10));
        epic.setDuration(standartDuration);
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

        assertEquals(epic.getStartTime(),subtask1StartTime, "Epic start time calculates incorrect.");
        assertEquals(epic.getEndTime(),subtask4.getEndTime(), "Epic end time calculates incorrect.");
    }
}