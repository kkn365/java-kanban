package ru.yandex.javacource.kvitchenko.schedule.managers;

import ru.yandex.javacource.kvitchenko.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager<T> {

    private Node<T> head; // указатель на первый элемент списка
    private Node<T> tail; // указатель на последний элемент списка

    private final HashMap<Integer, Node<T>> history = new HashMap<>();

    @Override
    public void add(T task) {
        final int taskId = task.getId();
        linkLast(task);
        //удалить элемент из списка, если он уже есть в истории обращений
        remove(taskId);
        history.put(taskId, tail);
    }

    @Override
    public void remove(int id) {
        final Node<T> node = history.remove(id);
        if (node == null) {
            return;
        }
        removeNode(node);
    }

    private void removeNode(Node<T> node) {
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

    public void linkLast(T task) {
        final Node<T> newNode = new Node<>((T)task.copy(), tail, null);
        if (head == null) {
            head = newNode;
        } else {
            tail.setNext(newNode);
        }
        tail = newNode;
    }

    @Override
    public List<T> getHistory() {
        List<T> historyList = new ArrayList<>();
        if (head != null) {
            Node<T> curNode = tail;
            while (curNode.getPrev() != null) {
                historyList.add(curNode.getData());
                curNode = curNode.getPrev();
            }
            historyList.add(curNode.getData());
        }
        return historyList;
    }

}