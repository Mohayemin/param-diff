package paramdiff.git;

import util.Command;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class GitReader {
    final String repositoryPath;

    public GitReader(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public List<String> getAllHashes() throws IOException {
        var hashes = new Command("git log --pretty=format:\"%H\"")
                .run(new File(this.repositoryPath))
                .readLines();
        return hashes;
    }

    public List<String> getChangedJavaFiles(String hash) throws IOException {
        var commandText = "git diff \"" + hash + "^!\" --name-only";
        var filePaths = new Command(commandText)
                .run(new File(this.repositoryPath))
                .readLines().stream()
                .filter(fp->fp.endsWith(".java"))
                .collect(Collectors.toList());

        return filePaths;
    }

    public String readFile(String hash, String filePath) throws IOException {
        var command = new Command(String.format("git show \"%s:%s\"", hash, filePath));
        var lines = command.run(new File(this.repositoryPath)).readLines();
        var content = String.join("\n", lines);
        return content;
    }
}
