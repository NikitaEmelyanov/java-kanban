package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

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

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    List<Subtask> getSubtasksByEpicId(int id);


}
