package paramdiff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import paramdiff.git.Repository;
import paramdiff.git.Revision;

import java.io.File;
import java.io.IOException;

// This test class assumes that "mockito" downloaded repository at revision a9c5105f8fda310a00b6d784a8bf9fb694a94610
public class RevisionTest {

    private File repoFile = new File("../ParamDiffData/mockito");

    /*
    @Test
    public void readAllHash() throws IOException {
        var gitReader = new Revision(repositoryPath);
        var hashes = gitReader.getAllHashes();
        Assertions.assertEquals(5163, hashes.size());
    }
    */

    @Test
    public void getChangedFiles() throws IOException {
        var repository = new Repository("", repoFile);
        var revision = new Revision(repository, "dfc08acdbfbbf979064af4a84c39f1b64df3239a");
        var filePaths = revision.getChangedJavaFiles();
        Assertions.assertEquals(5, filePaths.size());
        Assertions.assertTrue(filePaths.contains("src/main/java/org/mockito/internal/configuration/SpyAnnotationEngine.java"));
    }

    @Test
    public void readFile() throws IOException {
        var repository = new Repository("", repoFile);
        var revision = new Revision(repository, "dfc08acdbfbbf979064af4a84c39f1b64df3239a");
        var fileContent = revision.readFile("src/main/java/org/mockito/internal/configuration/SpyAnnotationEngine.java");
        Assertions.assertTrue(fileContent.contains("class SpyAnnotationEngine implements AnnotationEngine"));
    }
}