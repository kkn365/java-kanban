import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private static final HashMap<Integer, Task> tasks = new HashMap<>();
    private static final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private static final HashMap<Integer, Epic> epics = new HashMap<>();


    public static ArrayList<Object> getTasks(TaskType type) {
        ArrayList<Object> tasksList = new ArrayList<>();
        switch (type) {
            case TASK:
                tasksList.addAll(tasks.values());
                break;
            case SUBTASK:
                tasksList.addAll(subtasks.values());
                break;
            case EPIC:
                tasksList.addAll(epics.values());
                break;
            default:
                break;
        }
        return tasksList;
    }

    public static ArrayList<Subtask> getSubTasks(Integer epicId) {
        ArrayList tasksList = new ArrayList();
        for (Integer subtask : subtasks.keySet()) {
            if (subtasks.get(subtask).getEpicId() == epicId) {
                tasksList.add(subtasks.get(subtask));
            }
        }
        return tasksList;
    }

    public void deleteTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.remove(task.getId());
        }
    }

    public void deleteTask(Subtask subtask) {
        subtasks.remove(subtask.getId());
    }

    public void deleteTask(Epic epic) {
        for (Integer subtaskId : epic.getSubtasksIds()) {
            deleteTask(subtasks.get(subtaskId));
        }
        epics.remove(epic.getId());
    }

    public void addTask(Task task) {
        task.setStatus(Status.NEW);
        tasks.put(task.getId(), task);
    }

    public void addTask(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void addTask(Subtask subtask) {
        epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
        subtask.setStatus(Status.NEW);
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).updateStatus();
    }

}
