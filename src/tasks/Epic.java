package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description, int id) {
        super(name, description, id);
        this.subtasks = new ArrayList<>();
    }

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        this.subtasks = new ArrayList<>();
    }

    public Epic(int id, String name, String description, TaskStatus status,
        LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super(id, name, description, status, startTime, duration);
        this.subtasks = new ArrayList<>();
        this.endTime = endTime;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        updateStatus();
        updateTime();
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateStatus();
        updateTime();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void updateTime() {
        if (subtasks.isEmpty()) {
            super.setStartTime(null);
            super.setDuration(Duration.ZERO);
            this.endTime = null;
            return;
        }

        LocalDateTime newStartTime = subtasks.stream()
            .map(Subtask::getStartTime)
            .filter(Objects::nonNull)
            .min(LocalDateTime::compareTo)
            .orElse(null);

        LocalDateTime newEndTime = subtasks.stream()
            .map(Subtask::getEndTime)
            .filter(Objects::nonNull)
            .max(LocalDateTime::compareTo)
            .orElse(null);

        Duration newDuration = subtasks.stream()
            .map(Subtask::getDuration)
            .filter(Objects::nonNull)
            .reduce(Duration.ZERO, Duration::plus);

        super.setStartTime(newStartTime);
        super.setDuration(newDuration);
        this.endTime = newEndTime;
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = subtasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.NEW);
        boolean allDone = subtasks.stream().allMatch(subtask -> subtask.getStatus() == TaskStatus.DONE);

        if (allNew) {
            setStatus(TaskStatus.NEW);
        } else if (allDone) {
            setStatus(TaskStatus.DONE);
        } else {
            setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public String serializeToCsv() {
        return String.format("%s,%s,%s,%s,%s,%s,%s\n",
            getId(), TaskType.EPIC, getName(), getStatus(),
            getDescription(),
            getStartTime() != null ? getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "",
            getDuration() != null ? getDuration().toMinutes() : "");
    }

    @Override
    public String toString() {
        return "Epic{" +
               "name='" + getName() + '\'' +
               ", id=" + getId() +
               ", status=" + getStatus() +
               ", startTime=" + (getStartTime() != null ?
            getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "null") +
               ", duration=" + (getDuration() != null ? getDuration().toMinutes() + "m" : "null") +
               ", endTime=" + (endTime != null ? endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "null") +
               ", subtasksCount=" + subtasks.size() +
               '}';
    }
}