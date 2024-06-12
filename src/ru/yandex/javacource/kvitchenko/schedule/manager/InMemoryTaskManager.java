package ru.yandex.javacource.kvitchenko.schedule.manager;

import ru.yandex.javacource.kvitchenko.schedule.enums.Status;
import ru.yandex.javacource.kvitchenko.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;
import ru.yandex.javacource.kvitchenko.schedule.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(new TaskPriorityComparator());
    protected int generatorId = 0;

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    @Override
    public void deleteTasks() {
        for (int taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (int subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        subtasks.clear();
        for (int epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        epics.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            for (Subtask subtask : getEpicSubtasks(epic.getId())) {
                historyManager.remove(subtask.getId());
                epic.removeSubtask(subtask.getId());
            }
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return null;
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public int addNewTask(Task task) throws TaskValidationException {
        if (task.getStartTime() != null && hasIntersections(task)) {
            throw new TaskValidationException("Время задачи пересекается, провете время: НАЧАЛО, КОНЕЦ");
        }
        final int id = ++generatorId;
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.add(task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) throws TaskValidationException {
        final int id = ++generatorId;
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) throws TaskValidationException {
        final int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        if (subtask.getStartTime() != null && hasIntersections(subtask)) {
            throw new TaskValidationException("Время подзадачи пересекается, провете время: НАЧАЛО, КОНЕЦ");
        }
        final int id = ++generatorId;
        subtask.setId(id);
        subtasks.put(id, subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epicId);
        prioritizedTasks.add(subtask);
        return id;
    }

    @Override
    public void updateTask(Task task) {
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        int epicId = subtask.getEpicId();
        Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask == null) {
            return;
        }
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        subtasks.put(id, subtask);
        updateEpicStatus(epicId);
    }

    @Override
    public void deleteTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        for (Subtask subtask : getEpicSubtasks(id)) {
            historyManager.remove(subtask.getId());
            subtasks.remove(subtask.getId());
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        historyManager.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        return epic.getSubtasksIds().stream().map(
            id -> subtasks.get(id)
        ).collect(Collectors.toList());
    }

    // Получение истории просмотров
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Управление статусами эпиков
    protected void updateEpicStatus(int epicId) {
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
            updateEpicDuration(epic);
        }
    }

    private void updateEpicDuration(Epic epic) {
        List<Integer> subs = epic.getSubtasksIds();
        if (subs.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            return;
        }
        LocalDateTime start = LocalDateTime.MAX;
        LocalDateTime end = LocalDateTime.MIN;
        Duration duration = Duration.ZERO;
        for (int id : subs) {
            final Subtask subtask = subtasks.get(id);
            final LocalDateTime startTime = subtask.getStartTime();
            final LocalDateTime endTime = subtask.getEndTime();
            if (startTime.isBefore(start)) {
                start = startTime;
            }
            if (endTime.isAfter(end)) {
                end = endTime;
            }
            duration =  duration.plus(subtask.getDuration());
        }
        epic.setDuration(duration);
        epic.setStartTime(start);
        epic.setEndTime(end);
    }

    // Проверка, пересекаются ли две задачи по времени выполнения
    protected boolean isIntersect(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    // Поиск пересечений в существующих задачах
    protected boolean hasIntersections(Task task) {
        if (prioritizedTasks.isEmpty()) {
            return false;
        }
        return prioritizedTasks.stream()
                .filter(existedTask -> isIntersect(
                                existedTask.getStartTime(),
                                existedTask.getStartTime().plus(Duration.from(existedTask.getDuration())),
                                task.getStartTime(),
                                task.getStartTime().plus(Duration.from(task.getDuration()))
                        )
                ).findFirst().isPresent();
    }
}
