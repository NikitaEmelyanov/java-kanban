package managers;

import exception.ManagerSaveException;
import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        HistoryManager historyManager = getDefaultHistory();
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        fileBackedTaskManager.load();
        return fileBackedTaskManager;
    }
}
