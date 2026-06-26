package org.example.bruteforce;

import java.util.Arrays;

public record LottoResult(int[] n) {
    boolean isSame(LottoResult other) {
        return Arrays.equals(this.n, other.n);
    }

    public String toString() {
        return Arrays.toString(this.n);
    }
}