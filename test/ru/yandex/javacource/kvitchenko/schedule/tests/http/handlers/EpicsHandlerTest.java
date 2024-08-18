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

public class EpicsHandlerTest {

    private static final String TEST_NAME_VALUE = "WDVNJIOHYVSDFGDFMKLHDJSBG5678";
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
    public void shouldHandleEpicsPath() throws IOException, InterruptedException {

        /* =============================================================================================================
         * Test 1: POST /epics should create new epic and return 201 status code
         * =============================================================================================================
         */
        URI urlWithoutId = URI.create("http://localhost:8080/epics");

        String taskJsonString = "{ \"type\": \"EPIC\", \"name\": \"" + TEST_NAME_VALUE + "\", \"status\": \"NEW\", "
                + "\"description\": \"Test epic description\", \"start time\": \"10.02.2022 09:00:00\", "
                + "\"duration\": 15 }";

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(urlWithoutId)
                .POST(HttpRequest.BodyPublishers.ofString(taskJsonString, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Server return unexpected status code after"
                + "request POST /epics");

        /* =============================================================================================================
         * Test 2: GET /epics should return epics list and status code 200
         * =============================================================================================================
         */
        request = HttpRequest
                .newBuilder()
                .uri(urlWithoutId)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                + "request GET /epics");

        String taskJsonStringFromServer = "";
        Pattern pattern = Pattern.compile("\\{[^\\{]*" + TEST_NAME_VALUE + "[^\\}]*\\}");
        Matcher matcher = pattern.matcher(response.body());
        if (matcher.find()) {
            taskJsonStringFromServer = matcher.group(0);
        }
        assertTrue(taskJsonStringFromServer.contains(TEST_NAME_VALUE), "Server does not return created epic"
                + " after request GET /epics");

        /* =============================================================================================================
         * Test 3: GET /epics/{id} should return code 200 if epic is exist
         * =============================================================================================================
         */
        JsonElement jsonElement = JsonParser.parseString(taskJsonStringFromServer);
        JsonObject object = jsonElement.getAsJsonObject();
        final int id = object.get("id").getAsInt();
        URI urlWithId = URI.create("http://localhost:8080/epics/" + id);

        request = HttpRequest
                .newBuilder()
                .uri(urlWithId)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                + "request GET /epics/" + id);

        /* =============================================================================================================
         * Test 4: GET /epics/{id}/subtasks should return code 200 if epic is exist
         * =============================================================================================================
         */
        URI urlForGetSubtasks = URI.create("http://localhost:8080/epics/" + id + "/subtasks");

        request = HttpRequest
                .newBuilder()
                .uri(urlForGetSubtasks)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                + "request GET /epics/" + id + "/subtasks");

        /* =============================================================================================================
         * Test 5: DELETE /epics/{id} should return code 200
         * =============================================================================================================
         */
        request = HttpRequest
                .newBuilder()
                .uri(urlWithId)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Server return unexpected status code after"
                + "request DELETE /epics/" + id);

        /* =============================================================================================================
         * Test 6: GET /epics/{id} should return code 404 if epic not exist
         * =============================================================================================================
         */
        request = HttpRequest
                .newBuilder()
                .uri(urlWithId)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Server return unexpected status code after"
                + "request GET /epics/" + id + ", when epic does not exists.");

        /* =============================================================================================================
         * Test 7: GET /epics/{id}/subtasks should return code 404 if epic not exist
         * =============================================================================================================
         */
        request = HttpRequest
                .newBuilder()
                .uri(urlForGetSubtasks)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Server return unexpected status code after"
                + "request GET /epics/" + id + "/subtasks, when epic does not exists.");

    }

}
