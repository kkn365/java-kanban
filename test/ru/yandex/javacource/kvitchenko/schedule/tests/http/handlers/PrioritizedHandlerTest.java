package ru.yandex.javacource.kvitchenko.schedule.tests.http.handlers;

import com.google.gson.GsonBuilder;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHandlerTest {

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

        Map<String, String> tasksDataMap = new HashMap<>();
        tasksDataMap.put(TEST_TASK1_NAME_VALUE, "10.11.2020 09:00:00");
        tasksDataMap.put(TEST_TASK2_NAME_VALUE, "10.11.2020 09:30:00");
        tasksDataMap.put(TEST_TASK3_NAME_VALUE, "10.11.2020 10:00:00");

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

        /* =============================================================================================================
         * Test 1: GET /prioritized should return list task with earlier start time in head and status code 200
         * =============================================================================================================
         */
        URI url = URI.create("http://localhost:8080/prioritized");
        request = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                + "request GET /prioritized");

        GsonBuilder builder = HttpTaskServer.getGson();
        Type listType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksList = builder.create().fromJson(response.body(), listType);

        LocalDateTime earlierStartTime = LocalDateTime.now();
        for (Task task : tasksList) {
            if (task.getStartTime().isBefore(earlierStartTime)) {
                earlierStartTime = task.getStartTime();
            }
        }

        assertEquals(earlierStartTime, tasksList.getFirst().getStartTime(),
                "Tasks priority not match");

        /* =============================================================================================================
         * Remove tasks
         * =============================================================================================================
         */
        URI urlWithId;
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
