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

    @Test
    public void findParamAddition_addParamAtEnd() throws IOException {
        var code1 = getCalculatorCode(1);
        var code2 = getCalculatorCode(2);
        var paramDiff = new ParamDiffFinder().findParamAddition(code1, code2);
        Assertions.assertEquals(1, paramDiff.size());
        Assertions.assertEquals(1, paramDiff.get(0).indexOfAddedParam);
        Assertions.assertEquals(int.class, paramDiff.get(0).typeOfAddedParam);
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