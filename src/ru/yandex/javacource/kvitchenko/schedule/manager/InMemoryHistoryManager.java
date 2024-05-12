package ru.yandex.javacource.kvitchenko.schedule.manager;

import ru.yandex.javacource.kvitchenko.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head; // указатель на первый элемент списка
    private Node<Task> tail; // указатель на последний элемент списка
    private int size = 0;

    private final HashMap<Integer, Node<Task>> history = new HashMap<>();

    @Override
    public void add(Task task) {
        final int taskId = task.getId();
        linkLast(task);
        // удалить элемент из списка, если он уже есть в истории обращений
        // if (history.containsKey(taskId)) {
        //    remove(taskId);
        // }
        history.put(taskId, tail);
        this.size++;
    }

    @Override
    public void remove(int id) {
        final Node node = history.remove(id);
        if (node == null) {
            return;
        }
        removeNode(node);
        this.size--;
    }

    private void removeNode(Node node) {

        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
            if (node.getNext() == null) {
                tail = node.getPrev();
            } else {
                node.getNext().setPrev(node.getPrev());
            }
        } else {
            head = node.getNext();
            if (head == null) {
                tail = null;
            } else {
                head.setPrev(null);
            }
        }
    }

    public void linkLast(Task task) {
        final Node<Task> newNode = new Node<>(task.copy(), tail, null);
        if (head == null) {
            head = newNode;
        } else {
            tail.setNext(newNode);
        }
        tail = newNode;
    }

    public int size() {
        return this.size;
    }

    @Override
    public List<Task> getHistory() {

        List<Task> historyList = new ArrayList<>();

        if (head != null) {
            Node<Task> curNode = tail;

            while (curNode.getPrev() != null) {
                historyList.add(curNode.getData());
                curNode = curNode.getPrev();
            }

            historyList.add(curNode.getData());
        }
        return historyList;

    }
}