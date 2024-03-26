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
    public Epic getCopy() {
        Epic cloneEpic = new Epic(super.getName(), super.getDescription());
        cloneEpic.setStatus(super.getStatus());
        cloneEpic.setId(super.getId());
        cloneEpic.subtasksIds = new ArrayList<>(this.subtasksIds);
        return cloneEpic;
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
