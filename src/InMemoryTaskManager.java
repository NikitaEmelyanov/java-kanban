import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idCounter = 1; // Счетчик идентификаторов

    private HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager(InMemoryHistoryManager inMemoryHistoryManager) {
        this.historyManager = historyManager;
    }

    public int getNextId() { //Логика счетчика ID
        return idCounter++;
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = tasks.get(id);
        Task taskForHistory = new Task(task.getName(), task.getDescription(), task.getId(), task.getStatus());
        return tasks.get(id);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }


    @Override
    public Task createTask(Task task) { // Создание задачи
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void createEpic(Epic epic) { // Создание Эпика
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        // Добавление подзадачи к соответствующему эпику
        if (epics.containsKey(subtask.getEpic().getId())) {
            epics.get(subtask.getEpic().getId()).addSubtask(subtask);
            subtask.getEpic().updateStatus();

        }
    }

    public Task getTaskById(int id) { // Получить задачи по ID
        return tasks.get(id);
    }

    public Epic getEpicById(int id) { // Получить Эпики по ID
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) { // Получить подзадачи по ID
        return subtasks.get(id);
    }

    @Override
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

    public void deleteSubtaskById(int id) { // Удалить Подзадачу по ID
        subtasks.remove(id);
    }

    @Override
    public void updateTask(Task task) { // Изменение статуса Задачи
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) { // Изменение статуса Эпика
        epics.put(epic.getId(), epic);
        epic.updateStatus(); // Обновляем статус эпика
    }

    public void updateSubtask(Subtask subtask) { // Изменение статуса подзадачи
        tasks.put(subtask.getId(), subtask);

        // Обновление статуса эпика, к которому принадлежит подзадача
        Epic epic = subtask.getEpic();
        if (epic != null) {
            epic.updateStatus(); // Обновление статуса эпика
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

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : getAllEpics()) {
            epic.updateStatus();
        }
    }
}