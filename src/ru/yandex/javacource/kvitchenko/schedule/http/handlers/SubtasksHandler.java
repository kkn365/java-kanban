package ru.yandex.javacource.kvitchenko.schedule.http.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.kvitchenko.schedule.enums.Endpoint;
import ru.yandex.javacource.kvitchenko.schedule.exceptions.NotFoundException;
import ru.yandex.javacource.kvitchenko.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;

import java.io.IOException;
import java.io.InputStream;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final GsonBuilder builder;

    public SubtasksHandler(TaskManager manager, GsonBuilder builder) {
        this.manager = manager;
        this.builder = builder;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        try {
            switch (endpoint) {
                case GET_SUBTASKS: {
                    handleGetSubtasks(exchange);
                    break;
                }
                case GET_SUBTASK: {
                    handleGetSubtask(exchange);
                    break;
                }
                case POST_SUBTASK: {
                    handlePostSubtask(exchange);
                    break;
                }
                case DELETE_SUBTASK: {
                    handleDeleteSubtask(exchange);
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

        if (requestPath.matches("^\\/subtasks(\\/)?$")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_SUBTASK;
            }
            return Endpoint.UNKNOWN;
        }

        if (requestPath.matches("^\\/subtasks\\/\\d+(\\/)?$")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASK;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_SUBTASK;
            }
            return Endpoint.UNKNOWN;
        }

        return Endpoint.UNKNOWN;

    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        super.sendText(exchange, builder.create().toJson(manager.getSubtasks()));
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.split("/")[2]);
        super.sendText(exchange, builder.create().toJson(manager.getSubtask(id)));
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException, JsonParseException,
            TaskValidationException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), super.getCharset());
        JsonElement jsonElement = JsonParser.parseString(body);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has("id")) {
            manager.updateSubtask(builder.create().fromJson(body, Subtask.class));
        } else {
            manager.addNewSubtask(builder.create().fromJson(body, Subtask.class));
        }
        super.send201code(exchange);
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.split("/")[2]);
        manager.deleteSubtask(id);
        super.send200code(exchange);
    }

}
