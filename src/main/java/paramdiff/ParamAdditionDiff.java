package paramdiff;

import com.github.javaparser.ast.body.CallableDeclaration;

public class ParamAdditionDiff {
    final CallableDeclaration<?> oldMethod;
    final CallableDeclaration<?> newMethod;


    public ParamAdditionDiff(CallableDeclaration<?> oldMethod, CallableDeclaration<?> newMethod) {
        this.oldMethod = oldMethod;
        this.newMethod = newMethod;
    }

    @Override
    public String toString() {
        return String.format("%s->%s",oldMethod.getSignature().asString(),newMethod.getSignature().asString());
    }
}
