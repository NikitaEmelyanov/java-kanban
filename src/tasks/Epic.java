package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Subtask> subtasks;

    public Epic(String name, String description, int id) {
        super(name, description, id);
        this.subtasks = new ArrayList<>();
    }

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
    }


    public void addSubtask(Subtask subtask) { // Добавить подзадачу
        subtasks.add(subtask);
    }

    public List<Subtask> getSubtasks() { // Получить список подзадач
        return subtasks;
    }


    @Override
    public String serializeToCsv() {
        return String.format("%s,%s,%s,%s,%s,%s\n", getId(), TaskType.EPIC, getName(), getStatus(),
            getDescription(), subtasks);
    }

    public void updateStatus() { // Изменить статус эпика
        if (subtasks.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }

        boolean hasInProgress = false;
        boolean hasDone = false;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                hasInProgress = true;
                break; // Выходим из цикла, если нашли первую подзадачу в статусе IN_PROGRESS
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                hasDone = true;
            }
        }

        if (hasInProgress) { // Обновляем статус эпика на основе найденных подзадач
            setStatus(TaskStatus.IN_PROGRESS);
        } else if (hasDone) {
            setStatus(TaskStatus.DONE);
        } else {
            setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public String toString() {
        return "Epics{" +
               ", name='" + getName() + '\'' +
               ", id=" + getId() +
               ", status=" + getStatus() +
               '}';
    }

    public void setName(String updatedEpic) {
        this.name = name;
    }
}