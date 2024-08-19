package ru.yandex.javacource.kvitchenko.schedule.util;

import com.google.gson.*;
import ru.yandex.javacource.kvitchenko.schedule.enums.Status;
import ru.yandex.javacource.kvitchenko.schedule.enums.TaskType;
import ru.yandex.javacource.kvitchenko.schedule.task.Epic;
import ru.yandex.javacource.kvitchenko.schedule.task.Subtask;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomConverter<T extends Task> implements JsonSerializer<T>, JsonDeserializer<T> {

    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public JsonElement serialize(T task, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("id", task.getId());
        object.addProperty("type", task.getType().toString());
        object.addProperty("name", task.getName());
        object.addProperty("status", task.getStatus().toString());
        object.addProperty("description", task.getDescription());
        object.addProperty("start time", task.getStartTime().format(inputFormatter));
        object.addProperty("duration", task.getDuration().toMinutes());
        if (task instanceof Subtask) {
            object.addProperty("epic id", ((Subtask) task).getEpicId());
        }
        if (task instanceof Epic) {
            JsonElement jsonElement = JsonParser.parseString(((Epic) task).getSubtasksIds().toString());
            object.add("subtasks", jsonElement);
        }
        return object;
    }

    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        JsonObject object = jsonElement.getAsJsonObject();

        int taskId = 0;
        if (object.has("id")) {
            taskId = object.get("id").getAsInt();
        }
        TaskType taskType = TaskType.valueOf(object.get("type").getAsString());
        String taskName = object.get("name").getAsString();
        Status taskStatus = Status.valueOf(object.get("status").getAsString());
        String taskDescription = object.get("description").getAsString();
        LocalDateTime taskStartTime = LocalDateTime.parse(object.get("start time").getAsString(), inputFormatter);
        Duration taskDuration = Duration.ofMinutes(object.get("duration").getAsLong());

        T task;
        task = (T) switch (taskType) {
            case TASK -> new Task(taskId, taskName, taskDescription, taskStatus, taskStartTime, taskDuration);
            case SUBTASK -> new Subtask(taskId, taskName, taskDescription, taskStatus, taskStartTime, taskDuration,
                    object.get("epic id").getAsInt());
            case EPIC -> new Epic(taskId, taskName, taskDescription, taskStatus, taskStartTime, taskDuration);
        };
        return task;

    }

}
