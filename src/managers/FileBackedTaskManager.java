package managers;

import exception.ManagerSaveException;
import tasks.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.load();
        return manager;
    }

    public void save() throws ManagerSaveException {
        final String header = "id,type,name,status,description,start_time,duration,epic\n";
        List<String> lines = new ArrayList<>();
        lines.add(header);

        getAllTasks().forEach(task -> lines.add(task.serializeToCsv()));
        getAllEpics().forEach(epic -> lines.add(epic.serializeToCsv()));
        getAllSubtasks().forEach(subtask -> lines.add(subtask.serializeToCsv()));

        saveToCsv(lines);
    }

    public void load() throws ManagerSaveException {
        List<String> lines = loadFromCsv();
        if (lines.isEmpty()) return;

        lines.remove(0); // Remove header

        int maxId = 0;
        for (String line : lines) {
            Task task = deSerialize(line);
            if (task != null) {
                switch (task.getType()) {
                    case TASK:
                        super.createTask(task);
                        break;
                    case EPIC:
                        super.createEpic((Epic) task);
                        break;
                    case SUBTASK:
                        super.createSubtask((Subtask) task);
                        break;
                }
                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
            }
        }
        int idCounter = maxId + 1;
    }

    private Task deSerialize(String line) {
        String[] fields = line.split(",");
        if (fields.length < 5) return null;

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        LocalDateTime startTime = fields.length > 5 && !fields[5].isEmpty()
            ? LocalDateTime.parse(fields[5], DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            : null;
        Duration duration = fields.length > 6 && !fields[6].isEmpty()
            ? Duration.ofMinutes(Long.parseLong(fields[6]))
            : null;

        switch (type) {
            case TASK:
                return new Task(id, name, description, status, startTime, duration);
            case EPIC:
                return new Epic(id, name, description, status, startTime, duration, null);
            case SUBTASK:
                if (fields.length < 8) return null;
                int epicId = Integer.parseInt(fields[7]);
                return new Subtask(id, epicId, name, description, status, startTime, duration);
            default:
                return null;
        }
    }

    private void saveToCsv(List<String> lines) throws ManagerSaveException {
        if (file == null) {
            throw new ManagerSaveException("Невозможно сохранить данные в файл.");
        }

        try (BufferedWriter writer = new BufferedWriter(
            new FileWriter(file, StandardCharsets.UTF_8, false))) {
            for (String line : lines) {
                writer.write(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл");
        }
    }

    private List<String> loadFromCsv() throws ManagerSaveException {
        if (file == null || !file.exists()) {
            throw new ManagerSaveException("Невозможно загрузить данные из файла.");
        }

        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
            new FileReader(file, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                lines.add(reader.readLine());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении из файла");
        }

        return lines;
    }

    @Override
    public void createTask(Task task) throws ManagerSaveException {
        super.createTask(task);
        save();
    }

    @Override
    public Task updateTask(Task task) throws ManagerSaveException {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Ошибка при сохранении после удаления задачи", e);
        }
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Ошибка при сохранении после удаления всех задач", e);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Ошибка при сохранении эпика", e);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Ошибка при обновлении эпика", e);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Ошибка при сохранении после удаления эпика", e);
        }
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Ошибка при сохранении после удаления всех эпиков", e);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) throws ManagerSaveException {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Ошибка при сохранении после удаления подзадачи", e);
        }
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Ошибка при сохранении после удаления всех подзадач", e);
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int id) {
        List<Subtask> subtasks = super.getSubtasksByEpicId(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Ошибка при сохранении после получения подзадач эпика", e);
        }
        return subtasks;
    }
}