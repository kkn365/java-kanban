package ru.yandex.javacource.kvitchenko.schedule.http.handlers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.kvitchenko.schedule.enums.Endpoint;
import ru.yandex.javacource.kvitchenko.schedule.interfaces.TaskManager;

import java.io.IOException;
import java.util.Objects;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final GsonBuilder builder;

    public HistoryHandler(TaskManager manager, GsonBuilder builder) {
        this.manager = manager;
        this.builder = builder;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        try {
            if (Objects.requireNonNull(endpoint) == Endpoint.GET_HISTORY) {
                handleGetHistory(exchange);
            } else {
                super.sendNotFound(exchange);
            }
        } catch (JsonParseException e) {
            super.sendNotFound(exchange);
        } catch (IOException e) {
            super.sendNotFound(exchange);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {

        if (requestPath.matches("^\\/history(\\/)?$") && requestMethod.equals("GET")) {
            return Endpoint.GET_HISTORY;
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        super.sendText(exchange, builder.create().toJson(manager.getHistory()));
    }

}

