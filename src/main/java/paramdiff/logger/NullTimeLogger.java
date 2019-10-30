package paramdiff.logger;

public class NullTimeLogger implements TimeLogger {
    @Override
    public void start() {
    }

    @Override
    public void startLap() {
    }

    @Override
    public void logLap(String message) {
    }

    @Override
    public void logTotal(String message) {
    }
}

