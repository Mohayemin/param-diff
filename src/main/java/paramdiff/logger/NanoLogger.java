package paramdiff.logger;

public class NanoLogger implements TimeLogger{
    private long startTime;
    private long lapStartTime;

    public void start() {
        startTime = System.nanoTime();
    }

    public void startLap(){
        lapStartTime = System.nanoTime();
    }

    public void logLap(String message) {
        var lapEndTime = System.nanoTime();
        System.out.printf("%s: %s\n", message, formatDuration(lapEndTime - lapStartTime));
    }

    public void logTotal(String message){
        var endTime = System.nanoTime();
        System.out.printf("%s: %s\n", message, formatDuration(endTime - startTime));
    }

    private String formatDuration(long nanoSeconds){
        var seconds = nanoSeconds / 1e9;
        return String.format("%ss", seconds);
    }
}

