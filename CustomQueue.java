package ds;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Linked-List Queue implementation for customer waiting line.
 * Implements standard FIFO operations without using java.util.Queue.
 *
 * @param <T> Element type held in the queue
 */
public class CustomQueue<T> {
    private Node<T> head; // Front of the queue
    private Node<T> tail; // Rear of the queue
    private int count;    // Number of elements

    public CustomQueue() {
        this.head = null;
        this.tail = null;
        this.count = 0;
    }

    /**
     * Add element to the rear of the queue (O(1)).
     */
    public void enqueue(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            tail = newNode;
        }
        count++;
    }

    /**
     * Remove and return the front element of the queue (O(1)).
     * Returns null if queue is empty.
     */
    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        T data = head.getData();
        head = head.getNext();
        if (head == null) {
            tail = null;
        }
        count--;
        return data;
    }

    /**
     * View front element without removing it (O(1)).
     */
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return head.getData();
    }

    /**
     * Check if queue is empty.
     */
    public boolean isEmpty() {
        return head == null;
    }

    /**
     * Return current queue size.
     */
    public int size() {
        return count;
    }

    /**
     * Helper to return all elements as a List for traversal/display without modifying the queue.
     */
    public List<T> getAllElements() {
        List<T> list = new ArrayList<>();
        Node<T> current = head;
        while (current != null) {
            list.add(current.getData());
            current = current.getNext();
        }
        return list;
    }
}
