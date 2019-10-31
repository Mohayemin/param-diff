package paramdiff.git;

import util.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Repository {
    public final String remoteUrl;
    public final File localFile;

    public Repository(String remoteUrl, String localPath) {
        this.remoteUrl = remoteUrl;
        this.localFile = Path.of(localPath).toFile();
    }

    public Repository update() throws IOException {
        var runtime = Runtime.getRuntime();
        if (localFile.exists()) {
            runtime.exec("git pull", null, this.localFile);
        } else {
            this.localFile.mkdirs();
            runtime.exec(String.format("git clone \"%s\" \"%s\"", remoteUrl, localFile.getAbsolutePath()), null, this.localFile);
        }

        return this;
    }

    public List<String> getAllHashes() throws IOException {
        var hashes = new Command("git log --pretty=format:\"%H\"")
                .run(localFile)
                .readLines();
        return hashes;
    }
}
