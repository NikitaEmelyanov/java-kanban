package managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tasks.Task;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> idNode;
    private Node<Task> first;
    private Node<Task> last;

    public InMemoryHistoryManager() {
        this.idNode = new HashMap<>();
    }

    private static class Node<T> {

        private T data;
        private Node<T> next;
        private Node<T> prev;

        public Node(T data, Node<T> next, Node<T> prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    @Override
    public void addToHistory(Task task) {
        if (task != null) {
            remove(task.getId());
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(idNode.get(id));
        idNode.remove(idNode.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task element) {
        final Node<Task> oldTail = last;
        final Node<Task> newNode = new Node<>(element, oldTail, null);
        last = newNode;
        idNode.put(element.getId(), newNode);
        if (oldTail == null)
            first = newNode;
        else
            oldTail.next = newNode;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> currentNode = first;
        while (!(currentNode == null)) {
            tasks.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (!(node == null)) {
            final Node<Task> next = node.next;
            final Node<Task> prev = node.prev;
            node.data = null;

            if (first == node && last == node) {
                first = null;
                last = null;
            } else if (first == node) {
                first = next;
                first.prev = null;
            } else if (last == node) {
                last = prev;
                last.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }

        }
    }
}