package paramdiff;

import com.beust.jcommander.Parameter;

import java.io.File;
import java.nio.file.Path;

public class CLIParams {
    @Parameter(names = "-url", description = "URL of the git repository.")
    public String gitUrl;

    @Parameter(names = "-outdir", description = "Directory for program output. Defaults to `out`.")
    public String outPath = "out";

    @Parameter(names = {"-rn"}, description = "A short name of repository. Calculated from `url` if not specified.")
    public String repositoryName;

    public File localRepositoryDir;
    public File outDir;

    public void init() {
        if (repositoryName == null){
            if (gitUrl == null)
                throw new IllegalArgumentException("At least one of `url` and `rn` arguments must be passed");
            else {
                var segments = gitUrl.split("/");
                repositoryName = segments[segments.length - 1];
            }
        }

        outDir = Path.of(outPath).toFile();
        localRepositoryDir = Path.of(outPath, repositoryName).toFile();
    }

    public String asString() {
        var str = "Remote repository: " + (gitUrl == null ? "none" : gitUrl);
        str += "\nLocal repository: " + localRepositoryDir.getAbsolutePath();
        return str;
    }
}
