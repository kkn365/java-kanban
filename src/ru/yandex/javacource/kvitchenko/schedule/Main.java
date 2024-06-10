package ru.yandex.javacource.kvitchenko.schedule;

import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;
import ru.yandex.javacource.kvitchenko.schedule.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime subtask1StartTime = now.plusMinutes(30);
        LocalDateTime subtask2StartTime = now.plusMinutes(45);
        Duration standartDuration = Duration.ofMinutes(15);

        Epic epic = new Epic("Test epic with two subtasks","Test epic description");
        //epic.setStartTime(subtask1StartTime);
        //epic.setDuration(Duration.ofMinutes(30));
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test subtask 1", "Subtask 1 description", epicId);
        subtask1.setStartTime(subtask1StartTime);
        subtask1.setDuration(standartDuration);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test subtask 2", "Subtask 2 description", epicId);
        subtask2.setStartTime(subtask2StartTime);
        subtask2.setDuration(standartDuration);
        final int subtask2Id = taskManager.addNewSubtask(subtask2);

        LocalDateTime start1 = LocalDateTime.of(2024, 6, 1, 11, 0);
        Subtask subtask3 = new Subtask("Test subtask 3", "Subtask 3 description", epicId);
        subtask3.setStartTime(start1);
        subtask3.setDuration(standartDuration);
        final int subtask3Id = taskManager.addNewSubtask(subtask3);

        LocalDateTime start2 = LocalDateTime.of(2024, 6, 1, 11, 4);
        Subtask subtask4 = new Subtask("Test subtask 4", "Subtask 4 description", epicId);
        subtask4.setStartTime(start2);
        subtask4.setDuration(standartDuration);
        final int subtask4Id = taskManager.addNewSubtask(subtask4);

        taskManager.getEpic(epicId);
        for (int i = 0; i < 10; i++) {
            taskManager.getSubtask(subtask1Id);
            taskManager.getSubtask(subtask2Id);
        }
        printAllTasks(taskManager);

        System.out.println(System.lineSeparator());
        taskManager.deleteSubtask(subtask2Id);
        printAllTasks(taskManager);

        System.out.println(System.lineSeparator());
        taskManager.deleteEpics();
        printAllTasks(taskManager);

    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            if (task.getStartTime() != null) {
                System.out.println(task);
            }
        }
    }

}