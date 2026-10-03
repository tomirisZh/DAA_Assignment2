package workload;

import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapTest {
    @Test
    void emptyHeapThrows() {
        MinHeap h = new MinHeap();
        assertThrows(IllegalStateException.class, h::peekMin);
        assertThrows(IllegalStateException.class, h::extractMin);
    }

    @Test
    void singleElement() {
        MinHeap h = new MinHeap();
        h.insert(5);
        assertEquals(5, h.peekMin());
        assertEquals(5, h.extractMin());
        assertTrue(h.isEmpty());
    }

    @Test
    void heapPropertyAfterEveryOperation() {
        Random rng = new Random(2);
        MinHeap h = new MinHeap();
        for (int i = 0; i < 1000; i++) {
            h.insert(rng.nextInt(100));                 // много дубликатов
            assertTrue(h.isValidHeap(), "after insert " + i);
        }
        while (!h.isEmpty()) {
            h.extractMin();
            assertTrue(h.isValidHeap(), "after extract");
        }
    }

    @Test
    void sortedOutputMatchesPriorityQueue() {
        Random rng = new Random(42);
        MinHeap h = new MinHeap();
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int i = 0; i < 10_000; i++) {
            int x = rng.nextInt(2_000_001) - 1_000_000;  // включая отрицательные
            h.insert(x);
            pq.add(x);
        }
        int prev = Integer.MIN_VALUE;
        while (!h.isEmpty()) {
            int m = h.extractMin();
            assertTrue(m >= prev);
            assertEquals(pq.poll().intValue(), m);
            prev = m;
        }
    }

    @Test
    void peekDoesNotRemove() {
        MinHeap h = new MinHeap();
        h.insert(3); h.insert(1); h.insert(2);
        assertEquals(1, h.peekMin());
        assertEquals(3, h.size());
    }
}