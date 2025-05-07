package managers;

import exception.ManagerSaveException;
import exception.TimeOverlapException;
import tasks.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(
        Comparator.comparing(
            Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())
        )
    );
    private int idCounter = 1;

    // Методы для Task
    @Override
    public void createTask(Task task) throws TimeOverlapException, ManagerSaveException {
        if (task == null) return;
        if (hasTimeOverlapWithAny(task)) {
            throw new TimeOverlapException("Задача пересекается по времени с существующей");
        }
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
        addToPrioritized(task);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.addToHistory(task);
        }
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task updateTask(Task task) throws TimeOverlapException, ManagerSaveException {
        if (task == null || !tasks.containsKey(task.getId())) return null;

        Task existingTask = tasks.get(task.getId());
        if (!isTimeAvailableForUpdate(existingTask, task)) {
            throw new TimeOverlapException("Обновленная задача пересекается по времени с другими");
        }

        tasks.put(task.getId(), task);
        updateInPrioritized(existingTask, task);
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(task -> {
            prioritizedTasks.remove(task);
            historyManager.remove(task.getId());
        });
        tasks.clear();
    }

    // Методы для Epic
    @Override
    public void createEpic(Epic epic) {
        if (epic == null) return;
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.addToHistory(epic);
        }
        return epic;
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) return;
        epics.put(epic.getId(), epic);
        epic.updateStatus();
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            List<Subtask> epicSubtasks = getSubtasksByEpicId(id);
            epicSubtasks.forEach(subtask -> {
                subtasks.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
                historyManager.remove(subtask.getId());
            });
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllEpics() {
        subtasks.values().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        });
        epics.values().forEach(epic -> historyManager.remove(epic.getId()));
        epics.clear();
        subtasks.clear();
    }

    // Методы для Subtask
    @Override
    public void createSubtask(Subtask subtask) throws ManagerSaveException, TimeOverlapException {
        if (subtask == null) return;
        if (!epics.containsKey(subtask.getEpicId())) return;
        if (hasTimeOverlapWithAny(subtask)) {
            throw new TimeOverlapException("Подзадача пересекается по времени с существующей");
        }

        subtask.setId(idCounter++);
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).addSubtask(subtask);
        addToPrioritized(subtask);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.addToHistory(subtask);
        }
        return subtask;
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int id) {
        if (!epics.containsKey(id)) return new ArrayList<>();
        return epics.get(id).getSubtasks();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException, TimeOverlapException {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) return;

        Subtask existingSubtask = subtasks.get(subtask.getId());
        if (!isTimeAvailableForUpdate(existingSubtask, subtask)) {
            throw new TimeOverlapException("Обновленная подзадача пересекается по времени с другими");
        }

        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.updateStatus();
            epic.updateTime();
        }
        updateInPrioritized(existingSubtask, subtask);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
            }
        }
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.values().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
            }
        });
        subtasks.clear();
        epics.values().forEach(Epic::updateStatus);
    }

    // Общие методы
    @Override
    public int getNextId() {
        return idCounter++;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Новые методы для работы с временем
    private boolean hasTimeOverlap(Task task1, Task task2) {
        if (task1 == task2) return false;
        if (task1.getStartTime() == null || task2.getStartTime() == null) return false;

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    private boolean hasTimeOverlapWithAny(Task task) {
        return prioritizedTasks.stream()
            .filter(t -> t.getStartTime() != null)
            .anyMatch(existingTask -> hasTimeOverlap(task, existingTask));
    }

    private boolean isTimeAvailableForUpdate(Task existingTask, Task updatedTask) {
        if (updatedTask.getStartTime() == null) return true;

        Set<Task> otherTasks = new HashSet<>(prioritizedTasks);
        otherTasks.remove(existingTask);

        return otherTasks.stream()
            .filter(t -> t.getStartTime() != null)
            .noneMatch(t -> hasTimeOverlap(updatedTask, t));
    }

    private void addToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritized(Task task) {
        prioritizedTasks.remove(task);
    }

    private void updateInPrioritized(Task oldTask, Task newTask) {
        removeFromPrioritized(oldTask);
        addToPrioritized(newTask);
    }
    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> prioritizedList = new ArrayList<>();

        // Добавляем задачи с установленным временем начала
        prioritizedTasks.forEach(prioritizedList::add);

        // Добавляем задачи без времени начала (они будут в конце списка)
        tasks.values().stream()
            .filter(task -> task.getStartTime() == null)
            .forEach(prioritizedList::add);

        subtasks.values().stream()
            .filter(subtask -> subtask.getStartTime() == null)
            .forEach(prioritizedList::add);

        return prioritizedList;
    }


}