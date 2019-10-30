package paramdiff;

import com.github.javaparser.ast.body.MethodDeclaration;

public class ParamAdditionDiff {
    final MethodDeclaration oldMethod;
    final MethodDeclaration newMethod;


    public ParamAdditionDiff(MethodDeclaration oldMethod, MethodDeclaration newMethod) {
        this.oldMethod = oldMethod;
        this.newMethod = newMethod;
    }

    @Override
    public String toString() {
        return String.format("%s->%s",oldMethod.getSignature().asString(),newMethod.getSignature().asString());
    }
}
