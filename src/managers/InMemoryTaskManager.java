package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private int idCounter = 1; // Счетчик идентификаторов

    ////////////////////////////////////////// Методы Task'ов ///////////////////////////////////////////////////
    @Override
    public void createTask(Task task) { // Создание задачи
        task.setId(idCounter);
        tasks.put(task.getId(), task);
    }

    @Override
    public Task getTaskById(int id) { // Получить задачи по ID
        Task task = tasks.get(id);
        historyManager.addToHistory(task);
        return task;
    }

    @Override
    public List<Task> getAllTasks() { // Получить все задачи
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task updateTask(Task task) { // Изменение статуса Задачи
        tasks.put(task.getId(),task);

        return tasks.get(task.getId());
    }


    public void deleteTaskById(int id) { // Удалить задачу по ID
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllTasks() { //Удаление всех задач
        tasks.clear();
    }

    ////////////////////////////////////////// Методы Epic'ов /////////////////////////////////////////////////
    @Override
    public void createEpic(Epic epic) { // Создание Эпика
        epic.setId(idCounter);
        epics.put(epic.getId(), epic);
    }

    @Override
    public Epic getEpicById(int id) { // Получить Эпики по ID
        Epic epic = epics.get(id);
        historyManager.addToHistory(epic);
        return epics.get(id);
    }

    @Override
    public List<Epic> getAllEpics() { // Получить все эпики
        return new ArrayList<>(epics.values());
    }

    @Override
    public void updateEpic(Epic epic) { // Изменение статуса Эпика
        epics.put(epic.getId(), epic);
        epic.updateStatus(); // Обновляем статус эпика
    }

    @Override
    public void deleteEpicById(int id) { // Удаление Эпика по ID
        epics.remove(id);
        // Удаляем все подзадачи, связанные с этим эпиком
        for (Subtask subtask : getAllSubtasks()) {
            if (subtask.getEpic().getId() == id) {
                subtasks.remove(subtask.getId());
                historyManager.remove(id);
            }
        }
    }

    @Override
    public void deleteAllEpics() { //Удаление всех эпиков
        epics.clear();
        subtasks.clear();
    }

    //////////////////////////////////////// Методы Subtask'ов ////////////////////////////////////////////////
    @Override
    public void createSubtask(Subtask subtask) { // Создание подзадачи
        subtasks.put(subtask.getId(), subtask);
        // Добавление подзадачи к соответствующему эпику
        if (epics.containsKey(subtask.getEpic().getId())) {
            epics.get(subtask.getEpic().getId()).addSubtask(subtask);
            subtask.getEpic().updateStatus();
        }
    }

    @Override
    public Subtask getSubtaskById(int id) { // Получить подзадачи по ID
        Subtask subTask = subtasks.get(id);
        historyManager.addToHistory(subTask);
        return subTask;
    }

    @Override
    public List<Subtask> getAllSubtasks() { // Получить все подзадачи
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void updateSubtask(Subtask subtask) { // Изменение статуса подзадачи
        tasks.put(subtask.getId(), subtask);

        // Обновление статуса эпика, к которому принадлежит подзадача
        Epic epic = subtask.getEpic();
        if (epic != null) {
            epic.updateStatus(); // Обновление статуса эпика
        }
    }

    @Override
    public void deleteSubtaskById(int id) { // Удалить Подзадачу по ID
        subtasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllSubtasks() { //Удалить все подзадачи
        subtasks.clear();
        for (Epic epic : getAllEpics()) {
            epic.updateStatus();
        }
    }

//////////////////////////////////////////// Прочие Методы ////////////////////////////////////////////////
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    public int getNextId() { //Логика счетчика ID
        return idCounter++;
    }


    public List<Subtask> getSubtasksByEpicId(int epicId) { // Получение подзадачи по ID
        Epic epic = epics.get(epicId);
        return epic != null ? epic.getSubtasks() : new ArrayList<>();
    }


    private void deleteListSubTasks(List<Integer> subTaskIds) {
        for (Integer id : subTaskIds) {
            subtasks.remove(id);
        }
    }

    private List<Subtask> getListSubTasksByEpicId(List<Integer> subTaskIds) {
        List<Subtask> subTasks = new ArrayList<>();
        for (Integer id : subTaskIds) {
            subTasks.add(subtasks.get(id));
        }
        return subTasks;
    }

}
