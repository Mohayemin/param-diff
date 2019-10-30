package util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SequenceTest {
    @Test
    public void containsNonContinuous_extraInMiddle_true() {
        var sub = List.of("one", "two", "three");
        var sup = List.of("one", "whoisit?", "two", "three");

        Assertions.assertTrue(Sequences.containsNonContinuous(sup, sub));
    }

    @Test
    public void containsNonContinuous_extraEverywhere_true() {
        var sub = List.of("one", "two", "three");
        var sup = List.of("this is first extra", "one", "who is it?", "and it?", "two", "three", "what about this?");

        Assertions.assertTrue(Sequences.containsNonContinuous(sup, sub));
    }

    @Test
    public void containsNonContinuous_noExtra_true() {
        var sub = List.of("one", "two", "three");
        var sup = List.of("one", "two", "three");

        Assertions.assertTrue(Sequences.containsNonContinuous(sup, sub));
    }


    @Test
    public void containsNonContinuous_doesNotContain_false() {
        var sub = List.of("one", "two", "three");
        var sup = List.of("one", "three");

        Assertions.assertFalse(Sequences.containsNonContinuous(sup, sub));
    }
}
