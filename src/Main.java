public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        System.out.println("Поехали!");

        // 1. Создайте две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей.
        Task testTask1 = new Task("Тестовая задача №1",
                "Проверить корректность создания задачи");
        Task testTask2 = new Task ("Тестовая задача №2",
                "Повторно проверить корректность создания задачи");
        manager.addTask(testTask1);
        manager.addTask(testTask2);
        Epic testEpic1 = new Epic("Тестовый эпик №1", "Создать эпик с двумя подзадачами.");
        manager.addTask(testEpic1);
        Subtask testSubtask1 = new Subtask("Тестовая подзадача №1",
                "Создать тестовую подзадачу №1 для тестового эпика №1.", testEpic1.getId());
        Subtask testSubtask2 = new Subtask("Тестовая подзадача №2",
                "Создать тестовую подзадачу №2 для тестового эпика №1.", testEpic1.getId());
        manager.addTask(testSubtask1);
        manager.addTask(testSubtask2);
        Epic testEpic2 = new Epic("Тестовый эпик №2", "Создать эпик №2 с одной подзадачей.");
        manager.addTask(testEpic2);
        Subtask testSubtask3 = new Subtask("Тестовая подзадача №3",
                "Создать тестовую подзадачу для перевого тестового эпика №2.", testEpic2.getId());
        manager.addTask(testSubtask3);

        // 2. Распечатайте списки эпиков, задач и подзадач через System.out.println(..).
        System.out.println("\n----> Списки эпиков:");
        System.out.println(testEpic1);
        System.out.println(testEpic2);
        System.out.println("\n----> Списки задач:");
        System.out.println(testTask1);
        System.out.println(testTask2);
        System.out.println("\n----> Списки подзадач:");
        System.out.println(testSubtask1);
        System.out.println(testSubtask2);
        System.out.println(testSubtask3);

        // 3.1. Измените статусы созданных объектов, распечатайте их.
        System.out.println("\n----> Попытка изменения статусов эпиков на DONE и IN_PROGRESS:");
        System.out.println("Текущий статус эпика №1: " + testEpic1.getStatus());
        System.out.println("Текущий статус эпика №2: " + testEpic2.getStatus());
        testEpic1.setStatus(Status.DONE);
        testEpic2.setStatus(Status.IN_PROGRESS);
        System.out.println("Новый статус эпика №1: " + testEpic1.getStatus());
        System.out.println("Новый статус эпика №2: " + testEpic2.getStatus());

        System.out.println("\n----> Изменение статусов задач на DONE и IN_PROGRESS:");
        System.out.println("Текущий статус задачи №1: " + testTask1.getStatus());
        System.out.println("Текущий статус задачи №2: " + testTask2.getStatus());
        testTask1.setStatus(Status.DONE);
        testTask2.setStatus(Status.IN_PROGRESS);
        System.out.println("Новый статус задачи №1: " + testTask1.getStatus());
        System.out.println("Новый статус задачи №2: " + testTask2.getStatus());

        System.out.println("\n----> Изменение статусов подзадач на DONE и IN_PROGRESS:");
        System.out.println("Текущий статус подзадачи №1: " + testSubtask1.getStatus());
        System.out.println("Текущий статус подзадачи №2: " + testSubtask2.getStatus());
        System.out.println("Текущий статус подзадачи №3: " + testSubtask3.getStatus());
        testSubtask1.setStatus(Status.DONE);
        testSubtask2.setStatus(Status.IN_PROGRESS);
        testSubtask3.setStatus(Status.DONE);
        System.out.println("Новый статус подзадачи №1: " + testSubtask1.getStatus());
        System.out.println("Новый статус подзадачи №2: " + testSubtask2.getStatus());
        System.out.println("Новый статус подзадачи №3: " + testSubtask3.getStatus());

        // 3.2. Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        System.out.println("\n----> Проверка расчета статусов эпиков по статусам подзадач:");
        System.out.println("Новый статус эпика №1: " + testEpic1.getStatus());
        System.out.println("Новый статус эпика №2: " + testEpic2.getStatus());

        // 4.1. Удаление задачи
        System.out.println("\n----> Удаление тестовой задачи №1: id=" + testTask1.getId());
        manager.deleteTask(testTask1);
        System.out.println(manager.getTasks(TaskType.TASK));

        // 4.2. Удаление эпика
        System.out.println("\n----> Удаление тестового эпика id=" + testEpic1.getId());
        manager.deleteTask(testEpic1);
        System.out.println(manager.getTasks(TaskType.EPIC));
        System.out.println(manager.getTasks(TaskType.SUBTASK));

    }
}
