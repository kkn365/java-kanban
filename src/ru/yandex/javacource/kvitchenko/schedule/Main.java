package ru.yandex.javacource.kvitchenko.schedule;

import ru.yandex.javacource.kvitchenko.schedule.manager.*;
import ru.yandex.javacource.kvitchenko.schedule.task.*;

public class Main {

    static int idTestTask1; // идентификатор тестовой задачи №1
    static int idTestTask2; // идентификатор тестовой задачи №2
    static int idTestEpic1; // идентификатор тестового эпика №1
    static int idTestEpic2; // идентификатор тестового эпика №2
    static int idTestSubtask1; // идентификатор тестовой подзадачи №1 тестового эпика №1
    static int idTestSubtask2; // идентификатор тестовой подзадачи №2 тестового эпика №1
    static int idTestSubtask3; // идентификатор тестовой подзадачи №1 тестового эпика №2
    static TaskManager manager = new TaskManager();

    public static void main(String[] args) {
        System.out.println("Поехали!");
        // 1. Создайте две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей.
        createTestTasks();

        // 2. Распечатайте списки эпиков, задач и подзадач через System.out.println(..).
        printTasks();

        // 3.1. Измените статусы созданных объектов, распечатайте их.
        changeAndPrintEpicsStatus();
        changeAndPrintTasksStatus();
        changeAndPrintSubtasksStatus();

        // 3.2. Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        printEpicsStatus();

        // 4.1. Удаление задачи
        System.out.println("\n----> Удаление тестовой задачи №1: id=" + idTestTask1);
        manager.deleteTask(idTestTask1);
        System.out.println(manager.getTasks());

        System.out.println("\n----> Удаление тестовой подзадачи №2: id=" + idTestSubtask2);
        manager.deleteSubtask(idTestSubtask2);
        System.out.println(manager.getSubtasks());
        printEpicsStatus();

        // 4.2. Удаление эпика
        System.out.println("\n----> Удаление тестового эпика id=" + idTestEpic1);
        manager.deleteEpic(idTestEpic1);
        System.out.println(manager.getEpics());

        // Удаление всех объектов
        System.out.println("\n----> Удаление всех подзадач");
        System.out.println("Проверка обновления статуса эпика:");
        manager.deleteSubtasks();
        System.out.println(manager.getEpics());
        System.out.println("\n----> Удаление всех созданных объектов");
        manager.deleteTasks();
        manager.deleteEpics();
        System.out.println(manager.getTasks());
        System.out.println(manager.getSubtasks());
        System.out.println(manager.getEpics());

    }

    public static void createTestTasks() {
        idTestTask1 = manager.addNewTask(new Task("Тестовая задача №1",
                "Проверить корректность создания задачи"));
        idTestTask2 = manager.addNewTask(new Task("Тестовая задача №2",
                "Повторно проверить корректность создания задачи"));
        idTestEpic1 = manager.addNewEpic(new Epic("Тестовый эпик №1",
                "Создать эпик с двумя подзадачами."));
        idTestSubtask1 = manager.addNewSubtask(new Subtask("Тестовая подзадача №1",
                "Создать тестовую подзадачу №1 для тестового эпика №1.", idTestEpic1));
        idTestSubtask2 = manager.addNewSubtask(new Subtask("Тестовая подзадача №2",
                "Создать тестовую подзадачу №2 для тестового эпика №1.", idTestEpic1));
        idTestEpic2 = manager.addNewEpic(new Epic("Тестовый эпик №2",
                "Создать эпик №2 с одной подзадачей."));
        idTestSubtask3 = manager.addNewSubtask(new Subtask("Тестовая подзадача №3",
                "Создать тестовую подзадачу для перевого тестового эпика №2.", idTestEpic2));
    }

    public static void printTasks() {
        System.out.println("\n----> Списки задач:");
        System.out.println(manager.getTasks());
        System.out.println("\n----> Списки эпиков:");
        System.out.println(manager.getEpics());
        System.out.println("\n----> Списки подзадач:");
        System.out.println(manager.getSubtasks());
    }

    public static void changeAndPrintEpicsStatus() {
        System.out.println("\n----> Попытка изменения статусов эпиков на DONE и IN_PROGRESS:");
        Epic copyOfEpic1 = manager.getEpic(idTestEpic1);
        Epic copyOfEpic2 = manager.getEpic(idTestEpic2);
        System.out.println("Текущий статус эпика №1: " + copyOfEpic1.getStatus());
        System.out.println("Текущий статус эпика №2: " + copyOfEpic2.getStatus());
        copyOfEpic1.setStatus(Status.DONE);
        copyOfEpic2.setStatus(Status.IN_PROGRESS);
        manager.updateEpic(copyOfEpic1);
        manager.updateEpic(copyOfEpic2);
        System.out.println("Новый статус эпика №1: " + manager.getEpic(idTestEpic1).getStatus());
        System.out.println("Новый статус эпика №2: " + manager.getEpic(idTestEpic2).getStatus());
    }

    public static void changeAndPrintTasksStatus() {
        System.out.println("\n----> Изменение статусов задач на DONE и IN_PROGRESS:");
        Task copyOfTask1 = manager.getTask(idTestTask1);
        Task copyOfTask2 = manager.getTask(idTestTask2);
        System.out.println("Текущий статус задачи №1: " + copyOfTask1.getStatus());
        System.out.println("Текущий статус задачи №2: " + copyOfTask2.getStatus());
        copyOfTask1.setStatus(Status.DONE);
        copyOfTask2.setStatus(Status.IN_PROGRESS);
        manager.updateTask(copyOfTask1);
        manager.updateTask(copyOfTask2);
        System.out.println("Новый статус задачи №1: " + manager.getTask(idTestTask1).getStatus());
        System.out.println("Новый статус задачи №2: " + manager.getTask(idTestTask2).getStatus());
    }

    public static void changeAndPrintSubtasksStatus() {
        System.out.println("\n----> Изменение статусов подзадач на DONE и IN_PROGRESS:");
        Subtask copyOfSubtask1 = manager.getSubtask(idTestSubtask1);
        Subtask copyOfSubtask2 = manager.getSubtask(idTestSubtask2);
        Subtask copyOfSubtask3 = manager.getSubtask(idTestSubtask3);
        System.out.println("Текущий статус подзадачи №1: " + copyOfSubtask1.getStatus());
        System.out.println("Текущий статус подзадачи №2: " + copyOfSubtask2.getStatus());
        System.out.println("Текущий статус подзадачи №3: " + copyOfSubtask3.getStatus());
        copyOfSubtask1.setStatus(Status.DONE);
        copyOfSubtask2.setStatus(Status.IN_PROGRESS);
        copyOfSubtask3.setStatus(Status.DONE);
        manager.updateSubtask(copyOfSubtask1);
        manager.updateSubtask(copyOfSubtask2);
        manager.updateSubtask(copyOfSubtask3);
        System.out.println("Новый статус подзадачи №1: " + manager.getSubtask(idTestSubtask1).getStatus());
        System.out.println("Новый статус подзадачи №2: " + manager.getSubtask(idTestSubtask2).getStatus());
        System.out.println("Новый статус подзадачи №3: " + manager.getSubtask(idTestSubtask3).getStatus());
    }

    public static void printEpicsStatus() {
        System.out.println("\n----> Проверка расчета статусов эпиков по статусам подзадач:");
        System.out.println("Новый статус эпика №1: " + manager.getEpic(idTestEpic1).getStatus());
        System.out.println("Новый статус эпика №2: " + manager.getEpic(idTestEpic2).getStatus());
    }


}
