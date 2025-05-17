package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = TaskStatus.NEW;
    }

    public Task(String name, String description, int id, LocalDateTime startTime,
        Duration duration) {
        this(name, description, id);
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String name, String description, TaskStatus status,
        LocalDateTime startTime, Duration duration) {
        this(id, name, description, status);
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, TaskStatus status, Duration duration,
        LocalDateTime startTime) {

    }

    public Task(String test, String description) {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public void updateStatus() {
    }

    public String serializeToCsv() {
        return String.format("%s,%s,%s,%s,%s,%s,%s\n",
            id, getType(), name, status, description,
            startTime != null ? startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "",
            duration != null ? duration.toMinutes() : "");
    }

    @Override
    public String toString() {
        return "Task{" +
               "name='" + name + '\'' +
               ", id=" + id +
               ", status=" + status +
               ", startTime=" + (startTime != null ? startTime.format(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "null") +
               ", duration=" + (duration != null ? duration.toMinutes() + "m" : "null") +
               '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Task task = (Task) obj;
        return id == task.id &&
               Objects.equals(name, task.name) &&
               Objects.equals(description, task.description) &&
               status == task.status &&
               Objects.equals(duration, task.duration) &&
               Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, duration, startTime);
    }
}