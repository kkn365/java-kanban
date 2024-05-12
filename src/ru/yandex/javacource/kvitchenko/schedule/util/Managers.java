package ru.yandex.javacource.kvitchenko.schedule.util;

import ru.yandex.javacource.kvitchenko.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.manager.FileBackedTaskManager;
import ru.yandex.javacource.kvitchenko.schedule.manager.InMemoryHistoryManager;
import ru.yandex.javacource.kvitchenko.schedule.manager.InMemoryTaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileManager() {
//        Path path = Paths.get(System.getProperty("java.io.tmpdir") + File.separator + "java-kanban.csv");
//        try {
//            this.filename = Files.createFile(path).toFile();
//        } catch (FileAlreadyExistsException e) {
//            this.filename = path.toFile();
//            FileBackedTaskManager.loadFromFile(filename);
//        } catch (IOException e) {
//            System.out.println("Произошла ошибка во время создания csv-файла.");
//            e.printStackTrace();
//        }
        //FileBackedTaskManager taskManager = new FileBackedTaskManager();
        return new FileBackedTaskManager();
    }

}
