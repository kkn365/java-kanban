package ru.yandex.javacource.kvitchenko.schedule.tests.http.handlers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.HttpTaskServer;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryHandlerTest {
    private static final String TEST_TASK1_NAME_VALUE = "TESTHISTORYMANAGERTASK1";
    private static final String TEST_TASK2_NAME_VALUE = "TESTHISTORYMANAGERTASK2";
    private static final String TEST_TASK3_NAME_VALUE = "TESTHISTORYMANAGERTASK3";

    HttpClient client = HttpClient.newHttpClient();

    @BeforeAll
    public static void setUp() throws IOException {
        HttpTaskServer.start();
    }

    @AfterAll
    public static void shutDown() {
        HttpTaskServer.stop();
    }

    @Test
    void shouldHandleHistoryPath() throws IOException, InterruptedException {

        /* =============================================================================================================
         * Create first task and get its id
         * =============================================================================================================
         */
        URI urlWithoutId = URI.create("http://localhost:8080/tasks");
        String taskJsonString;
        HttpRequest request;
        HttpResponse<String> response;
        List<Integer> testTasksIds = new ArrayList<>();

        Map<String, String> tasksDataMap = new HashMap<>();
        tasksDataMap.put(TEST_TASK1_NAME_VALUE, "10.10.2024 09:00:00");
        tasksDataMap.put(TEST_TASK2_NAME_VALUE, "10.10.2024 09:30:00");
        tasksDataMap.put(TEST_TASK3_NAME_VALUE, "10.10.2024 10:00:00");

        for (Map.Entry<String, String> entry : tasksDataMap.entrySet()) {
            taskJsonString = "{ \"type\": \"TASK\", \"name\": \"" + entry.getKey()
                    + "\", \"status\": \"NEW\", \"description\": \"Test task description\", "
                    + "\"start time\": \"" + entry.getValue() + "\", \"duration\": 15 }";
            request = HttpRequest
                    .newBuilder()
                    .uri(urlWithoutId)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJsonString, StandardCharsets.UTF_8))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        request = HttpRequest
                .newBuilder()
                .uri(urlWithoutId)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                + "request GET /tasks");

        String taskJsonStringFromServer;
        Pattern pattern;
        Matcher matcher;
        JsonElement jsonElement;
        JsonObject object;
        for (String key : tasksDataMap.keySet()) {
            pattern = Pattern.compile("\\{[^\\{]*" + key + "[^\\}]*\\}");
            matcher = pattern.matcher(response.body());
            if (matcher.find()) {
                taskJsonStringFromServer = matcher.group(0).toString();
                jsonElement = JsonParser.parseString(taskJsonStringFromServer);
                object = jsonElement.getAsJsonObject();
                int id = object.get("id").getAsInt();
                testTasksIds.add(id);
            }
        }

        /* =============================================================================================================
         * Get tasks in reverse order
         * =============================================================================================================
         */
        URI urlWithId;
        for (Integer id : testTasksIds) {
            urlWithId = URI.create("http://localhost:8080/tasks/" + id);
            request = HttpRequest
                    .newBuilder()
                    .uri(urlWithId)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                    + "request GET /tasks/" + id);
        }

        /* =============================================================================================================
         * Test 1: GET /history should return tasks list in reverse order and status code 200
         * =============================================================================================================
         */
        URI url = URI.create("http://localhost:8080/history");
        request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                + "request GET /history");

        GsonBuilder builder = HttpTaskServer.getGson();
        Type listType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksList = builder.create().fromJson(response.body(), listType);

        for (Task task : tasksList) {
            assertEquals(task.getId(), testTasksIds.getLast(), "History manager return unexpected value");
            testTasksIds.removeLast();
        }

        /* =============================================================================================================
         * Remove tasks
         * =============================================================================================================
         */
        for (Task task : tasksList) {
            urlWithId = URI.create("http://localhost:8080/tasks/" + task.getId());
            request = HttpRequest
                    .newBuilder()
                    .uri(urlWithId)
                    .DELETE()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                    + "request DELETE /tasks/" + task.getId());
        }

    }

}
