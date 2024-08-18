package ru.yandex.javacource.kvitchenko.schedule.http.handlers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.kvitchenko.schedule.enums.Endpoint;
import ru.yandex.javacource.kvitchenko.schedule.exceptions.NotFoundException;
import ru.yandex.javacource.kvitchenko.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;

import java.io.IOException;
import java.io.InputStream;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final GsonBuilder builder;

    public EpicsHandler(TaskManager manager, GsonBuilder builder) {
        this.manager = manager;
        this.builder = builder;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        try {
            switch (endpoint) {
                case POST_EPIC: {
                    handlePostEpic(exchange);
                    break;
                }
                case GET_EPICS: {
                    handleGetEpics(exchange);
                    break;
                }
                case GET_EPIC: {
                    handleGetEpic(exchange);
                    break;
                }
                case GET_EPIC_SUBTASKS: {
                    handleGetEpicSubtasks(exchange);
                    break;
                }
                case DELETE_EPIC: {
                    handleDeleteEpic(exchange);
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

        if (requestPath.matches("^\\/epics(\\/)?$")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_EPIC;
            }
            return Endpoint.UNKNOWN;
        }

        if (requestPath.matches("^\\/epics\\/\\d+(\\/)?$")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPIC;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_EPIC;
            }
            return Endpoint.UNKNOWN;
        }

        if (requestPath.matches("^\\/epics\\/\\d+\\/subtasks(\\/)?$")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPIC_SUBTASKS;
            }
            return Endpoint.UNKNOWN;
        }

        return Endpoint.UNKNOWN;

    }

    private void handlePostEpic(HttpExchange exchange) throws IOException, JsonParseException,
            TaskValidationException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), super.getCharset());
        manager.addNewEpic(builder.create().fromJson(body, Epic.class));
        super.send201code(exchange);
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        super.sendText(exchange, builder.create().toJson(manager.getEpics()));
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.split("/")[2]);
        super.sendText(exchange, builder.create().toJson(manager.getEpic(id)));
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.split("/")[2]);
        super.sendText(exchange, builder.create().toJson(manager.getEpicSubtasks(id)));
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.split("/")[2]);
        manager.deleteEpic(id);
        super.send200code(exchange);
    }

}
