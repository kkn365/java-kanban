package ru.yandex.javacource.kvitchenko.schedule;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacource.kvitchenko.schedule.http.handlers.*;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;
import ru.yandex.javacource.kvitchenko.schedule.util.CustomConverter;
import ru.yandex.javacource.kvitchenko.schedule.util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static HttpServer httpServer;

    static TaskManager taskManager = Managers.getDefault();

    static GsonBuilder builder = new GsonBuilder();

     public static GsonBuilder getGson() {
        return builder;
    }

    public static void main(String[] args) throws IOException {
        start();
    }

    public static void start() throws IOException {
        builder.registerTypeAdapter(Task.class, new CustomConverter());
        builder.registerTypeAdapter(Subtask.class, new CustomConverter());
        builder.registerTypeAdapter(Epic.class, new CustomConverter());
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager, builder));
        httpServer.createContext("/epics", new EpicsHandler(taskManager, builder));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager, builder));
        httpServer.createContext("/history", new HistoryHandler(taskManager,builder));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, builder));
        httpServer.start();
        //System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public static void stop() {
        httpServer.stop(1);
        //System.out.println("HTTP-сервер на порту " + PORT + " остановлен!");
    }
}