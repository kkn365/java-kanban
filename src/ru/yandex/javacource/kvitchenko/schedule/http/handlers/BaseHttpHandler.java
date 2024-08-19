package ru.yandex.javacource.kvitchenko.schedule.http.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {

    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public Charset getCharset() {
        return DEFAULT_CHARSET;
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void send200code(HttpExchange h) throws IOException {
        h.sendResponseHeaders(200, 0);
        h.close();
    }

    protected void send201code(HttpExchange h) throws IOException {
        h.sendResponseHeaders(201, 0);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        h.sendResponseHeaders(404, 0);
        h.close();
    }

    protected void sendHasIntersections(HttpExchange h) throws IOException {
        h.sendResponseHeaders(406, 0);
        h.close();
    }


}