package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LogicalProgrammingTest {

    @Test
    void testLoopCalculations() {
        int a = 0;
        int b = 0;
        int c = 0;
        int d = 0;
        int e = 0;
        int countB = 0;
        int countC = 0;
        int countD = 0;
        int countE = 0;

        for (int i = 0; i <= 10; i++) {
            if (i % 4 == 0) {
                b = b + i + c - e;
                // kondisi untuk menghindari ketika nilai i = 0 dan nilai variable tidak mengalami perubahan tetap 0
                if (i != 0 && b != 0) countB += 1; 
            }
            if (i % 3 == 0) {
                c = c + (i + b) - d;
                // kondisi untuk menghindari ketika nilai i = 0 dan nilai variable tidak mengalami perubahan tetap 0
                if (i != 0 && c != 0) countC += 1;
            }
            if (i % 2 == 0) {
                d = d + (i + b) - c;
                // kondisi untuk menghindari ketika nilai i = 0 dan nilai variable tidak mengalami perubahan tetap 0
                if (i != 0 && d != 0) countD += 1;
            }
            if (i % 1 == 0) {
                e = i + b + c;
                // kondisi untuk menghindari ketika nilai i = 0 dan nilai variable tidak mengalami perubahan tetap 0
                if (i != 0 && e != 0) countE += 1;
            }
            a = i;
        }

        // hasil akhir
        assertEquals(10, a);
        assertEquals(1, b);
        assertEquals(-6, c);
        assertEquals(35, d);
        assertEquals(5, e);

        // Assertions for counts
        assertEquals(2, countB);  // multiples of 4 (4, 8)
        assertEquals(3, countC);  // multiples of 3 (3, 6, 9)
        assertEquals(5, countD);  // multiples of 2 (2, 4, 6, 8, 10)
        assertEquals(10, countE); // always runs
    }
}
