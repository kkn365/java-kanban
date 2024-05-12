package ru.yandex.javacource.kvitchenko.schedule;

import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.manager.FileBackedTaskManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;
import ru.yandex.javacource.kvitchenko.schedule.util.Managers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        Path path = Paths.get(System.getProperty("java.io.tmpdir") + File.separator + "java-kanban.csv");
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(path.toFile());
        printAllTasks(taskManager);

        //TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic("Test epic with two subtasks","Test epic description");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Test subtask 1", "Subtask 1 description", epicId);
        Subtask subtask2 = new Subtask("Test subtask 2", "Subtask 2 description", epicId);
        final int subtask1Id = taskManager.addNewSubtask(subtask1);
        final int subtask2Id = taskManager.addNewSubtask(subtask2);

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
            System.out.println(task);
        }
    }

}
