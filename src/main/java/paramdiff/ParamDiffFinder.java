package paramdiff;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.Type;
import util.Sequences;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParamDiffFinder {
    public List<ParamAdditionDiff> findParamAddition(String oldCodeText, String newCodeText) {
        try {
            var oldCode = StaticJavaParser.parse(oldCodeText);
            var newCode = StaticJavaParser.parse(newCodeText);
            return findParamAddition(oldCode, newCode);
        } catch (ParseProblemException e) {
            return new ArrayList<>();
        }
    }

    private List<ParamAdditionDiff> findParamAddition(CompilationUnit oldCode, CompilationUnit newCode) {
        var oldTypes = oldCode.getTypes();
        var newTypes = newCode.getTypes();
        var diffs = new ArrayList<ParamAdditionDiff>();
        for (var oldType : oldTypes) {
            var newType = findMatchingClass(newTypes, oldType);
            if (newType != null)
                diffs.addAll(findParamAdditions(oldType, newType));
        }

        return diffs;
    }

    private List<ParamAdditionDiff> findParamAdditions(TypeDeclaration<?> oldType, TypeDeclaration newType) {
        if (newType == null)
            return List.of();

        var paramModifiedMethods = new ArrayList<MethodDeclaration>();
        List<MethodDeclaration> newMethods = new ArrayList<>(newType.getMethods());
        for (var oldMethod : oldType.getMethods()) {
            var newMethod = findMatchingMethod(newType, oldMethod);
            if (newMethod != null) {
                newMethods.remove(newMethod);
            } else {
                paramModifiedMethods.add(oldMethod);
            }
        }

        var diffs = new ArrayList<ParamAdditionDiff>();
        for (var oldMethod : paramModifiedMethods) {
            var addedParams = newMethods.stream()
                    .filter(m -> m.getNameAsString().equals(oldMethod.getNameAsString()))
                    .filter(m -> isParamSuperSet(m, oldMethod))
                    .collect(Collectors.toList());
            if (addedParams.size() == 0)
                continue;

            diffs.add(new ParamAdditionDiff(oldMethod, addedParams.get(0)));
        }

        return diffs;
    }

    private boolean isParamSuperSet(MethodDeclaration sup, MethodDeclaration sub) {
        var supParams = getParamTypes(sup);
        var subParams = getParamTypes(sub);
        if (supParams.size() == subParams.size())
            return false;

        return Sequences.containsNonContinuous(getParamTypes(sup), getParamTypes(sub));
    }

    private List<Type> getParamTypes(MethodDeclaration sup) {
        return sup.getParameters().stream().map(p -> p.getType()).collect(Collectors.toList());
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
        var typesWithName = typeList.stream()
                .filter(nt -> nt.getFullyQualifiedName().equals(typeToFind.getFullyQualifiedName()))
                .collect(Collectors.toList());
        if (typesWithName.size() > 0) {
            return typesWithName.get(0);
        }
        return null;
    }
}
