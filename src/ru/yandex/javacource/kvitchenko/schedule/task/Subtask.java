package ru.yandex.javacource.kvitchenko.schedule.task;

import ru.yandex.javacource.kvitchenko.schedule.enums.Status;
import ru.yandex.javacource.kvitchenko.schedule.enums.TaskType;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public Subtask copy() {
        Subtask copySubtask = new Subtask(this.getName(), this.getDescription(), this.epicId);
        copySubtask.epicId = this.epicId;
        copySubtask.setStatus(this.getStatus());
        copySubtask.setId(this.getId());
        return copySubtask;
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
                '}';
    }

}
