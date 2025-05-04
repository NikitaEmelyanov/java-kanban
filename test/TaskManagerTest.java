package tests;

import exception.ManagerSaveException;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    @BeforeEach
    void setUp() {
        taskManager = (T) Managers.getDefault();
        task = new Task("Test Task", "Description", 1,
            LocalDateTime.now(), Duration.ofMinutes(30));
        epic = new Epic("Test Epic", "Epic Description", 2);
        subtask = new Subtask("Test Subtask", "Subtask Description", 3,
            LocalDateTime.now().plusHours(1), Duration.ofMinutes(45), 2);
    }

    @Test
    void testCreateTaskWithTime() throws ManagerSaveException {
        taskManager.createTask(task);
        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask);
        assertEquals(task, savedTask);
        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void testTaskTimeOverlap() throws ManagerSaveException {
        taskManager.createTask(task);
        Task overlappingTask = new Task("Overlapping", "Desc", 4,
            task.getStartTime().plusMinutes(15), Duration.ofMinutes(20));

        assertThrows(ManagerSaveException.class, () -> taskManager.createTask(overlappingTask));
    }

    @Test
    void testPrioritizedTasksOrder() throws ManagerSaveException {
        Task earlyTask = new Task("Early", "Desc", 5,
            LocalDateTime.now().plusHours(2), Duration.ofMinutes(30));
        Task lateTask = new Task("Late", "Desc", 6,
            LocalDateTime.now().plusHours(1), Duration.ofMinutes(30));

        taskManager.createTask(earlyTask);
        taskManager.createTask(lateTask);

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(lateTask.getId(), prioritized.get(0).getId());
        assertEquals(earlyTask.getId(), prioritized.get(1).getId());
    }

    @Test
    void testEpicTimeCalculation() throws ManagerSaveException {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        Subtask subtask2 = new Subtask("Sub 2", "Desc", 4,
            subtask.getStartTime().plusHours(2), Duration.ofHours(1), epic.getId());
        taskManager.createSubtask(subtask2);

        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(subtask.getStartTime(), savedEpic.getStartTime());
        assertEquals(subtask2.getEndTime(), savedEpic.getEndTime());
        assertEquals(
            subtask.getDuration().plus(subtask2.getDuration()),
            savedEpic.getDuration()
        );
    }

    @Test
    void testUpdateTaskTime() throws ManagerSaveException {
        taskManager.createTask(task);
        Task updatedTask = new Task(task.getId(),task.getName(), task.getDescription(),
            task.getStatus(), task.getStartTime().plusHours(3), task.getDuration());

        taskManager.updateTask(updatedTask);
        assertEquals(updatedTask.getStartTime(),
            taskManager.getTaskById(task.getId()).getStartTime());
    }
}