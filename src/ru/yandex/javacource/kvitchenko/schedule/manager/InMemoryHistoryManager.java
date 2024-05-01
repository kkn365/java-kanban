package ru.yandex.javacource.kvitchenko.schedule.manager;

import ru.yandex.javacource.kvitchenko.schedule.interfaces.HistoryManager;
import ru.yandex.javacource.kvitchenko.schedule.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> tail; // указатель на последний элемент списка
    private int size = 0;

    private final HashMap<Integer, Node<Task>> history = new HashMap<>();

    @Override
    public void add(Task task) {

        final int taskId = task.getId();

        final Node<Task> newNode = new Node<>(task.copy(), tail, null);

        if (this.tail != null) {
            // добавить ссылку на следующий элемент
            this.tail.setNext(newNode);
        }

        // удалить элемент из списка, если он уже есть в истории обращений
        if (history.containsKey(taskId)) {
            remove(taskId);
        }

        this.tail = newNode;
        history.put(taskId, newNode);
        this.size++;

    }

    @Override
    public void remove(int id) {
        // если элемент единственный в списке
        if (size() == 1) {
            history.remove(id);
            this.tail = null;
            this.size--;
            return;
        }

        final Node<Task> curNode = history.get(id);
        final Node<Task> tmpPrev = curNode.getPrev();
        final Node<Task> tmpNext = curNode.getNext();

        // Node в середине списка
        if (tmpPrev != null && tmpNext != null) {
            tmpNext.setPrev(tmpPrev);
            tmpPrev.setNext(tmpNext);
            history.remove(id);
            this.size--;
            return;
        }

        // Node в конце списка
        if (tmpNext == null) {
            tmpPrev.setNext(null);
            history.remove(id);
            this.size--;
        }

    }

    public int size() {
        return this.size;
    }

    @Override
    public List<Task> getHistory() {

        List<Task> historyList = new ArrayList<>();
        Node<Task> curNode = tail;

        while (curNode.getPrev() != null) {
            historyList.add(curNode.getData());
            curNode = curNode.getPrev();
        }

        historyList.add(curNode.getData());
        return historyList;

    }
}