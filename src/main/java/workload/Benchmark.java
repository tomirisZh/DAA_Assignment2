package workload;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

public class Benchmark {
    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int WARMUP_RUNS = 10;
    private static final int MEASURED_RUNS = 5;
    private static final long SEED = 42;
    private static final int VALUE_RANGE = 1_000_000;

    private static final String[] NAMES = {"DynamicArray", "MyLinkedList"};
    private static final List<Supplier<IntList>> FACTORIES =
            List.of(DynamicArray::new, MyLinkedList::new);

    private static volatile long sink;

    private record Result(double timeMs, long steps, long moves, long comparisons) {}

    private interface Case { Result run(); }

    private static Result measure(Case c) {
        for (int i = 0; i < WARMUP_RUNS; i++) c.run();
        double[] times = new double[MEASURED_RUNS];
        Result last = null;
        for (int i = 0; i < MEASURED_RUNS; i++) {
            last = c.run();
            times[i] = last.timeMs();
        }
        Arrays.sort(times);
        return new Result(times[MEASURED_RUNS / 2], last.steps(), last.moves(), last.comparisons());
    }

    private static int[] fill(IntList list, int n) {
        Random rng = new Random(SEED);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = rng.nextInt(VALUE_RANGE);
            list.add(data[i]);
        }
        list.resetMetrics();
        return data;
    }

    private static Result result(IntList l, long t0, long t1) {
        return new Result((t1 - t0) / 1e6, l.getSteps(), l.getMoves(), l.getComparisons());
    }

    private static Result w1(Supplier<IntList> f, int n) {
        IntList list = f.get();
        fill(list, n);
        Random rng = new Random(SEED + 1);
        int[] idx = new int[10_000];
        for (int i = 0; i < idx.length; i++) idx[i] = rng.nextInt(n);
        long sum = 0;
        long t0 = System.nanoTime();
        for (int i : idx) sum += list.get(i);
        long t1 = System.nanoTime();
        sink += sum;
        return result(list, t0, t1);
    }

    private static Result w2(Supplier<IntList> f, int n) {
        IntList list = f.get();
        int[] data = fill(list, n);
        Random rng = new Random(SEED + 1);
        int[] q = new int[1_000];
        for (int i = 0; i < q.length; i++)
            q[i] = (i % 2 == 0) ? data[rng.nextInt(n)] : -1 - rng.nextInt(VALUE_RANGE);
        int found = 0;
        long t0 = System.nanoTime();
        for (int x : q) if (list.contains(x)) found++;
        long t1 = System.nanoTime();
        sink += found;
        return result(list, t0, t1);
    }

    private static Result w3(Supplier<IntList> f, int n, boolean head) {
        IntList list = f.get();
        fill(list, n);
        Random rng = new Random(SEED + 1);
        int[] vals = new int[1_000];
        for (int i = 0; i < vals.length; i++) vals[i] = rng.nextInt(VALUE_RANGE);
        int pos = head ? 0 : n / 2;
        long sum = 0;
        long t0 = System.nanoTime();
        for (int v : vals) list.add(pos, v);
        for (int i = 0; i < vals.length; i++) sum += list.remove(pos);
        long t1 = System.nanoTime();
        sink += sum;
        return result(list, t0, t1);
    }

    private static Result w4(int n) {
        Random rng = new Random(SEED);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) data[i] = rng.nextInt(VALUE_RANGE);
        MinHeap heap = new MinHeap();
        long sum = 0;
        int prev = Integer.MIN_VALUE;
        long t0 = System.nanoTime();
        for (int x : data) heap.insert(x);
        for (int i = 0; i < n; i++) {
            int m = heap.extractMin();
            if (m < prev) throw new IllegalStateException("Not sorted at i=" + i);
            prev = m;
            sum += m;
        }
        long t1 = System.nanoTime();
        sink += sum;
        return new Result((t1 - t0) / 1e6, heap.getSteps(), heap.getMoves(), heap.getComparisons());
    }

    private static void row(PrintWriter w, String wl, String variant, String structure, int n, Result r) {
        w.printf(Locale.US, "%s,%s,%s,%d,%.3f,%d,%d,%d%n",
                wl, variant, structure, n, r.timeMs(), r.steps(), r.moves(), r.comparisons());
        w.flush();
        System.out.printf(Locale.US, "%s %s %s n=%d -> %.3f ms%n", wl, variant, structure, n, r.timeMs());
    }

    public static void main(String[] args) throws IOException {
        Path out = Path.of("results", "results.csv");
        Files.createDirectories(out.getParent());
        for (int rep = 0; rep < 20; rep++) {
            for (Supplier<IntList> f : FACTORIES) {
                w1(f, 1_000);
                w2(f, 1_000);
                w3(f, 1_000, true);
                w3(f, 1_000, false);
            }
            w4(1_000);
        }
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(out))) {
            w.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");
            for (int n : SIZES) {
                for (int s = 0; s < FACTORIES.size(); s++) {
                    Supplier<IntList> f = FACTORIES.get(s);
                    String name = NAMES[s];
                    row(w, "W1", "-", name, n, measure(() -> w1(f, n)));
                    row(w, "W2", "-", name, n, measure(() -> w2(f, n)));
                    row(w, "W3", "head", name, n, measure(() -> w3(f, n, true)));
                    row(w, "W3", "middle", name, n, measure(() -> w3(f, n, false)));
                }
                row(w, "W4", "-", "MinHeap", n, measure(() -> w4(n)));
            }
        }
        System.out.println("Done: " + out.toAbsolutePath() + " (sink=" + sink + ")");
    }
}