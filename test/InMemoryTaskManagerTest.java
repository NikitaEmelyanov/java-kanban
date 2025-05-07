import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import exception.ManagerSaveException;
import exception.TimeOverlapException;
import java.util.List;
import managers.InMemoryTaskManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void init() {
        taskManager = new InMemoryTaskManager();
        task = new Task(taskManager.getNextId(),"Test tasks.Task", "Description",  TaskStatus.NEW);
        epic = new Epic("Test tasks.Epic", "tasks.Epic Description", taskManager.getNextId());
        subtask = new Subtask("Test tasks.Subtask", "tasks.Subtask Description", taskManager.getNextId(), epic);
    }

    @Test
    void testCreateTask() throws ManagerSaveException, TimeOverlapException {
        taskManager.createTask(task);
        assertEquals(task, taskManager.getTaskById(task.getId()));

    }

    @Test
    void testGetTaskById() throws ManagerSaveException, TimeOverlapException {
        taskManager.createTask(task);
        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertNull(taskManager.getTaskById(11)); // Не существующий ID
    }

    @Test
    void testGetAllTasks() throws ManagerSaveException, TimeOverlapException {
        taskManager.createTask(task);
        assertEquals(1, taskManager.getAllTasks().size());
    }

    @Test
    void testUpdateTask() throws ManagerSaveException, TimeOverlapException {
        taskManager.createTask(task);
        Task updatedTask = new Task(task.getId(),"Updated Task", "Updated Description"
                , TaskStatus.DONE);
        taskManager.updateTask(updatedTask);
        assertEquals(updatedTask, taskManager.getTaskById(task.getId()));
    }

    @Test
    void testDeleteTaskById() throws ManagerSaveException, TimeOverlapException {
        taskManager.createTask(task);
        taskManager.deleteTaskById(task.getId());
        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void testDeleteAllTasks() throws ManagerSaveException, TimeOverlapException {
        taskManager.createTask(task);
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    void testCreateEpic() {
        taskManager.createEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @Test
    void testGetEpicById() {
        taskManager.createEpic(epic);
        Epic fetchedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(epic, fetchedEpic);
    }

    @Test
    void testGetAllEpics() {
        Epic epic1 = new Epic("Epic 1", "just description1", taskManager.getNextId());
        Epic epic2 = new Epic("Epic 2", "just description2", taskManager.getNextId());
        taskManager.createEpic(epic1); // Используем createEpic
        taskManager.createEpic(epic2); // Используем createEpic

        List<Epic> allEpics = taskManager.getAllEpics();
        assertEquals(2, allEpics.size());
        assertTrue(allEpics.contains(epic1));
        assertTrue(allEpics.contains(epic2));
    }

    @Test
    void testDeleteEpicById() throws ManagerSaveException, TimeOverlapException {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.deleteEpicById(epic.getId());
        assertNull(taskManager.getEpicById(epic.getId()));
        assertEquals(0, taskManager.getAllSubtasks().size()); // Подзадачи также должны быть удалены
    }


    @Test
    void testDeleteSubtaskById() throws ManagerSaveException, TimeOverlapException {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtaskById(subtask.getId());
        assertNull(taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void testGetHistory() throws ManagerSaveException, TimeOverlapException {
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }
}