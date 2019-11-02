package paramdiff;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
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

        var oldCandidateMethods = new HashSet<>(getCallables(oldType));
        var newCandidateMethods = new HashSet<>(getCallables(newType));

        removeUnchangedMethods(oldType, newType, oldCandidateMethods, newCandidateMethods);

        var diffs = new ArrayList<ParamAdditionDiff>();
        for (var oldMethod : oldCandidateMethods) {
            findParamAddedMethods(newCandidateMethods, oldMethod)
                    .forEach(m -> diffs.add(new ParamAdditionDiff(oldMethod, m)));
        }

        return diffs;
    }

    private Stream<CallableDeclaration<?>> findParamAddedMethods(HashSet<CallableDeclaration<?>> newCandidateMethods,
                                                            CallableDeclaration<?> oldMethod) {
        var overloads = newCandidateMethods.stream()
                .filter(m -> m.getNameAsString().equals(oldMethod.getNameAsString()));
        var overloadsWithMoreParams =
                overloads.filter(m -> m.getParameters().size() > oldMethod.getParameters().size());
        return overloadsWithMoreParams
                .filter(m -> isParamSuperSet(m, oldMethod));
    }

    private void removeUnchangedMethods(TypeDeclaration<?> oldType, TypeDeclaration newType,
                                        HashSet<CallableDeclaration<?>> oldCandidates, HashSet newCandidates) {
        for (var oldMethod : getCallables(oldType)) {
            var newMethod = findMatchingMethod(newType, oldMethod);
            if (newMethod != null) {
                newCandidates.remove(newMethod);
                oldCandidates.remove(oldMethod);
            }
        }
    }

    private boolean isParamSuperSet(CallableDeclaration<?> sup, CallableDeclaration<?> sub) {
        return Sequences.containsNonContinuous(getParamTypes(sup), getParamTypes(sub));
    }

    private List<Type> getParamTypes(CallableDeclaration<?> method) {
        return method.getParameters().stream().map(p -> p.getType()).collect(Collectors.toList());
    }

    private CallableDeclaration<?> findMatchingMethod(TypeDeclaration<?> type, CallableDeclaration<?> method) {
        var matchingMethodList = type.getCallablesWithSignature(method.getSignature());

        if (matchingMethodList.size() > 0) {
            return matchingMethodList.get(0);
        }
        return null;
    }

    private TypeDeclaration<?> findMatchingClass(NodeList<TypeDeclaration<?>> typeList, TypeDeclaration typeToFind) {
        var typesWithName = typeList.stream()
                .filter(nt -> nt.getFullyQualifiedName().equals(typeToFind.getFullyQualifiedName()))
                .collect(Collectors.toList());
        if (typesWithName.size() > 0) {
            return typesWithName.get(0);
        }
        return null;
    }

    private List<CallableDeclaration<?>> getCallables(TypeDeclaration<?> type){
        var all = new ArrayList<CallableDeclaration<?>>();
        all.addAll(type.getConstructors());
        all.addAll(type.getMethods());

        return all;
    }
}
