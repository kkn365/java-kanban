package ru.yandex.javacource.kvitchenko.schedule.manager;

import ru.yandex.javacource.kvitchenko.schedule.task.*;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int generatorId = 0;

    // 2a. Получение списка всех задач.
    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasksCopy = new ArrayList<>();
        for (Task task : tasks.values()) {
            tasksCopy.add(task.getCopy());
        }
        return tasksCopy;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicsCopy = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epicsCopy.add(epic.getCopy());
        }
        return epicsCopy;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtasksCopy = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            subtasksCopy.add(subtask.getCopy());
        }
        return subtasksCopy;
    }

    // 2b. Удаление всех задач.
    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            for (Subtask subtask : getEpicSubtasks(epic.getId())) {
                epic.removeSubtask(subtask.getId());
            }
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    // 2c. Получение по идентификатору.
    public Task getTask(int id) {
        if (tasks.get(id) == null) {
            return null;
        }
        return tasks.get(id).getCopy();
    }

    public Epic getEpic(int id) {
        if (epics.get(id) == null) {
            return null;
        }
        return epics.get(id).getCopy();
    }

    public Subtask getSubtask(int id) {
        if (subtasks.get(id) == null) {
            return null;
        }
        return subtasks.get(id).getCopy();
    }

    // 2d. Создание. Сам объект должен передаваться в качестве параметра.
    public int addNewTask(Task task) {
        final int id = ++generatorId;
        // делаем копию для исключения изменения пользователем статуса по задачам
        Task newTask = task.getCopy();
        newTask.setId(id);
        tasks.put(id, newTask);
        return id;
    }

    public int addNewEpic(Epic epic) {
        final int id = ++generatorId;
        Epic newEpic = epic.getCopy();
        newEpic.setId(id);
        epics.put(id, newEpic);
        return id;
    }

    public Integer addNewSubtask(Subtask subtask) {
        final int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        final int id = ++generatorId;
        Subtask newSubtask = subtask.getCopy();
        newSubtask.setId(id);
        subtasks.put(id, newSubtask);
        epic.addSubtaskId(newSubtask.getId());
        updateEpicStatus(epicId);
        return id;
    }

    // 2e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(Task task) {
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    public void updateEpic(Epic epic) {
        final int id = epic.getId();
        final Epic savedEpic = epics.get(id);
        if (savedEpic == null) {
            return;
        }
        epics.put(id, epic);
        updateEpicStatus(epic.getId());
    }

    public void updateSubtask(Subtask subtask) {
        final int id = subtask.getId();
        final Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask == null) {
            return;
        }
        subtasks.put(id, subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    // 2f. Удаление по идентификатору.
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        for (Subtask subtask : getEpicSubtasks(id)) {
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    // 3a. Получение списка всех подзадач определённого эпика.
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> tasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        for (int id : epic.getSubtasksIds()) {
            tasks.add(subtasks.get(id));
        }
        return tasks;
    }

    // 4. Управление статусами эпиков
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        if (epic.getSubtasksIds().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            int statusCounterNew = 0;
            int statusCounterDone = 0;
            for (Subtask subtask : getEpicSubtasks(epicId)) {
                switch (subtask.getStatus()) {
                    case NEW:
                        statusCounterNew++;
                        break;
                    case DONE:
                        statusCounterDone++;
                        break;
                    default:
                        break;
                }
            }
            if (statusCounterNew == epic.getSubtasksIds().size()) {
                epic.setStatus(Status.NEW);
            } else if (statusCounterDone == epic.getSubtasksIds().size()) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

}
