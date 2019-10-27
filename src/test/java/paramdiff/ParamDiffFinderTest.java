package paramdiff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ParamDiffFinderTest {
    @Test
    public void findParamAddition_nodiff_emptyList() throws IOException {
        var code = getCalculatorCode(1);
        var paramDiff = new ParamDiffFinder().findParamAddition(code, code);
        Assertions.assertEquals(0, paramDiff.size());
    }

    private String getCalculatorCode(int suffix) {
        var filePath = new File("src/test/resources/test_data/Calculator" + suffix + ".java").getAbsolutePath();
        try {
            String code = Files.readString(Paths.get(filePath));
            return code;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
