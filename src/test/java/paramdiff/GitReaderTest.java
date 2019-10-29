package paramdiff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

// This test class assumes that "mockito" downloaded repository at revision a9c5105f8fda310a00b6d784a8bf9fb694a94610
public class GitReaderTest {
    @Test
    public void readAllHash() throws IOException {
        var gitReader = new GitReader("src/test/resources/git_repo/mockito");
        var hashes = gitReader.getAllHashes();
        Assertions.assertEquals(5163, hashes.size());
    }
}
