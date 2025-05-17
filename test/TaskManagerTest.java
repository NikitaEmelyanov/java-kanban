package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import exception.ManagerSaveException;
import exception.TimeOverlapException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

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
    void testCreateTaskWithTime() throws ManagerSaveException, TimeOverlapException {
        taskManager.createTask(task);
        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask);
        assertEquals(task, savedTask);
        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void testTaskTimeOverlap() throws ManagerSaveException, TimeOverlapException {
        taskManager.createTask(task);
        Task overlappingTask = new Task("Overlapping", "Desc", 4,
            task.getStartTime().plusMinutes(15), Duration.ofMinutes(20));

        // Ожидаем TimeOverlapException вместо ManagerSaveException
        assertThrows(TimeOverlapException.class, () -> taskManager.createTask(overlappingTask));
    }

    @Test
    void testPrioritizedTasksOrder() throws ManagerSaveException, TimeOverlapException {
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
    void testEpicTimeCalculation() throws ManagerSaveException, TimeOverlapException {
        // Фиксируем конкретное время для теста
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        Duration duration = Duration.ofHours(1);

        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Sub 1", "Desc", taskManager.getNextId(),
            startTime, duration, epic.getId());
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Sub 2", "Desc", taskManager.getNextId(),
            startTime.plusHours(2), duration.plusHours(1), epic.getId());
        taskManager.createSubtask(subtask2);

        Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertEquals(startTime, savedEpic.getStartTime()); // Должен быть минимальный startTime
        assertEquals(startTime.plusHours(2).plusHours(2),
            savedEpic.getEndTime()); // startTime + duration
        assertEquals(duration.plus(duration.plusHours(1)),
            savedEpic.getDuration()); // Сумма длительностей
    }

    @Test
    void testUpdateTaskTime() throws ManagerSaveException, TimeOverlapException {
        taskManager.createTask(task);
        Task updatedTask = new Task(task.getId(), task.getName(), task.getDescription(),
            task.getStatus(), task.getStartTime().plusHours(3), task.getDuration());

        taskManager.updateTask(updatedTask);
        assertEquals(updatedTask.getStartTime(),
            taskManager.getTaskById(task.getId()).getStartTime());
    }
}