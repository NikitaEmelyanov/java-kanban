package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TimeOverlapException;
import java.io.IOException;
import managers.TaskManager;
import tasks.Subtask;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
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
        if (path.equals("/subtasks")) {
            String response = gson.toJson(taskManager.getAllSubtasks());
            sendText(exchange, response);
        } else if (path.matches("/subtasks/\\d+")) {
            int id = Integer.parseInt(path.split("/")[2]);
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask != null) {
                sendText(exchange, gson.toJson(subtask));
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readText(exchange);
        Subtask subtask = gson.fromJson(body, Subtask.class);

        try {
            if (subtask.getId() == 0) {
                taskManager.createSubtask(subtask);
                sendCreated(exchange);
            } else {
                taskManager.updateSubtask(subtask);
                sendCreated(exchange);
            }
        } catch (TimeOverlapException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/subtasks/\\d+")) {
            int id = Integer.parseInt(path.split("/")[2]);
            taskManager.deleteSubtaskById(id);
            sendCreated(exchange);
        } else if (path.equals("/subtasks")) {
            taskManager.deleteAllSubtasks();
            sendCreated(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}