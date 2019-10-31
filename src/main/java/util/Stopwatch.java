package util;

public class Stopwatch {
    private long startTime;

    public Stopwatch start() {
        startTime = System.nanoTime();
        return this;
    }

    public long elapsedNanos(){
        return System.nanoTime() - startTime;
    }
}
