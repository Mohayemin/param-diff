package paramdiff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import paramdiff.git.GitReader;

import java.io.IOException;

// This test class assumes that "mockito" downloaded repository at revision a9c5105f8fda310a00b6d784a8bf9fb694a94610
public class GitReaderTest {

    private String repositoryPath = "../ParamDiffData/mockito";

    /*
    @Test
    public void readAllHash() throws IOException {
        var gitReader = new GitReader(repositoryPath);
        var hashes = gitReader.getAllHashes();
        Assertions.assertEquals(5163, hashes.size());
    }
    */

    @Test
    public void getChangedFiles() throws IOException {
        var gitReader = new GitReader(repositoryPath);
        var filePaths = gitReader.getChangedJavaFiles("dfc08acdbfbbf979064af4a84c39f1b64df3239a");
        Assertions.assertEquals(5, filePaths.size());
        Assertions.assertTrue(filePaths.contains("src/main/java/org/mockito/internal/configuration/SpyAnnotationEngine.java"));
    }

    @Test
    public void readFile() throws IOException {
        var gitReader = new GitReader(repositoryPath);
        var fileContent = gitReader.readFile("dfc08acdbfbbf979064af4a84c39f1b64df3239a",
                "src/main/java/org/mockito/internal/configuration/SpyAnnotationEngine.java");
        Assertions.assertTrue(fileContent.contains("class SpyAnnotationEngine implements AnnotationEngine"));
    }
}
