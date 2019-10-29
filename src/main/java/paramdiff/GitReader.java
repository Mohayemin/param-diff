package paramdiff;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class GitReader {
    final String repositoryPath;

    public GitReader(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public List<String> getAllHashes() throws IOException {
        var output = runCommand("git log --pretty=format:\"%H\"");
        var hashes = readLines(output);
        return hashes;
    }

    public List<String> getChangedFiles(String hash) throws IOException {
        var command = "git diff \"" + hash + "^!\" --name-only";
        var output = runCommand(command);
        var filePaths = readLines(output);
        return filePaths;
    }

    private List<String> readLines(InputStream output) {
        return new BufferedReader(new InputStreamReader(output)).lines().collect(Collectors.toList());
    }

    private InputStream runCommand(String command) throws IOException {
        var runtime = Runtime.getRuntime();
        var process = runtime.exec(command, null, new File(this.repositoryPath));
        return process.getInputStream();
    }
}
