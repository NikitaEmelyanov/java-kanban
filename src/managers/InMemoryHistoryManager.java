package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private final HashMap<Integer, Node<Task>> idNode;
    private Node<Task> first;
    private Node<Task> last;

    public InMemoryHistoryManager() {
        this.idNode = new HashMap<>();
    }

    private static class Node<Task> {

        public Task data;
        public Node<Task> next;
        public Node<Task> prev;

        public Node(Task data, Node<Task> next, Node<Task> prev) {
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
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task element) {
        final Node<Task> oldTail = last;
        final Node<Task> newNode = new Node<>( element, oldTail, null);
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