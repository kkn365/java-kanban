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

public class SubtasksHandlerTest {

    private static final String TEST_SUBTASK_NAME_VALUE = "WDVNJIOHYVSDFGDFMKLHDJSBG91011";
    private static final String TEST_EPIC_NAME_VALUE = "WDVNJIOHYVSDFGDFMKLHDJSBG121314";
    private static final String TEST_STATUS_VALUE = "IN_PROGRESS";
    static HttpTaskServer taskServer = new HttpTaskServer();

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
    public void shouldHandleSubtasksPath() throws IOException, InterruptedException {

        /* =============================================================================================================
         * Creating test epic and get his id
         * =============================================================================================================
         */
        URI url = URI.create("http://localhost:8080/epics");

        String taskJsonString = "{ \"type\": \"EPIC\", \"name\": \""
                + TEST_EPIC_NAME_VALUE
                + "\", \"status\": \"NEW\", "
                + "\"description\": \"Test epic description\", \"start time\": \"10.03.2022 09:00:00\", "
                + "\"duration\": 15 }";

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJsonString, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Server return unexpected status code after"
                + "request POST /epics");

        request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                + "request GET /epics");

        String taskJsonStringFromServer = "";
        Pattern pattern = Pattern.compile("\\{[^\\{]*" + TEST_EPIC_NAME_VALUE + "[^\\}]*\\}");
        Matcher matcher = pattern.matcher(response.body());
        if (matcher.find()) {
            taskJsonStringFromServer = matcher.group(0);
        }
        assertTrue(taskJsonStringFromServer.contains(TEST_EPIC_NAME_VALUE),
                "Server does not return created epic after request GET /epics");

        JsonElement jsonElement = JsonParser.parseString(taskJsonStringFromServer);
        JsonObject object = jsonElement.getAsJsonObject();
        final int epicId = object.get("id").getAsInt();

        /* =============================================================================================================
         * Test 1: POST /subtasks (id not set) should create new subtask and return 201 status code
         * =============================================================================================================
         */
        URI urlWithoutId = URI.create("http://localhost:8080/subtasks");

        String subtaskJsonString = "{ \"type\": \"SUBTASK\", \"name\": \"" + TEST_SUBTASK_NAME_VALUE
                + "\", \"status\": \"NEW\", "
                + "\"description\": \"Test subtask description\", \"start time\": \"10.03.2024 10:00:00\", "
                + "\"duration\": 15, \"epic id\": "
                + epicId + " }";

        request = HttpRequest
                .newBuilder()
                .uri(urlWithoutId)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJsonString, StandardCharsets.UTF_8))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Server return unexpected status code after"
                + "request POST /subtasks (id not set).");

        /* =============================================================================================================
         * Test 2: POST /subtasks (id not set, time intersections) should return 406 status code
         * =============================================================================================================
         */
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Server return unexpected status code after"
                + "request POST /subtasks with time intersection in body.");

        /* =============================================================================================================
         * Test 3: GET /subtasks should return subtasks list and status code 200
         * =============================================================================================================
         */
        request = HttpRequest
                .newBuilder()
                .uri(urlWithoutId)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                + "request GET /subtasks");

        String subtaskJsonStringFromServer = "";
        Pattern subtaskPattern = Pattern.compile("\\{[^\\{]*" + TEST_SUBTASK_NAME_VALUE + "[^\\}]*\\}");
        Matcher subtaskMatcher = subtaskPattern.matcher(response.body());
        if (subtaskMatcher.find()) {
            subtaskJsonStringFromServer = subtaskMatcher.group(0).toString();
        }
        assertTrue(subtaskJsonStringFromServer.contains(TEST_SUBTASK_NAME_VALUE), "Server does not return "
                + "created task after request GET /subtasks");

        /* =============================================================================================================
         * Test 4: GET /tasks/{id} should return code 200 if task is exist
         * =============================================================================================================
         */
        jsonElement = JsonParser.parseString(subtaskJsonStringFromServer);
        object = jsonElement.getAsJsonObject();
        final int id = object.get("id").getAsInt();
        URI urlWithId = URI.create("http://localhost:8080/subtasks/" + id);

        request = HttpRequest
                .newBuilder()
                .uri(urlWithId)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                + "request GET /subtasks/" + id);

        /* =============================================================================================================
         * Test 5: POST /tasks (id is set) should update task and return 201 status code
         * =============================================================================================================
         */
        String updateSubtaskJsonString = "{ \"id\": " + id
                + ", \"type\": \"SUBTASK\", \"name\": \"" + TEST_SUBTASK_NAME_VALUE + "\", \"status\": \""
                + TEST_STATUS_VALUE + "\", "
                + "\"description\": \"Test task description\", \"start time\": \"10.03.2024 11:00:00\", "
                + "\"duration\": 15, \"epic id\": "
                + epicId + " }";

        request = HttpRequest
                .newBuilder()
                .uri(urlWithoutId)
                .POST(HttpRequest.BodyPublishers.ofString(updateSubtaskJsonString, StandardCharsets.UTF_8))
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Server return unexpected status code after"
                + "request POST /subtasks (id is set).");

        /* =============================================================================================================
         * Test 6: DELETE /subtasks/{id} should return code 200
         * =============================================================================================================
         */
        request = HttpRequest
                .newBuilder()
                .uri(urlWithId)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                + "request DELETE /subtasks/" + id);

        /* =============================================================================================================
         * Test 7: GET /subtasks/{id} should return code 404 if task is not exist
         * =============================================================================================================
         */
        request = HttpRequest
                .newBuilder()
                .uri(urlWithId)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Server return unexpected status code after"
                + "request GET /subtasks/" + id + " (not existed task).");

        /* =============================================================================================================
         * Epic deletion
         * =============================================================================================================
         */
        URI urlWithEpicId = URI.create("http://localhost:8080/epics/" + epicId);
        request = HttpRequest
                .newBuilder()
                .uri(urlWithEpicId)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                + "request DELETE /epics/" + epicId);

    }
}
