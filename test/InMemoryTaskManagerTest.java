import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void init() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        task = new Task("Test tasks.Task", "Description", taskManager.getNextId(), TaskStatus.NEW);
        epic = new Epic("Test tasks.Epic", "tasks.Epic Description", taskManager.getNextId());
        subtask = new Subtask("Test tasks.Subtask", "tasks.Subtask Description", taskManager.getNextId(), epic);
    }

    @Test
    void testCreateTask() {
        taskManager.createTask(task);
        assertEquals(task, taskManager.getTaskById(task.getId()));

    }

    @Test
    void testGetTaskById() {
        taskManager.createTask(task);
        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertNull(taskManager.getTaskById(11)); // Не существующий ID
    }

    @Test
    void testGetAllTasks() {
        taskManager.createTask(task);
        assertEquals(1, taskManager.getAllTasks().size());
    }

    @Test
    void testDeleteTaskById() {
        taskManager.createTask(task);
        taskManager.deleteTaskById(task.getId());
        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void testCreateEpic() {
        taskManager.createEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void testCreateSubtask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void testGetSubtasksByEpicId() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.get(0));
    }

    @Test
    void testDeleteSubtaskById() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtaskById(subtask.getId());
        assertNull(taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void testUpdateTask() {
        taskManager.createTask(task);
        Task updatedTask = new Task("Updated tasks.Task", "Updated Description", task.getId(), TaskStatus.DONE);
        taskManager.updateTask(updatedTask);
        assertEquals(updatedTask, taskManager.getTaskById(task.getId()));
    }

    @Test
    void testDeleteAllTasks() {
        taskManager.createTask(task);
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    void testDeleteEpicById() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.deleteEpicById(epic.getId());
        assertNull(taskManager.getEpicById(epic.getId()));
        assertEquals(0, taskManager.getAllSubtasks().size()); // Подзадачи также должны быть удалены
    }

    @Test
    void testGetHistory() {
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }
}