package ru.yandex.javacource.kvitchenko.schedule.task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
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
