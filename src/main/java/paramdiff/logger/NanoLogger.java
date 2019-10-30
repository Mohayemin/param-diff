package paramdiff.logger;

public class NanoLogger implements TimeLogger{
    private long startTime;
    private long lapStartTime;

    public void start() {
        startTime = System.nanoTime();
    }

    public void startLap(){
        lapStartTime = startTime;
    }

    public void logLap(String message) {
        var lapEndTime = System.nanoTime();
        System.out.printf("%s: %s\n ns", message, lapEndTime - lapStartTime);
    }

    public void logTotal(String message){
        var endTime = System.nanoTime();
        System.out.printf("%s: %s\n ns", message, endTime - startTime);
    }
}

