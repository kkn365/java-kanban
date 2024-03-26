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

    public int getEpicId() {
        return epicId;
    }

    @Override
    public Subtask getCopy() {
        Subtask cloneSubtask = new Subtask(super.getName(), super.getDescription(), this.epicId);
        cloneSubtask.setStatus(super.getStatus());
        cloneSubtask.setId(super.getId());
        return cloneSubtask;
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
