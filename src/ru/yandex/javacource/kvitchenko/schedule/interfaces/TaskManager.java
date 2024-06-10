package ru.yandex.javacource.kvitchenko.schedule.interfaces;

import ru.yandex.javacource.kvitchenko.schedule.task.Epic;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import java.util.List;

public interface TaskManager {

    // Получение списка всех задач.
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    // Удаление всех задач.
    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    // Получение по идентификатору.
    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void addAnyTask(Task task);

    // Создание. Сам объект должен передаваться в качестве параметра.
    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    Integer addNewSubtask(Subtask subtask);

    // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    // Удаление по идентификатору.
    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);

    // Получение списка всех подзадач определённого эпика.
    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();

}
