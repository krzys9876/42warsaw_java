package org.example.test;

import org.example.LottoNumbers;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestLotto {
    @Test
    public void shouldDrawExactlySixNumbers() {
        LottoNumbers draw = new LottoNumbers();
        int[] numbers = draw.numbers();
        assertEquals(6, numbers.length, "there should be 6 numbers in a draw");
    }

    @Test
    public void shouldAllNumbersBeUnique() {
        for(int i=0; i<100; i++) {
            LottoNumbers draw = new LottoNumbers();
            int[] numbers = draw.numbers();
            List<Integer> distinctNumbers = Arrays.stream(numbers).boxed().distinct().toList();
            assertEquals(numbers.length, distinctNumbers.size(), "numbers should be unique");
        }
    }

    @Test
    public void shouldEnableDrawComparison() {
        LottoNumbers draw1 = new LottoNumbers(new int[]{2,3,4,5,6,7});
        LottoNumbers draw2 = new LottoNumbers(new int[]{2,4,3,5,6,7});
        assertEquals(draw1, draw2, "draws with the same numbers should be considered equal");
    }

    @Test
    public void shouldDrawDifferentNumbers() {
        int size = 1000;
        LottoNumbers.setSeed(1234);
        LottoNumbers[] draws = new LottoNumbers[size];
        for (int i = 0; i < size; i++) { draws[i] = new LottoNumbers(); }
        List<LottoNumbers> drawListDistinct = Arrays.stream(draws).distinct().toList();
        assertEquals(draws.length, drawListDistinct.size(), "random draws should be different");
    }
}
