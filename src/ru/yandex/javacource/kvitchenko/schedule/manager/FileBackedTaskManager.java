package ru.yandex.javacource.kvitchenko.schedule.manager;

import ru.yandex.javacource.kvitchenko.schedule.enums.Status;
import ru.yandex.javacource.kvitchenko.schedule.enums.TaskType;
import ru.yandex.javacource.kvitchenko.schedule.exceptions.ManagerSaveException;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import java.io.*;

import java.nio.file.Files;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {


    private static final String HEADER = "id,type,name,status,description,epic";
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER);
            writer.newLine();

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                final Task task = entry.getValue();
                writer.write(toString(task));
                writer.newLine();
            }

            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                final Task task = entry.getValue();
                writer.write(toString(task));
                writer.newLine();
            }

            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                final Task task = entry.getValue();
                writer.write(toString(task));
                writer.newLine();
            }

            writer.newLine();
        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file: " + file.getName(), e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        final FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try {
            final String csv = Files.readString(file.toPath());
            final String[] lines = csv.split(System.lineSeparator());
            int generatorId = 0;
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.isEmpty()) {
                    break;
                }
                final Task task = fromString(line);
                final int id;
                if (task != null) {
                    id = task.getId();
                    if (id > generatorId) {
                        generatorId = id;
                    }
                    taskManager.addAnyTask(task);
                }
            }
            for (Map.Entry<Integer, Subtask> e : taskManager.subtasks.entrySet()) {
                final Subtask subtask = e.getValue();
                final Epic epic = taskManager.epics.get(subtask.getEpicId());
                epic.addSubtaskId(subtask.getId());
            }
            taskManager.generatorId = generatorId;
        } catch (IOException e) {
            throw new ManagerSaveException("Can't read form file: " + file.getName(), e);
        }
        return taskManager;
    }

    private String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + ","
                + (task.getType().equals(TaskType.SUBTASK) ? ((Subtask) task).getEpicId() : "");
    }

    static Task fromString(String value) {

        String[] split = value.split(",");

        return switch (TaskType.valueOf(split[1])) {
            case TASK -> new Task(Integer.parseInt(split[0]), split[1], split[4], Status.valueOf(split[3]));
            case SUBTASK -> new Subtask(Integer.parseInt(split[0]), split[2], split[4],
                    Status.valueOf(split[3]), Integer.parseInt(split[5]));
            case EPIC -> new Epic(Integer.parseInt(split[0]), split[1], split[4], Status.valueOf(split[3]));
        };

    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save();
        return subtask.getId();
    }

    @Override
    public int addNewTask(Task task) {
        super.addNewTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

}
