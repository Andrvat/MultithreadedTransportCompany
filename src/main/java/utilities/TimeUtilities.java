package utilities;

public class TimeUtilities {
    public static long convertSecsToMillis(long sec) {
        return sec * 1000;
    }

    public static long convertMillisToSecs(long millis) {
        return (long) Math.ceil((double) millis / 1000);
    }
}
