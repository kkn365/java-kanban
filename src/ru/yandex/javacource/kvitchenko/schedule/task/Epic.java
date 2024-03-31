package ru.yandex.javacource.kvitchenko.schedule.task;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtaskId(int id) {
        subtasksIds.add(id);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void removeSubtask(int id) {
        subtasksIds.remove(subtasksIds.indexOf(id));
    }

    @Override
    public Epic copy() {
        Epic copyEpic = new Epic(this.getName(), this.getDescription());
        copyEpic.setId(this.getId());
        copyEpic.setStatus(this.getStatus());
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
                '}';
    }

}
