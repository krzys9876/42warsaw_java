package org.example;

import java.util.Arrays;
import java.util.Random;

public record LottoNumbers(int[] numbers) {
    private static final Random generator = new Random();

    public static void setSeed(long seed) {
        generator.setSeed(seed);
    }

    public LottoNumbers() {
        this(drawAll());
    }

    private static int drawOne() {
        return generator.nextInt(1, 50);
    }

    private static int[] drawAll() {
        int[] n = new int[6];
        for (int i = 0; i < n.length; i++) {
            n[i] = drawOne();
        }
        Arrays.sort(n);
        long distinct = Arrays.stream(n).distinct().count();
        if(distinct != 6) return drawAll();
        return n;
    }

    public LottoNumbers(int[] numbers) {
        int[] copy = Arrays.copyOf(numbers, numbers.length);
        Arrays.sort(copy);
        this.numbers = copy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof LottoNumbers(int[] numbers1))) return false;
        return Arrays.equals(numbers, numbers1);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(numbers);
    }
}
