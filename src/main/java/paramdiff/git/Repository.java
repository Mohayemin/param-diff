package paramdiff.git;

import util.Command;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public class Repository {
    public final String remoteUrl;
    public final File localFile;

    public Repository(String remoteUrl, File localFile) {
        this.remoteUrl = remoteUrl;
        this.localFile = localFile;
    }

    public Repository update() throws IOException, InterruptedException {
        var runtime = Runtime.getRuntime();
        Process process;
        System.out.println("Updating repository. This can take some time.");
        if (localFile.exists()) {
            process = runtime.exec("git pull", null, this.localFile);
        } else {
            this.localFile.mkdirs();
            process = runtime.exec(String.format("git clone \"%s\" \"%s\"", remoteUrl, localFile.getAbsolutePath()), null, this.localFile);
        }

        process.waitFor();
        System.out.println("Done updating repository.");

        return this;
    }

    public Stream<Revision> getAllRevisions() throws IOException {
        var revisions = new Command("git log --pretty=format:\"%H\"")
                .run(localFile)
                .readLines()
                .stream()
                .map(h -> new Revision(this, h));
        return revisions;
    }
}
