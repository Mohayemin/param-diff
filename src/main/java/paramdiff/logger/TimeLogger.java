package paramdiff.logger;

public interface TimeLogger {
    void start();
    void startLap();
    void logLap(String message);
    void logTotal(String message);
}
