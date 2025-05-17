package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.ManagerSaveException;
import exception.TimeOverlapException;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/epics")) {
            String response = gson.toJson(taskManager.getAllEpics());
            sendText(exchange, response);
        } else if (path.matches("/epics/\\d+")) {
            int id = Integer.parseInt(path.split("/")[2]);
            Epic epic = taskManager.getEpicById(id);
            if (epic != null) {
                sendText(exchange, gson.toJson(epic));
            } else {
                sendNotFound(exchange);
            }
        } else if (path.matches("/epics/\\d+/subtasks")) {
            int epicId = Integer.parseInt(path.split("/")[2]);
            List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epicId);
            sendText(exchange, gson.toJson(subtasks));
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readText(exchange);
        Epic epic = gson.fromJson(body, Epic.class);
        if (epic.getId() == 0) {
            taskManager.createEpic(epic);
            sendCreated(exchange);
        } else {
            taskManager.updateEpic(epic);
            sendCreated(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/epics/\\d+")) {
            int id = Integer.parseInt(path.split("/")[2]);
            taskManager.deleteEpicById(id);
            sendCreated(exchange);
        } else if (path.equals("/epics")) {
            taskManager.deleteAllEpics();
            sendCreated(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}