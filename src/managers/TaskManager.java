package managers;

import exception.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    void createTask(Task task) throws ManagerSaveException;

    Task getTaskById(int id);

    List<Task> getAllTasks();

    Task updateTask(Task task) throws ManagerSaveException;

    void deleteTaskById(int id);

    void deleteAllTasks();

    void createEpic(Epic epic1);

    Epic getEpicById(int id);

    List<Epic> getAllEpics();

    void updateEpic(Epic epic);

    void deleteEpicById(int id);

    void deleteAllEpics();

    void createSubtask(Subtask subtask2) throws ManagerSaveException;

    Subtask getSubtaskById(int id);

    List<Subtask> getSubtasksByEpicId(int id);

    List<Subtask> getAllSubtasks();

    void updateSubtask(Subtask subtask) throws ManagerSaveException;

    void deleteSubtaskById(int id);

    void deleteAllSubtasks();

    int getNextId();

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
