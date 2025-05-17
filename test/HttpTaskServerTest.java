import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import http.HttpTaskServer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

class HttpTaskServerTest {

    private HttpTaskServer server;
    private TaskManager manager;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        gson = server.getGson();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testCreateAndGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test", "Description");
        String taskJson = gson.toJson(task);

        // POST запрос
        HttpRequest postRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/tasks"))
            .POST(HttpRequest.BodyPublishers.ofString(taskJson))
            .build();
        HttpResponse<String> postResponse = client.send(postRequest,
            HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode());

        Task createdTask = gson.fromJson(postResponse.body(), Task.class);
        int taskId = createdTask.getId();

        // GET запрос
        HttpRequest getRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/tasks/" + taskId))
            .GET()
            .build();
        HttpResponse<String> getResponse = client.send(getRequest,
            HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());

        Task savedTask = gson.fromJson(getResponse.body(), Task.class);
        assertEquals(task.getName(), savedTask.getName());
    }
}