package org.example.test;

import org.example.LottoNumbers2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestLotto2 {
    @BeforeEach
    public void setup() {
        LottoNumbers2.setSeed(1234);
    }

    @Test
    public void shouldGenerate6Numbers() {
        LottoNumbers2 ln = new LottoNumbers2();
        assertEquals(6, ln.getNumbers().size(), "there should be exactly 6 numbers");
    }

    @Test
    public void shouldNumbersBeUnique() {
        for (int i = 0; i < 100; i++) {
            LottoNumbers2 ln = new LottoNumbers2();
            Set<Integer> numbersUnique = new HashSet<>(ln.getNumbers());
            assertEquals(ln.getNumbers().size(), numbersUnique.size(), "all numbers should be unique");
        }
    }

    @Test
    public void shouldSameNumbersSameOrderBeEqual() {
        LottoNumbers2 ln1 = new LottoNumbers2(new int[] {1,2,3,4,5,6});
        LottoNumbers2 ln2 = new LottoNumbers2(new int[] {1,2,3,4,5,6});
        assertEquals(ln1, ln2, "same elements, same order");
    }

    @Test
    public void shouldSameNumbersDifferentOrderBeEqual() {
        LottoNumbers2 ln1 = new LottoNumbers2(new int[] {1,2,3,4,5,6});
        LottoNumbers2 ln2 = new LottoNumbers2(new int[] {1,3,2,4,5,6});
        assertEquals(ln1, ln2, "same elements, different order");
    }

    @Test
    public void shouldGenerateRandomNumbers() {
        ArrayList<LottoNumbers2> ln = new ArrayList<>();
        for (int i = 0; i < 1000; i++) { ln.add(new LottoNumbers2()); }
        HashSet<LottoNumbers2> uniqueNumbers = new HashSet<>(ln);
        assertEquals(uniqueNumbers.size(), ln.size(), "all number sequences should be unique");
    }
}
