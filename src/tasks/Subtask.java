package tasks;

public class Subtask extends Task {
    private final Epic epic; // Эпик, к которому принадлежит подзадача

    public Subtask(String name, String description, int id, Epic epic) {
        super(name, description, id);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }
}