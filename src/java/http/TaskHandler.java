package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TimeOverlapException;
import managers.TaskManager;
import tasks.Task;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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
            e.printStackTrace();
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
                Task createdTask = taskManager.createTask(task);
                String response = gson.toJson(createdTask);
                sendText(exchange, response, 201);
            } else {
                taskManager.updateTask(task);
                sendText(exchange, "Task updated", 201);
            }
        } catch (TimeOverlapException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        if (path.matches("/tasks/\\d+")) {
            int id = Integer.parseInt(path.split("/")[2]);
            taskManager.deleteTaskById(id);
            sendText(exchange, "Task deleted", 200);
        } else if (path.equals("/tasks")) {
            taskManager.deleteAllTasks();
            sendText(exchange, "All tasks deleted", 200);
        } else {
            sendNotFound(exchange);
        }
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendText(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 200);
    }
}