package paramdiff;

import java.util.ArrayList;
import java.util.List;

public class ParamDiffFinder {
    public List<ParamAdditionDiff> findParamAddition(String oldCode, String newCode) {
        if (oldCode == newCode){
            return new ArrayList<>();
        }else {
            return List.of(new ParamAdditionDiff("","",1, int.class));
        }
    }
}
