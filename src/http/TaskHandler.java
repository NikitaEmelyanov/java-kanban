package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Task;
import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
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
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/tasks")) {
            String response = gson.toJson(taskManager.getAllTasks());
            sendText(exchange, response);
        } else if (path.matches("/tasks/\\d+")) {
            int id = Integer.parseInt(path.split("/")[2]);
            Task task = taskManager.getTaskById(id);
            if (task != null) {
                sendText(exchange, gson.toJson(task));
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readText(exchange);
        Task task = gson.fromJson(body, Task.class);

        try {
            if (task.getId() == 0) {
                taskManager.createTask(task);
                sendCreated(exchange);
            } else {
                taskManager.updateTask(task);
                sendCreated(exchange);
            }
        } catch (Exception e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.matches("/tasks/\\d+")) {
            int id = Integer.parseInt(path.split("/")[2]);
            taskManager.deleteTaskById(id);
            sendCreated(exchange);
        } else if (path.equals("/tasks")) {
            taskManager.deleteAllTasks();
            sendCreated(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}