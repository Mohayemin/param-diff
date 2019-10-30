package util;


public class Sequences {
    public static <E> boolean containsNonContinuous(Iterable<E> sup, Iterable<E> sub) {
        var supIt = sup.iterator();
        var subIt = sub.iterator();
        while (subIt.hasNext()) {
            var item = subIt.next();
            var found = false;
            while (supIt.hasNext()) {
                var supItem = supIt.next();
                if ((item == null && supItem == null) || item.equals(supItem)) {
                    found = true;
                    break;
                }
            }

            if (!found)
                return false;
        }

        return true;
    }
}
