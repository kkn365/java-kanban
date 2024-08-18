package ru.yandex.javacource.kvitchenko.schedule.tests.http.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TasksHandlerTest {

    static HttpTaskServer taskServer = new HttpTaskServer();
    private static final String TEST_NAME_VALUE = "WDVNJIOHYVSDFGDFMKLHDJSBG1234";
    private static final String TEST_STATUS_VALUE = "IN_PROGRESS";
    HttpClient client = HttpClient.newHttpClient();

    @BeforeAll
    public static void setUp() throws IOException {
        taskServer.start();
    }

    @AfterAll
    public static void shutDown() {
        taskServer.stop();
    }

    @Test
    public void shouldHandleTasksPath() throws IOException, InterruptedException {

        /* =============================================================================================================
         * Test 1: POST /tasks (id not set) should create new task and return 201 status code
         * =============================================================================================================
         */
        URI urlWithoutId = URI.create("http://localhost:8080/tasks");

        String taskJsonString = "{ \"type\": \"TASK\", \"name\": \"" + TEST_NAME_VALUE + "\", \"status\": \"NEW\", "
                + "\"description\": \"Test task description\", \"start time\": \"10.04.2024 09:00:00\", "
                + "\"duration\": 15 }";

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(urlWithoutId)
                .POST(HttpRequest.BodyPublishers.ofString(taskJsonString, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Server return unexpected status code after"
                + "request POST /tasks (id not set).");

        /* =============================================================================================================
         * Test 2: POST /tasks (id not set, time intersections) should return 406 status code
         * =============================================================================================================
         */
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Server return unexpected status code after"
                + "request POST /tasks with time intersection in body.");

        /* =============================================================================================================
         * Test 3: GET /tasks should return tasks list and status code 200
         * =============================================================================================================
         */
        request = HttpRequest
                .newBuilder()
                .uri(urlWithoutId)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                + "request GET /tasks");

        String taskJsonStringFromServer = "";
        Pattern pattern = Pattern.compile("\\{[^\\{]*" + TEST_NAME_VALUE + "[^\\}]*\\}");
        Matcher matcher = pattern.matcher(response.body());
        if (matcher.find()) {
            taskJsonStringFromServer = matcher.group(0);
        }
        assertTrue(taskJsonStringFromServer.contains(TEST_NAME_VALUE), "Server does not return created task"
                + " after request GET /tasks");

        /* =============================================================================================================
         * Test 4: GET /tasks/{id} should return code 200 if task is exist
         * =============================================================================================================
         */
        JsonElement jsonElement = JsonParser.parseString(taskJsonStringFromServer);
        JsonObject object = jsonElement.getAsJsonObject();
        final int id = object.get("id").getAsInt();
        URI urlWithId = URI.create("http://localhost:8080/tasks/" + id);

        request = HttpRequest
                .newBuilder()
                .uri(urlWithId)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                + "request GET /tasks/" + id);

        /* =============================================================================================================
         * Test 5: POST /tasks (id is set) should update task and return 201 status code
         * =============================================================================================================
         */
        String updateTaskJsonString = "{ \"id\": " + id + ", "
                + "\"type\": \"TASK\", \"name\": \"" + TEST_NAME_VALUE + "\", \"status\": \""
                + TEST_STATUS_VALUE + "\", "
                + "\"description\": \"Test task description\", \"start time\": \"10.04.2024 09:00:00\", "
                + "\"duration\": 15 }";

        request = HttpRequest
                .newBuilder()
                .uri(urlWithoutId)
                .POST(HttpRequest.BodyPublishers.ofString(updateTaskJsonString, StandardCharsets.UTF_8))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Server return unexpected status code after"
                + "request POST /tasks (id is set).");

        /* =============================================================================================================
         * Test 5: DELETE /tasks/{id} should return code 200
         * =============================================================================================================
         */
        request = HttpRequest
                .newBuilder()
                .uri(urlWithId)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                + "request DELETE /tasks/" + id);

        /* =============================================================================================================
         * Test 4: GET /tasks/{id} should return code 404 if task is not exist
         * =============================================================================================================
         */
        request = HttpRequest
                .newBuilder()
                .uri(urlWithId)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Server return unexpected status code after"
                + "request GET /tasks/" + id + " (not existed task).");

    }

}