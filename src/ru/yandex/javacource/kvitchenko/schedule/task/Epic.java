package ru.yandex.javacource.kvitchenko.schedule.task;

import ru.yandex.javacource.kvitchenko.schedule.enums.Status;
import ru.yandex.javacource.kvitchenko.schedule.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Epic extends Task {

    private LocalDateTime endTime;
    private ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        if (startTime != null && duration != null) {
            this.endTime = startTime.plus(Duration.from(duration));
        }
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void addSubtaskId(int id) {
        if (!subtasksIds.contains(id)) {
            subtasksIds.add(id);
        }
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void removeSubtask(int id) {
        subtasksIds.remove(subtasksIds.indexOf(id));
    }

    @Override
    public Epic copy() {
        Epic copyEpic = new Epic(
                this.getId(),
                this.getName(),
                this.getDescription(),
                this.getStatus(),
                this.getStartTime(),
                this.getDuration()
        );
        copyEpic.subtasksIds = this.getSubtasksIds();
        return copyEpic;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + super.getId() +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasks=" + subtasksIds +
                ", start=[" + super.getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + '\'' +
                "], duration = [" + super.getDuration().toMinutes() + "]" +
                '}';
    }

}
