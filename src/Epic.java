import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIds = new ArrayList<>();
    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtaskId(int id) {
        subtasksIds.add(id);
        updateStatus();
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    @Override
    public void setStatus(Status status) {
        updateStatus();
    }

    @Override
    public Status getStatus() {
        updateStatus();
        return super.getStatus();
    }

    public void updateStatus() {
        if(subtasksIds.isEmpty()) {
            super.setStatus(Status.NEW);
        } else {
            int statusCounterNew = 0;
            int statusCounterDone = 0;
            int subtasksCount = TaskManager.getSubTasks(super.getId()).size();
            for (Subtask subtask : TaskManager.getSubTasks(super.getId())) {
                switch (subtask.getStatus()){
                    case NEW:
                        statusCounterNew++;
                        break;
                    case DONE:
                        statusCounterDone++;
                        break;
                    default:
                        break;
                }
            }
            if(statusCounterNew == subtasksCount){
                super.setStatus(Status.NEW);
            } else if (statusCounterDone == subtasksCount) {
                super.setStatus(Status.DONE);
            } else {
                super.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + super.getId() +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasks=" + subtasksIds.toString() +
                '}';
    }

}
