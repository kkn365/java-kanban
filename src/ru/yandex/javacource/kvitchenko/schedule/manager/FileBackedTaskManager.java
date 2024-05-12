package ru.yandex.javacource.kvitchenko.schedule.manager;

import ru.yandex.javacource.kvitchenko.schedule.enums.Status;
import ru.yandex.javacource.kvitchenko.schedule.enums.TaskTypes;
import ru.yandex.javacource.kvitchenko.schedule.exceptions.ManagerSaveException;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    static File fileName;

    private void save(Task task) throws ManagerSaveException {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName, StandardCharsets.UTF_8))) {
            fileWriter.append(toString(task));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при сохранении" + task.getId());
        }
    }

    // Метод, который будет восстанавливать данные менеджера из файла при запуске программы.
    public static FileBackedTaskManager loadFromFile(File filename) {
        fileName = filename;
        FileBackedTaskManager taskManager = new FileBackedTaskManager();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(filename, StandardCharsets.UTF_8))) {
            while (fileReader.ready()) {
                Task task = fromString(fileReader.readLine());
                if (task instanceof Epic) {
                    taskManager.addNewEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    taskManager.addNewSubtask((Subtask) task);
                } else {
                    taskManager.addNewTask(task);
                }
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
            e.printStackTrace();
        }
        return taskManager;
    }

    // Метод сохранения задачи в строку
    String toString(Task task) {
        StringBuilder str = new StringBuilder(task.getId() + ",");
        if (task instanceof Epic) {
            str.append(TaskTypes.EPIC + ",");
        } else if (task instanceof Subtask) {
            str.append(TaskTypes.SUBTASK + ",");
        } else {
            str.append(TaskTypes.TASK + ",");
        }
        str.append(task.getName() + "," + task.getStatus() + "," + task.getDescription() + ",");
        if (task instanceof Subtask) {
            str.append(((Subtask) task).getEpicId());
        }
        return str.toString();
    }

    // Метод создания задачи из строки
    static Task fromString(String value) {
        /*
         *   id, type,       name,       status, description,            epic
         *    1, TASK,       Task1,      NEW,    Description task1,
         *    2, EPIC,       Epic2,      DONE,   Description epic2,
         *    3, SUBTASK,    Sub Task2,  DONE,   Description sub task3,  2
         * */
        String[] split = value.split(",");
        switch (split[1]) {
            case "TASK":
                Task task = new Task(split[2], split[4]);
                task.setStatus(Status.valueOf(split[3]));
                task.setId(Integer.parseInt(split[0]));
                return task;
            case "SUBTASK":
                Subtask subtask = new Subtask(split[2], split[4], Integer.parseInt(split[5]));
                subtask.setStatus(Status.valueOf(split[3]));
                subtask.setId(Integer.parseInt(split[0]));
                return subtask;
            case "EPIC":
                Epic epic = new Epic(split[2], split[4]);
                epic.setStatus(Status.valueOf(split[3]));
                epic.setId(Integer.parseInt(split[0]));
                return epic;
            default:
                break;
        }
        return null;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        try {
            save(subtask);
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return subtask.getId();
    }

    @Override
    public int addNewTask(Task task) {
        super.addNewTask(task);
        try {
            save(task);
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        try {
            save(epic);
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return epic.getId();
    }

}
