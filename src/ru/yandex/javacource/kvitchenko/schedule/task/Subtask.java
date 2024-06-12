package ru.yandex.javacource.kvitchenko.schedule.task;

import ru.yandex.javacource.kvitchenko.schedule.enums.Status;
import ru.yandex.javacource.kvitchenko.schedule.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(int id,
                   String name,
                   String description,
                   Status status,
                   LocalDateTime startTime,
                   Duration duration,
                   int epicId
    ) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public Subtask copy() {
        return new Subtask(
                this.getId(),
                this.getName(),
                this.getDescription(),
                this.getStatus(),
                this.getStartTime(),
                this.getDuration(),
                this.getEpicId()
        );
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + super.getId() +
                ", epicId='" + epicId + '\'' +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", start=[" + super.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + '\'' +
                "], duration = [" + super.getDuration().toMinutes() + "]" +
                '}';
    }

}
