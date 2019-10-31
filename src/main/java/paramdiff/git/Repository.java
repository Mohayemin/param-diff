package paramdiff.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Repository {
    private String remoteUrl;
    private File localFile;

    public Repository(String remoteUrl, String localPath) {
        this.remoteUrl = remoteUrl;
        this.localFile = Path.of(localPath).toFile();
    }

    public Process update() throws IOException {
        var runtime = Runtime.getRuntime();
        if (localFile.exists()) {
            return runtime.exec("git pull", null, this.localFile);
        } else {
            this.localFile.mkdirs();
            return runtime.exec(String.format("git clone \"%s\" \"%s\"", remoteUrl, localFile.getAbsolutePath()), null, this.localFile);
        }
    }
}
