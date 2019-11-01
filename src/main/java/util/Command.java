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

    public List<String> readLines() throws IOException {
        var isr = new InputStreamReader(this.process.getInputStream());
        var br = new BufferedReader(isr);
        var lines = br.lines().collect(Collectors.toList());

        this.process.getInputStream().close();
        isr.close();
        br.close();
        
        return lines;
    }
}
