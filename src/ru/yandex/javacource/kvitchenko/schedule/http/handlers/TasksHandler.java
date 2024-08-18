package ru.yandex.javacource.kvitchenko.schedule.http.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.kvitchenko.schedule.enums.Endpoint;
import ru.yandex.javacource.kvitchenko.schedule.exceptions.NotFoundException;
import ru.yandex.javacource.kvitchenko.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import java.io.IOException;
import java.io.InputStream;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final GsonBuilder builder;

    public TasksHandler(TaskManager manager, GsonBuilder builder) {
        this.manager = manager;
        this.builder = builder;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        try {
            switch (endpoint) {
                case GET_TASKS: {
                    handleGetTasks(exchange);
                    break;
                }
                case GET_TASK: {
                    handleGetTask(exchange);
                    break;
                }
                case POST_TASK: {
                    handlePostTask(exchange);
                    break;
                }
                case DELETE_TASK: {
                    handleDeleteTask(exchange);
                    break;
                }
                default:
                    super.sendNotFound(exchange);
            }
        } catch (JsonParseException e) {
            super.sendNotFound(exchange);
        } catch (NotFoundException e) {
            super.sendNotFound(exchange);
        } catch (TaskValidationException e) {
            super.sendHasIntersections(exchange);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {

        if (requestPath.matches("^\\/tasks(\\/)?$")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_TASK;
            }
            return Endpoint.UNKNOWN;
        }

        if (requestPath.matches("^\\/tasks\\/\\d+(\\/)?$")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASK;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_TASK;
            }
            return Endpoint.UNKNOWN;
        }

        return Endpoint.UNKNOWN;

    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        super.sendText(exchange, builder.create().toJson(manager.getTasks()));
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.split("/")[2]);
        super.sendText(exchange, builder.create().toJson(manager.getTask(id)));
    }

    private void handlePostTask(HttpExchange exchange) throws IOException, JsonParseException,
            TaskValidationException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), super.getCharset());
        JsonElement jsonElement = JsonParser.parseString(body);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has("id")) {
            manager.updateTask(builder.create().fromJson(body, Task.class));
        } else {
            manager.addNewTask(builder.create().fromJson(body, Task.class));
        }
        super.send201code(exchange);
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.split("/")[2]);
        manager.deleteTask(id);
        super.send200code(exchange);
    }

}
