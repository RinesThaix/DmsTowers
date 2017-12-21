/**
 * Created by RINES on 24.05.17.
 */
public class RatingTester {

    public static void main(String[] args) {
//        for(int r = 500; r <= 4000; r += 250)
//            show(r);
        for(int i = 1000; i <= 4000; i += 100)
            System.out.println(String.format("%d kills %d: %d", i, 1000, calculateRatingDelta(i, 1000)));
        System.out.println("----------------");
        for(int i = 1000; i <= 4000; i += 100)
            System.out.println(String.format("%d kills %d: %d", 1000, i, calculateRatingDelta(1000, i)));
    }

    private static void show(int rating) {
        System.out.println(String.format("%04d (dead): %02d", rating, calculateRatingDelta(rating, true)));
        System.out.println();
    }

    private static int calculateRatingDelta(int killer, int target) {
        double delta = 1D / (1 + Math.pow(10, (killer - target) / 1000D));
        double k = delta < 0 ? 10 : 5 + Math.PI * (4000 - target) / 500;
        int result = (int) Math.round(k * delta);
        System.out.println(String.format("%.3f %.3f %d", delta, k, result));
        if(result <= 0)
            result = 1;
        return result;
    }

    private static int calculateRatingDelta(int rating, boolean died) {
        int average = 1000, kills = 2, playersOnStart = 8;
        if(average == -1)
            return 0;
        double probability = 1D / (1 + Math.pow(10, (average - rating) / 1000D));
        double expectedAmountOfKills = 0.3D * (playersOnStart - 1) / 9 * Math.pow(Math.E, 3 * probability);
        expectedAmountOfKills = Math.min(playersOnStart - 1, expectedAmountOfKills);
        if(playersOnStart > 14)
            expectedAmountOfKills /= 1.44D;
        else if(playersOnStart > 7)
            expectedAmountOfKills /= 1.22D;
        if(!died)
            expectedAmountOfKills /= 2;
        double delta = kills - expectedAmountOfKills;
        double k = delta < 0 ? 10 : 5 + Math.PI * (4000 - rating) / 500;
        int result = (int) Math.round(k * delta);
        if(!died && result < 0)
            result = 0;
        System.out.println("Ожидаемое кол-во убийств: " + String.format("%.2f", expectedAmountOfKills));
        return result;
    }
}
