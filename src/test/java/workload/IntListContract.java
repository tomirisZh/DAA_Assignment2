package workload;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

abstract class IntListContract {
    protected abstract IntList create();

    @Test
    void emptyStructure() {
        IntList l = create();
        assertEquals(0, l.size());
        assertFalse(l.contains(1));
        assertThrows(IndexOutOfBoundsException.class, () -> l.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> l.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> l.add(1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> l.add(-1, 5));
    }

    @Test
    void singleElement() {
        IntList l = create();
        l.add(7);
        assertEquals(7, l.get(0));
        assertTrue(l.contains(7));
        assertEquals(7, l.remove(0));
        assertEquals(0, l.size());
        assertFalse(l.contains(7));
    }

    @Test
    void duplicates() {
        IntList l = create();
        l.add(3); l.add(3); l.add(3);
        l.remove(1);
        assertEquals(2, l.size());
        assertTrue(l.contains(3));
        l.remove(0); l.remove(0);
        assertFalse(l.contains(3));
    }

    @Test
    void firstAndLastIndex() {
        IntList l = create();
        for (int i = 0; i < 5; i++) l.add(i);          // 0 1 2 3 4
        assertEquals(0, l.get(0));
        assertEquals(4, l.get(4));
        l.add(0, 100);                                  // голова
        l.add(l.size(), 200);                           // индекс == size разрешён
        assertEquals(100, l.get(0));
        assertEquals(200, l.get(l.size() - 1));
        assertEquals(200, l.remove(l.size() - 1));
        assertEquals(100, l.remove(0));
    }

    @Test
    void invalidIndex() {
        IntList l = create();
        l.add(1);
        assertThrows(IndexOutOfBoundsException.class, () -> l.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> l.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> l.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> l.add(2, 5));
    }

    @Test
    void growsPastInitialCapacity() {
        IntList l = create();
        for (int i = 0; i < 1000; i++) l.add(i);
        assertEquals(1000, l.size());
        for (int i = 0; i < 1000; i++) assertEquals(i, l.get(i));
    }

    @Test
    void randomOperationsMatchArrayList() {
        Random rng = new Random(1);
        IntList mine = create();
        List<Integer> expected = new ArrayList<>();
        for (int step = 0; step < 5000; step++) {
            int x = rng.nextInt(50);
            switch (rng.nextInt(4)) {
                case 0 -> { mine.add(x); expected.add(x); }
                case 1 -> {
                    int i = rng.nextInt(expected.size() + 1);
                    mine.add(i, x);
                    expected.add(i, x);
                }
                case 2 -> {
                    if (!expected.isEmpty()) {
                        int i = rng.nextInt(expected.size());
                        assertEquals(expected.remove(i).intValue(), mine.remove(i));
                    }
                }
                default -> assertEquals(expected.contains(x), mine.contains(x));
            }
            assertEquals(expected.size(), mine.size());
        }
        for (int i = 0; i < expected.size(); i++)
            assertEquals(expected.get(i).intValue(), mine.get(i));
    }

    @Test
    void countersAreNotZeroAndCanBeReset() {
        IntList l = create();
        for (int i = 0; i < 100; i++) l.add(i);
        l.resetMetrics();
        assertEquals(0, l.getMoves());
        l.add(0, 999);                                  // вставка в голову
        assertTrue(l.getMoves() > 0, "moves must be counted for head insert");
        l.resetMetrics();
        l.contains(-1);
        assertTrue(l.getComparisons() >= 100);
    }
}