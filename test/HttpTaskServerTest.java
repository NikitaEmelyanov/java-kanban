import com.google.gson.Gson;
import http.HttpTaskServer;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

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
        Task task = new Task("Test", "Description", 1);
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/tasks"))
            .POST(HttpRequest.BodyPublishers.ofString(taskJson))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/tasks/1"))
            .GET()
            .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());

        Task savedTask = gson.fromJson(getResponse.body(), Task.class);
        assertEquals(task.getName(), savedTask.getName());
    }
}