package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {

    private Epic epic;
    private int epicId;

    public Subtask(String name, String description, int id, Epic epic) {
        super(name, description, id);
        this.epic = epic;
        this.epicId = epic.getId();
    }

    public Subtask(int id, int epicId, String name, String description, TaskStatus status,
        LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int id, LocalDateTime startTime,
        Duration duration, int epicId) {
        super(id, name, description, TaskStatus.NEW, startTime, duration);
        this.epicId = epicId;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
        this.epicId = epic.getId();
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String serializeToCsv() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
            getId(), TaskType.SUBTASK, getName(), getStatus(),
            getDescription(),
            getStartTime() != null ? getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : "",
            getDuration() != null ? getDuration().toMinutes() : "",
            epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
               "name='" + getName() + '\'' +
               ", id=" + getId() +
               ", status=" + getStatus() +
               ", epicId=" + epicId +
               ", startTime=" + (getStartTime() != null ?
            getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "null") +
               ", duration=" + (getDuration() != null ? getDuration().toMinutes() + "m" : "null") +
               '}';
    }
}