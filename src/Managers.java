public class Managers {

    public static TaskManager getDefault(){
        HistoryManager historyManager = getDefaultHistory();
        return new InMemoryTaskManager((InMemoryHistoryManager) historyManager);
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
