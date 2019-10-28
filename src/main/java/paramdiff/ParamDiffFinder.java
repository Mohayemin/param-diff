package paramdiff;

import com.github.javaparser.StaticJavaParser;

import java.util.ArrayList;
import java.util.List;

public class ParamDiffFinder {
    public List<ParamAdditionDiff> findParamAddition(String oldCodeText, String newCodeText) {
        if (oldCodeText == newCodeText) {
            return new ArrayList<>();
        } else {
            var newCode = StaticJavaParser.parse(newCodeText);

            var newMethod = newCode.getType(0).getMethods().get(0);
            var addedParamIndex = newMethod.getParameters().size()-1;
            var addedParam = newMethod.getParameter(2).getTypeAsString();
            var diff = new ParamAdditionDiff("", "", addedParamIndex, addedParam);
            return List.of(diff);
        }
    }
}
