package ru.yandex.javacource.kvitchenko.schedule.task;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        status = Status.NEW;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /*
     * Попытка выполнить требования ТЗ:
     * 1) Пользователь не должен иметь возможности поменять статус эпика самостоятельно.
     * ... не существует отдельного метода, который занимался бы только обновлением статуса задачи.
     * Вместо этого статус задачи обновляется вместе с полным обновлением задачи.
     *
     * Предлагаемое решение: передача пользователю копии объекта, с последующим обновлением через методы update...
     * в менеджере.
     */
    public Task getCopy() {
        Task cloneTask = new Task(this.name, this.description);
        cloneTask.status = this.status;
        cloneTask.id = this.id;
        return cloneTask;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
