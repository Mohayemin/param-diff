package util;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class Command {
    private String commandText;
    private Process process;

    public Command(String commandText) {
        this.commandText = commandText;
    }

    public Command run(File executionDirectory) throws IOException {
        var runtime = Runtime.getRuntime();
        this.process = runtime.exec(commandText, null, executionDirectory);
        return this;
    }

    public List<String> readLines() {
        return new BufferedReader(new InputStreamReader(this.process.getInputStream())).lines().collect(Collectors.toList());
    }
}
