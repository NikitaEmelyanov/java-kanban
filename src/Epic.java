import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks;

    public Epic(String name, String description, int id) {
        super(name, description, id);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) { // Добавить подзадачу
        subtasks.add(subtask);
    }

    public List<Subtask> getSubtasks() { // Получить список подзадач
        return subtasks;
    }

    public void updateStatus() { // Изменить статус подзадачи
        if (subtasks.isEmpty() || subtasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.NEW)) {
            setStatus(TaskStatus.NEW);
        } else if (subtasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE)) {
            setStatus(TaskStatus.DONE);
        } else {
            setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}