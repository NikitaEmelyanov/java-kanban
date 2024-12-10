import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idCounter = 1; // Счетчик идентификаторов

    public int getNextId() { //Логика счетчика ID
        return idCounter++;
    }

    public void createTask(Task task) { // Создание задачи
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) { // Создание Эпика
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        // Добавление подзадачи к соответствующему эпику
        if (epics.containsKey(subtask.getEpic().getId())) {
            epics.get(subtask.getEpic().getId()).addSubtask(subtask);
        }
    }

    public Task getTaskById(int id) { // Получить задачи по ID
        return tasks.get(id);
    }

    public Epic getEpicById(int id) { // ПОлучить Эпики по ID
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) { // Получить подзадачи по ID
        return subtasks.get(id);
    }

    public List<Task> getAllTasks() { // Получить все задачи
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() { // Получить все эпики
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() { // Получить все подзадачи
        return new ArrayList<>(subtasks.values());
    }

    public void deleteTaskById(int id) { // Удалить задачу по ID
        tasks.remove(id);
    }

    public void updateTask(Task task) { // Изменение статуса
        tasks.put(task.getId(), task);
        // Если задача обновляется, также нужно обновить статус эпика, если это подзадача
        if (task instanceof Subtask) {
            Epic epic = ((Subtask) task).getEpic();
            epic.updateStatus(); // Обновляем статус эпика
        }
    }

    public void deleteEpicById(int id) { // Удалени Эпика по ID
        epics.remove(id);
        // Удаляем все подзадачи, связанные с этим эпиком
        for (Subtask subtask : getAllSubtasks()) {
            if (subtask.getEpic().getId() == id) {
                subtasks.remove(subtask.getId());
            }
        }
    }

    public List<Subtask> getSubtasksByEpicId(int epicId) { // Получение подзадачи по ID
        Epic epic = epics.get(epicId);
        return epic != null ? epic.getSubtasks() : new ArrayList<>();
    }

    public void deleteAllTasks() { // Удаление всех задач
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }
}