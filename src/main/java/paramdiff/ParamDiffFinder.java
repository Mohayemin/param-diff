package paramdiff;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParamDiffFinder {
    public List<ParamAdditionDiff> findParamAddition(String oldCodeText, String newCodeText) {
        var oldCode = StaticJavaParser.parse(oldCodeText);
        var newCode = StaticJavaParser.parse(newCodeText);

        var oldTypes = oldCode.getTypes();
        var newTypes = newCode.getTypes();

        for (var oldType : oldTypes) {
            var newType = findMatchingClass(newTypes, oldType);
            if (newType == null)
                continue;

            var paramModifiedMethods = new ArrayList<MethodDeclaration>();
            List<MethodDeclaration> newMethods = newType.getMethods();
            for (var oldMethod : oldType.getMethods()) {
                var newMethod = findMatchingMethod(newType, oldMethod);
                if (newMethod != null) {
                    newMethods.remove(newMethod);
                } else {
                    paramModifiedMethods.add(oldMethod);
                }
            }

            for (var oldMethod : paramModifiedMethods) {
                var addedParams = newMethods.stream()
                        .filter(m -> m.getNameAsString() == oldMethod.getNameAsString())
                        .filter(m -> m.getParameters().size() > oldMethod.getParameters().size());
                if (addedParams.count() == 0)
                    continue;

                System.out.println(oldMethod.getDeclarationAsString());
            }
        }

        return List.of();
    }

    private MethodDeclaration findMatchingMethod(TypeDeclaration type, MethodDeclaration method) {
        var params = method.getParameters().stream()
                .map(parameter -> parameter.getNameAsString())
                .collect(Collectors.toList()).toArray(new String[0]);
        List<MethodDeclaration> mathichMethodList = type.getMethodsBySignature(method.getNameAsString(), params);

        if (mathichMethodList.size() > 0) {
            return mathichMethodList.get(0);
        }
        return null;
    }

    private TypeDeclaration findMatchingClass(NodeList<TypeDeclaration<?>> typeList, TypeDeclaration typeToFind) {
        System.out.println(typeList.stream().map(tl->tl.getFullyQualifiedName().get()).collect(Collectors.toList()));
        return typeList.stream()
                .filter(nt -> nt.getFullyQualifiedName().get().equals(typeToFind.getFullyQualifiedName().get())).findFirst().get();
    }


}
