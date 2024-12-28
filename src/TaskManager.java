import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    List<Task> getAllTasks();

    void updateTask(Task task);

    void deleteAllTasks();

    void createSubtask(Subtask subtask2);

    void createEpic(Epic epic1);

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    int getNextId();

    Task getTaskById(Integer id);

    List<Task> getHistory();

    void deleteSubtaskById(int id);

    Object getSubtaskById(int id);

    Object getEpicById(int id);

    List<Subtask> getSubtasksByEpicId(int id);
}
