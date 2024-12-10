public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // Создаем задачи
        Task task1 = new Task("Первая задача", "Описание первой задачи", manager.getNextId());
        Task task2 = new Task("Вторая задача", "Описание второй задачи", manager.getNextId());
        manager.createTask(task1);
        manager.createTask(task2);

        // Создаем эпики и подзадачи
        Epic epic1 = new Epic("Организовать праздник", "Организация семейного праздника", manager.getNextId());
        Subtask subtask1 = new Subtask("Забронировать место", "Арендовать зал", manager.getNextId(), epic1);
        Subtask subtask2 = new Subtask("Пригласить гостей", "Отправить приглашения", manager.getNextId(), epic1);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        // Печатать списки задач, эпиков и подзадач
        System.out.println("Все задачи: " + manager.getAllTasks());
        System.out.println("Все эпики: " + manager.getAllEpics());
        System.out.println("Все подзадачи: " + manager.getAllSubtasks());

        // Изменение статусов
        task1.setStatus(TaskStatus.DONE);
        task2.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.NEW);
        epic1.updateStatus(); // Обновляем статус эпика

        // Печать статусов
        System.out.println("Статус задачи 1: " + task1.getStatus());
        System.out.println("Статус задачи 2: " + task2.getStatus());
        System.out.println("Статус эпика: " + epic1.getStatus());

        // Удаление задачи и эпика
        manager.deleteTaskById(task1.getId());
        manager.deleteEpicById(epic1.getId());

        // Проверка удаления
        System.out.println("Задачи после удаления: " + manager.getAllTasks());
        System.out.println("Эпики после удаления: " + manager.getAllEpics());
    }
}