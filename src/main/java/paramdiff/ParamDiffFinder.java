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

        var oldCandidateCallables = new HashSet<>(getCallables(oldType));
        var newCandidateCallables = new HashSet<>(getCallables(newType));

        removeUnchangedCallables(oldType, newType, oldCandidateCallables, newCandidateCallables);

        var diffs = new ArrayList<ParamAdditionDiff>();
        for (var oldCallable : oldCandidateCallables) {
            findParamAddedCallables(newCandidateCallables, oldCallable)
                    .forEach(m -> diffs.add(new ParamAdditionDiff(oldCallable, m)));
        }

        return diffs;
    }

    private Stream<CallableDeclaration<?>> findParamAddedCallables(HashSet<CallableDeclaration<?>> newCandidateCallables,
                                                                   CallableDeclaration<?> oldCallable) {
        var overloads = newCandidateCallables.stream()
                .filter(m -> m.getNameAsString().equals(oldCallable.getNameAsString()));
        var overloadsWithMoreParams =
                overloads.filter(m -> m.getParameters().size() > oldCallable.getParameters().size());
        return overloadsWithMoreParams
                .filter(m -> isParamSuperSet(m, oldCallable));
    }

    private void removeUnchangedCallables(TypeDeclaration<?> oldType, TypeDeclaration newType,
                                          HashSet<CallableDeclaration<?>> oldCandidates, HashSet newCandidates) {
        for (var oldCallable : getCallables(oldType)) {
            var newCallable = findMatchingCallable(newType, oldCallable);
            if (newCallable != null) {
                newCandidates.remove(newCallable);
                oldCandidates.remove(oldCallable);
            }
        }
    }

    private boolean isParamSuperSet(CallableDeclaration<?> sup, CallableDeclaration<?> sub) {
        return Sequences.containsNonContinuous(getParamTypes(sup), getParamTypes(sub));
    }

    private List<Type> getParamTypes(CallableDeclaration<?> callable) {
        return callable.getParameters().stream().map(p -> p.getType()).collect(Collectors.toList());
    }

    private CallableDeclaration<?> findMatchingCallable(TypeDeclaration<?> type, CallableDeclaration<?> callable) {
        var matchingCallableList = type.getCallablesWithSignature(callable.getSignature());

        if (matchingCallableList.size() > 0) {
            return matchingCallableList.get(0);
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
