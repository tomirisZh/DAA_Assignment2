package workload;
public class MyLinkedList implements IntList {
    private static class Node {
        int val;
        Node next;
        Node(int val) { this.val = val; }
    }

    private Node head, tail;
    private int size = 0;
    private long steps, moves, comparisons;

    @Override
    public void add(int x) {
        Node node = new Node(x);
        if (head == null) {
            head = tail = node;
            moves++;
        } else {
            tail.next = node;
            tail = node;
            moves += 2;
        }
        size++;
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("index " + index);
        if (index == size) { add(x); return; }
        Node node = new Node(x);
        if (index == 0) {
            node.next = head;
            head = node;
            moves += 2;
        } else {
            Node prev = head;
            for (int i = 0; i < index - 1; i++) {
                prev = prev.next;
                steps++;
            }
            node.next = prev.next;
            prev.next = node;
            moves += 2;
        }
        size++;
    }

    @Override
    public int remove(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("index " + index);
        int removed;
        if (index == 0) {
            removed = head.val;
            head = head.next;
            moves++;
            if (head == null) { tail = null; moves++; }
        } else {
            Node prev = head;
            for (int i = 0; i < index - 1; i++) {
                prev = prev.next;
                steps++;
            }
            Node target = prev.next;
            removed = target.val;
            prev.next = target.next;
            moves++;
            if (target == tail) { tail = prev; moves++; }
        }
        size--;
        return removed;
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("index " + index);
        Node cur = head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
            steps++;
        }
        return cur.val;
    }

    @Override
    public boolean contains(int x) {
        Node cur = head;
        while (cur != null) {
            comparisons++;
            if (cur.val == x) return true;
            cur = cur.next;
            steps++;
        }
        return false;
    }

    @Override public int size() { return size; }
    @Override public long getSteps() { return steps; }
    @Override public long getMoves() { return moves; }
    @Override public long getComparisons() { return comparisons; }
    @Override public void resetMetrics() { steps = moves = comparisons = 0; }
}