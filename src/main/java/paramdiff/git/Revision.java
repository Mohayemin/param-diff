package paramdiff.git;

import util.Command;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class Revision {
    private Repository repository;
    private String hash;

    public Revision(Repository repository, String hash) {
        this.repository = repository;
        this.hash = hash;
    }

    public List<String> getChangedJavaFiles() throws IOException {
        var commandText = String.format("git diff \"%s^!\" --name-only", hash);
        var filePaths = new Command(commandText)
                .run(repository.localFile)
                .readLines().stream()
                .filter(fp->fp.endsWith(".java"))
                .collect(Collectors.toList());

        return filePaths;
    }

    public String readFile(String filePath) throws IOException {
        String commandText = String.format("git show \"%s:%s\"", hash, filePath);
        var lines = new Command(commandText)
                .run(repository.localFile)
                .readLines();

        var content = String.join("\n", lines);
        return content;
    }
}
