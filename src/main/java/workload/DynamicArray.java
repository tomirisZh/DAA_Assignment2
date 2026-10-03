package workload;

public class DynamicArray implements IntList {
    private int[] data = new int[10];
    private int size = 0;
    private long steps, moves, comparisons;

    @Override
    public void add(int x) {
        if (size == data.length) grow();
        data[size++] = x;
    }

    @Override
    public void add(int index, int x) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("index " + index);
        if (size == data.length) grow();
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
            moves++;
        }
        data[index] = x;
        size++;
    }

    @Override
    public int remove(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("index " + index);
        int removed = data[index];
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
            moves++;
        }
        size--;
        return removed;
    }

    @Override
    public int get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("index " + index);
        steps++;
        return data[index];
    }

    @Override
    public boolean contains(int x) {
        for (int i = 0; i < size; i++) {
            steps++;
            comparisons++;
            if (data[i] == x) return true;
        }
        return false;
    }

    private void grow() {
        int[] bigger = new int[data.length * 2];
        for (int i = 0; i < size; i++) {
            bigger[i] = data[i];
            moves++;
        }
        data = bigger;
    }

    @Override public int size() { return size; }
    @Override public long getSteps() { return steps; }
    @Override public long getMoves() { return moves; }
    @Override public long getComparisons() { return comparisons; }
    @Override public void resetMetrics() { steps = moves = comparisons = 0; }
}