package org.example.bruteforce;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public class Lotto {
    static void main() {
        while(true) {
            double speed = oneIteration(false);
            IO.println(speed);
        }
    }

    static double oneIteration(boolean printResults) {
        long iteration = 0L;
        boolean finished = false;
        LottoResult result = makeRandom();
        LocalDateTime now = LocalDateTime.now();
        if(printResults) IO.println("Searcching for: "+result);
        while (!finished) {
            LottoResult otherResult = makeRandom();
            if (result.isSame(otherResult)) {
                if(printResults) IO.println("Found: "+otherResult+" iteration: "+iteration+" duplicates: "+duplicates);
                finished = true;
            }
            iteration++;
            /*if (iteration % 1000000 == 0) {
                IO.println("Iteration "+iteration);
            }*/
        }
        LocalDateTime now2 = LocalDateTime.now();
        Long millis = ChronoUnit.MILLIS.between(now, now2);
        double speed = millis / (iteration / 1000000.0);
        if(printResults) IO.println("Elapsed s: "+ (millis / 1000.0) + " speed [ms/M]: " + speed);
        return speed;
        /*for(int i = 0; i < 10; i++) {
            LottoResult r = makeRandom();
            IO.println(r);
        }*/
    }

    static long duplicates = 0;

    static LottoResult makeRandom() {
        int[] numbers = new int[6];
        for(int i = 0; i < numbers.length; i++) {
            numbers[i] = (int) (Math.random() * 49 + 1);
        }
        Arrays.sort(numbers);
        for(int i = 0; i < numbers.length -1; i++) {
            if(numbers[i] == numbers[i+1]) {
                duplicates++;
                return makeRandom();
            }
        }
        return new LottoResult(numbers);
    }
}
