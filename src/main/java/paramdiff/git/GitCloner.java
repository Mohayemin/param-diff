package paramdiff.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class GitCloner {
    private String gitUrl;
    private File repositoryFile;

    public GitCloner(String gitUrl, String repositoryPath) {
        this.gitUrl = gitUrl;
        this.repositoryFile = Path.of(repositoryPath).toFile();
    }

    public void cloneRepo() throws IOException {
        var runtime = Runtime.getRuntime();
        if (repositoryFile.exists()) {
            runtime.exec("git pull", null, this.repositoryFile);
        } else {
            this.repositoryFile.mkdirs();
            runtime.exec(String.format("git clone \"%s\" \"%s\"", gitUrl, repositoryFile.getAbsolutePath()), null, this.repositoryFile);
        }
    }
}
