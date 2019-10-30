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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private List<ParamAdditionDiff> findParamAdditions(TypeDeclaration<?> oldType, TypeDeclaration<?> newType) {
        if (newType == null)
            return List.of();

        var oldCandidateMethods = new HashSet<>(oldType.getMethods());
        var newCandidateMethods = new HashSet<>(newType.getMethods());
        removeUnchangedMethods(oldType, newType, oldCandidateMethods, newCandidateMethods);

        var diffs = new ArrayList<ParamAdditionDiff>();
        for (var oldMethod : oldCandidateMethods) {
            findParamAddedMethods(newCandidateMethods, oldMethod)
                    .forEach(m -> diffs.add(new ParamAdditionDiff(oldMethod, m)));
        }

        return diffs;
    }

    private Stream<MethodDeclaration> findParamAddedMethods(HashSet<MethodDeclaration> newCandidateMethods,
                                                            MethodDeclaration oldMethod) {
        var overloads = newCandidateMethods.stream()
                .filter(m -> m.getNameAsString().equals(oldMethod.getNameAsString()));
        var overloadsWithMoreParams =
                overloads.filter(m -> m.getParameters().size() > oldMethod.getParameters().size());
        return overloadsWithMoreParams
                .filter(m -> isParamSuperSet(m, oldMethod));
    }

    private void removeUnchangedMethods(TypeDeclaration<?> oldType, TypeDeclaration newType,
                                        HashSet<MethodDeclaration> oldCandidates, HashSet newCandidates) {
        for (var oldMethod : oldType.getMethods()) {
            var newMethod = findMatchingMethod(newType, oldMethod);
            if (newMethod != null) {
                newCandidates.remove(newMethod);
                oldCandidates.remove(oldMethod);
            }
        }
    }

    private boolean isParamSuperSet(MethodDeclaration sup, MethodDeclaration sub) {
        return Sequences.containsNonContinuous(getParamTypes(sup), getParamTypes(sub));
    }

    private List<Type> getParamTypes(MethodDeclaration sup) {
        return sup.getParameters().stream().map(p -> p.getType()).collect(Collectors.toList());
    }

    private MethodDeclaration findMatchingMethod(TypeDeclaration type, MethodDeclaration method) {
        var params = method.getParameters().stream()
                .map(parameter -> parameter.getType().asString())
                .collect(Collectors.toList()).toArray(new String[0]);
        List<MethodDeclaration> matchingMethodList = type.getMethodsBySignature(method.getNameAsString(), params);

        if (matchingMethodList.size() > 0) {
            return matchingMethodList.get(0);
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
