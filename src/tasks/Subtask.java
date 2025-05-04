package tasks;

public class Subtask extends Task {

    private Epic epic; // Эпик, к которому принадлежит подзадача
    private int epicId;

    public Subtask(String name, String description, int id, Epic epic) {
        super(name, description, id);
        this.epic = epic;
        this.epicId = epicId;
    }

    public Subtask(Epic epic, String taskName, String description, TaskStatus taskStatus,
        Epic epic1) {
        super(taskName, description, taskStatus);
        this.epicId = epic.getId();
        this.epic = epic1;
    }

    public Subtask(int id, int epicId, String name, String description, TaskStatus taskStatus) {
        super(id, name, description, taskStatus);
        this.epicId = epicId;
    }

    @Override
    public String serializeToCsv() {
        return String.format("%s,%s,%s,%s,%s,%s\n", getId(), TaskType.SUBTASK, getName(),
            getStatus(), getDescription(), getEpic());
    }

    @Override
    public String toString() {
        return "Subtasks{" +
               ", name='" + getName() + '\'' +
               ", id=" + getId() +
               ", status=" + getStatus() +
               '}';
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

}
