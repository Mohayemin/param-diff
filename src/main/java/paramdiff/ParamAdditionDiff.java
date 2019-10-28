package paramdiff;

public class ParamAdditionDiff {
    final String className;
    final String methodName;
    final int indexOfAddedParam;
    final String typeOfAddedParam;

    public ParamAdditionDiff(String className, String methodName, int indexOfAddedParam, String typeOfAddedParam) {
        this.className = className;
        this.methodName = methodName;
        this.indexOfAddedParam = indexOfAddedParam;
        this.typeOfAddedParam = typeOfAddedParam;
    }
}
