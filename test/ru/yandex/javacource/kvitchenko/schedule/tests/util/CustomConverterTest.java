package ru.yandex.javacource.kvitchenko.schedule.tests.util;

import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.kvitchenko.schedule.enums.Status;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;
import ru.yandex.javacource.kvitchenko.schedule.util.CustomConverter;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomConverterTest {

    static GsonBuilder builder = new GsonBuilder();

    @BeforeAll
    public static void registerAdapter() {
        builder.registerTypeAdapter(Task.class, new CustomConverter());
        builder.registerTypeAdapter(Subtask.class, new CustomConverter());
        builder.registerTypeAdapter(Epic.class, new CustomConverter());
    }

    @Test
    public void shouldReturnTaskInstance() {
        String taskJsonString = "{ \"id\": 1000, \"type\": \"TASK\", \"name\": \"Test task\", \"status\": \"NEW\", "
                + "\"description\": \"Test task description\", \"start time\": \"10.04.2024 09:22:00\", "
                + "\"duration\": 15 }";

        String targetString = "Task{id=1000, name='Test task', description='Test task description', status=NEW', "
                + "start=[10.04.2024 09:22:00'], duration = [15]}";

        Task task = builder.create().fromJson(taskJsonString, Task.class);

        assertEquals(task.toString(), targetString);
        assertEquals(task.getClass(), Task.class);
    }

    @Test
    public void shouldReturnJsonForTaskInstance() {
        Task task = new Task(
                1000,
                "Test task",
                "Test task description",
                Status.NEW,
                LocalDateTime.of(2024, 06, 20, 9, 0),
                Duration.ofMinutes(15)
        );

        String targetString = "{\"id\":1000,\"type\":\"TASK\",\"name\":\"Test task\",\"status\":\"NEW\","
                + "\"description\":\"Test task description\",\"start time\":\"20.06.2024 09:00:00\",\"duration\":15}";

        assertEquals(builder.create().toJson(task), targetString);
    }

    @Test
    public void shouldReturnSubtaskInstance() {
        String taskJsonString = "{ \"id\": 1000, \"type\": \"SUBTASK\", \"name\": \"Test subtask\", "
                + "\"status\": \"DONE\", \"description\": \"Test subtask description\", "
                + "\"start time\": \"10.04.2024 09:22:00\", \"duration\": 15, \"epic id\": 1 }";

        String targetString = "SubTask{id=1000, epicId='1', name='Test subtask', "
                + "description='Test subtask description', status=DONE, start=[10.04.2024 09:22:00'], duration = [15]}";

        Task task = builder.create().fromJson(taskJsonString, Subtask.class);

        assertEquals(task.toString(), targetString);
        assertEquals(task.getClass(), Subtask.class);
    }

    @Test
    public void shouldReturnJsonForSubtaskInstance() {
        Subtask subtask = new Subtask(
                1000,
                "Test subtask",
                "Test subtask description",
                Status.DONE,
                LocalDateTime.of(2024, 06, 20, 9, 0),
                Duration.ofMinutes(15),
                23
        );

        String targetString = "{\"id\":1000,\"type\":\"SUBTASK\",\"name\":\"Test subtask\",\"status\":\"DONE\","
                + "\"description\":\"Test subtask description\",\"start time\":\"20.06.2024 09:00:00\","
                + "\"duration\":15,\"epic id\":23}";

        assertEquals(builder.create().toJson(subtask), targetString);
    }

    @Test
    public void shouldReturnEpicInstance() {
        String taskJsonString = "{ \"id\": 1000, \"type\": \"EPIC\", \"name\": \"Test epic\", "
                + "\"status\": \"IN_PROGRESS\", \"description\": \"Test epic description\", "
                + "\"start time\": \"10.04.2024 09:22:00\", \"duration\": 15}";

        String targetString = "Epic{id=1000, name='Test epic', description='Test epic description', "
                + "status=IN_PROGRESS, subtasks=[], start=[10.04.2024 09:22:00'], duration = [15]}";

        Task task = builder.create().fromJson(taskJsonString, Epic.class);

        assertEquals(task.toString(), targetString);
        assertEquals(task.getClass(), Epic.class);
    }

    @Test
    public void shouldReturnJsonForEpicInstance() {
        Epic epic = new Epic(
                1000,
                "Test epic",
                "Test epic description",
                Status.DONE,
                LocalDateTime.of(2024, 06, 20, 9, 0),
                Duration.ofMinutes(15)
        );

        for (int i = 10; i < 20; i++) {
            epic.addSubtaskId(i);
        }

        String targetString = "{\"id\":1000,\"type\":\"EPIC\",\"name\":\"Test epic\",\"status\":\"DONE\","
                + "\"description\":\"Test epic description\",\"start time\":\"20.06.2024 09:00:00\",\"duration\":15,"
                + "\"subtasks\":[10,11,12,13,14,15,16,17,18,19]}";

        assertEquals(builder.create().toJson(epic), targetString);
    }

}
