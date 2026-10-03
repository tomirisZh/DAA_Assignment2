package workload;
public class MinHeap {
    private int[] data = new int[16];
    private int size = 0;
    private long steps, moves, comparisons;

    public void insert(int x) {
        if (size == data.length) {
            int[] bigger = new int[data.length * 2];
            for (int i = 0; i < size; i++) { bigger[i] = data[i]; moves++; }
            data = bigger;
        }
        data[size] = x;
        int i = size++;
        while (i > 0) {
            int p = (i - 1) / 2;
            steps++;
            comparisons++;
            if (data[p] <= data[i]) break;
            swap(p, i);
            moves++;
            i = p;
        }
    }

    public int peekMin() {
        if (size == 0) throw new IllegalStateException("heap is empty");
        steps++;
        return data[0];
    }

    public int extractMin() {
        if (size == 0) throw new IllegalStateException("heap is empty");
        int min = data[0];
        data[0] = data[--size];
        moves++;
        siftDown(0);
        return min;
    }

    private void siftDown(int i) {
        while (true) {
            int l = 2 * i + 1, r = 2 * i + 2;
            if (l >= size) break;
            steps++;
            int smallest = l;
            if (r < size) {
                comparisons++;
                if (data[r] < data[l]) smallest = r;
            }
            comparisons++;
            if (data[i] <= data[smallest]) break;
            swap(i, smallest);
            moves++;
            i = smallest;
        }
    }

    private void swap(int a, int b) {
        int t = data[a]; data[a] = data[b]; data[b] = t;
    }
    public boolean isValidHeap() {
        for (int i = 1; i < size; i++)
            if (data[(i - 1) / 2] > data[i]) return false;
        return true;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
    public long getSteps() { return steps; }
    public long getMoves() { return moves; }
    public long getComparisons() { return comparisons; }
    public void resetMetrics() { steps = moves = comparisons = 0; }
}