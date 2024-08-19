package ru.yandex.javacource.kvitchenko.schedule.managers;

import ru.yandex.javacource.kvitchenko.schedule.enums.Status;
import ru.yandex.javacource.kvitchenko.schedule.exceptions.NotFoundException;
import ru.yandex.javacource.kvitchenko.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;
import ru.yandex.javacource.kvitchenko.schedule.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(new TaskPriorityComparator());
    protected int generatorId = 0;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

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
    public Task getTask(int id) throws NotFoundException {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Task id=" + id + " not found.");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) throws NotFoundException {
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NotFoundException("Epic id=" + id + " not found.");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) throws NotFoundException {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NotFoundException("Subtask id=" + id + " not found.");
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public int addNewTask(Task task) throws TaskValidationException {
        checkIntersections(task);
        if (task.getId() == null || task.getId() == 0) {
            final int id = ++generatorId;
            task.setId(id);
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        if (epic.getId() == null || epic.getId() == 0) {
            final int id = ++generatorId;
            epic.setId(id);
        }
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        final int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        checkIntersections(subtask);
        if (subtask.getId() == null || subtask.getId() == 0) {
            final int id = ++generatorId;
            subtask.setId(id);
        }
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpic(epic);
        prioritizedTasks.add(subtask);
        return subtask.getId();
    }

    @Override
    public void updateTask(Task task) throws TaskValidationException, NotFoundException {
        final int id = task.getId();
        final Task savedTask = tasks.get(id);
        if (savedTask == null) {
            throw new NotFoundException("Task id=" + id + " not found.");
        }
        if (!task.getStartTime().equals(savedTask.getStartTime()) &&
                !task.getDuration().equals(savedTask.getDuration())) {
            checkIntersections(savedTask);
        }
        tasks.put(id, task);
    }

    @Override
    public void updateEpic(Epic epic) throws NotFoundException {
        final int id = epic.getId();
        Epic savedEpic = epics.get(id);
        if (savedEpic == null) {
            throw new NotFoundException("Epic id=" + id + " not found.");
        }
        updateEpicStatus(id);
        updateEpicDuration(epic);
        epics.put(id, epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) throws TaskValidationException, NotFoundException {
        int id = subtask.getId();
        int epicId = subtask.getEpicId();
        Subtask savedSubtask = subtasks.get(id);
        if (savedSubtask == null) {
            throw new NotFoundException("Subtask id=" + id + " not found.");
        }
        final Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Epic id=" + epicId + " not found.");
        }
        checkIntersections(subtask);
        subtasks.put(id, subtask);
        updateEpic(epic);
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
    public List<Subtask> getEpicSubtasks(int epicId) throws NotFoundException {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Epic id=" + epicId + " not found.");
        }
        return epic.getSubtasksIds().stream().map(subtasks::get).collect(Collectors.toList());
    }

    // Получение истории просмотров
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Управление статусами эпиков
    private void updateEpicStatus(int epicId) throws NotFoundException {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Epic id=" + epicId + " not found.");
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
            duration = duration.plus(subtask.getDuration());
        }
        epic.setDuration(duration);
        epic.setStartTime(start);
        epic.setEndTime(end);
    }

    private void checkIntersections(Task task) throws TaskValidationException {
        if (task.getStartTime() != null && hasIntersections(task)) {
            throw new TaskValidationException("Время задачи пересекается, проверьте время: "
                    + task.getStartTime().format(formatter) + ", "
                    + task.getEndTime().format(formatter) + ")"
            );
        }
    }

    // Проверка, пересекаются ли две задачи по времени выполнения
    private boolean isIntersect(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    // Поиск пересечений в существующих задачах
    private boolean hasIntersections(Task task) {
        if (prioritizedTasks.isEmpty()) {
            return false;
        }
        return prioritizedTasks.stream()
                .filter(existedTask -> !existedTask.equals(task))
                .anyMatch(existedTask -> isIntersect(
                                existedTask.getStartTime(),
                                existedTask.getStartTime().plus(Duration.from(existedTask.getDuration())),
                                task.getStartTime(),
                                task.getStartTime().plus(Duration.from(task.getDuration()))
                        )
                );
    }

}
