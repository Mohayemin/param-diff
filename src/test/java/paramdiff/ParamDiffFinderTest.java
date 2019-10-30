package paramdiff;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ParamDiffFinderTest {
    @Test
    public void findParamAddition_nodiff_emptyList() {
        var code = getTestData("Calculator1");
        var paramDiff = new ParamDiffFinder().findParamAddition(code, code);
        Assertions.assertEquals(0, paramDiff.size());
    }

    @Test
    public void findParamAddition_addParamAtEnd_int() {
        var code1 = getTestData("Calculator1");
        var code2 = getTestData("Calculator2");
        var paramDiff = new ParamDiffFinder().findParamAddition(code1, code2);
        Assertions.assertEquals(1, paramDiff.size());
    }

    @Test
    public void findParamAddition_addParamAtEnd_double() {
        var code1 = getTestData("Calculator1");
        var code3 = getTestData("Calculator3");
        var paramDiff = new ParamDiffFinder().findParamAddition(code1, code3);
        Assertions.assertEquals(1, paramDiff.size());
    }

    @Test
    public void findParamAddition_addFourthParamAtEnd() {
        var code2 = getTestData("Calculator2");
        var code4 = getTestData("Calculator4");
        var paramDiff = new ParamDiffFinder().findParamAddition(code2, code4);
        Assertions.assertEquals(1, paramDiff.size());
    }

    @Test
    public void findParamAddition_addParamAtStart() {
        var code2 = getTestData("Calculator2");
        var code4 = getTestData("Calculator4");
        var paramDiff = new ParamDiffFinder().findParamAddition(code2, code4);
        Assertions.assertEquals(1, paramDiff.size());
    }

    @Test
    public void findParamAddition_addTwoNewParams(){
        var code1 = getTestData("Calculator1");
        var code5 = getTestData("Calculator5");
        var paramDiff = new ParamDiffFinder().findParamAddition(code1, code5);
        Assertions.assertEquals(1, paramDiff.size());
    }

    @Test
    public void findParamAddition_2methods(){
        var code1 = getTestData("MultiMethod1");
        var code2 = getTestData("MultiMethod2");
        var paramDiff = new ParamDiffFinder().findParamAddition(code1, code2);
        Assertions.assertEquals(2, paramDiff.size());
    }

    @Test
    public void findParamAddition_oneInOldTwoInNew(){
        var code1 = getTestData("Calculator1");
        var code2 = getTestData("MultiMethod2");
        var paramDiff = new ParamDiffFinder().findParamAddition(code1, code2);
        Assertions.assertEquals(1, paramDiff.size());
    }

    @Test
    public void findParamAddition_oneOfTwoMethodsChange(){
        var oldCode = getTestData("MultiMethod1");
        var newCode = getTestData("MultiMethod3");
        var paramDiff = new ParamDiffFinder().findParamAddition(oldCode, newCode);
        Assertions.assertEquals(1, paramDiff.size());
        Assertions.assertEquals("add", paramDiff.get(0).oldMethod.getNameAsString());
    }

    @Test
    public void findParamAddition_newMethodAdded_noDiff(){
        var oldCode = getTestData("MultiMethod1");
        var newCode = getTestData("MultiMethod_NewMethodAdded");
        var paramDiff = new ParamDiffFinder().findParamAddition(oldCode, newCode);
        Assertions.assertEquals(0, paramDiff.size());
    }

    @Test
    public void findParamAddition_noSuchClass_noChange(){
        var oldCode = getTestData("MultiMethod1");
        var newCode = getTestData("NoClass");
        var paramDiff = new ParamDiffFinder().findParamAddition(oldCode, newCode);
        Assertions.assertEquals(0, paramDiff.size());
    }

    @Test
    public void findParamAddition_errorInFile_noChange(){
        var oldCode = getTestData("ErrorInFile");
        var newCode = getTestData("MultiMethod1");
        var paramDiff = new ParamDiffFinder().findParamAddition(oldCode, newCode);
        Assertions.assertEquals(0, paramDiff.size());
    }

    @Test
    public void findParam_realData_noChange(){
        var oldCode = getTestData("v_old");
        var newCode = getTestData("v_new");
        var paramDiff = new ParamDiffFinder().findParamAddition(oldCode, newCode);
        Assertions.assertEquals(0, paramDiff.size());
    }

    private String getTestData(String fileName) {
        var filePath = new File("src/test/resources/test_data/" + fileName + ".java").getAbsolutePath();
        try {
            String code = Files.readString(Paths.get(filePath));
            return code;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
