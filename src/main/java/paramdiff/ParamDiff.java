package paramdiff;

public class ParamDiff {
    final String className;
    final String methodName;
    final String oldParams;
    final String newParams;

    public ParamDiff(String className, String methodName, String oldParams, String newParams) {
        this.className = className;
        this.methodName = methodName;
        this.oldParams = oldParams;
        this.newParams = newParams;
    }
}
