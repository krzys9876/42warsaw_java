package org.example;

import java.util.*;

public class LottoNumbers2 {
    private final List<Integer> numbers = new ArrayList<>();

    private static final Random generator = new Random();
    public static void setSeed(long i) { generator.setSeed(i);}

    public List<Integer> getNumbers() { return numbers; }

    public LottoNumbers2() {
        numbers.addAll(drawAll());
        numbers.sort(Comparator.naturalOrder());
    }

    public LottoNumbers2(int[] n) {
        for (int j : n) numbers.add(j);
        numbers.sort(Comparator.naturalOrder());
    }

    private static int drawOne() {
        return generator.nextInt(1, 50);
    }

    private static List<Integer> drawAll() {
        List<Integer> numbers = new ArrayList<>();
        while(numbers.size() < 6) {
            int number = drawOne();
            if(!numbers.contains(number)) numbers.add(number);
        }
        return numbers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LottoNumbers2 other)) return false;
        return numbers.equals(other.numbers);
    }

    @Override
    public int hashCode() { return Objects.hash(numbers); }
}
