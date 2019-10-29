package paramdiff;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class GitReader {
    final String repositoryPath;

    public GitReader(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    public List<String> getAllHashes() throws IOException {
        var command = "git log --pretty=format:\"%H\"";
        var runtime = Runtime.getRuntime();
        var process = runtime.exec(command, null, new File(this.repositoryPath));;
        var output = process.getInputStream();
        var hashes = new BufferedReader(new InputStreamReader(output)).lines().collect(Collectors.toList());
        return hashes;
    }
}
