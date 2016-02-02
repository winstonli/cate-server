package li.winston.cateserver.data.parsed.work;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by winston on 17/01/2016.
 */
public class ExerciseTypeTest {

    @Test
    public void testFromHexColour() throws Exception {
        assertEquals(ExerciseType.WHITE, ExerciseType.fromCateColour("white"));
        assertEquals(ExerciseType.PINK, ExerciseType.fromCateColour("#f0ccF0"));
        assertEquals(ExerciseType.PINK, ExerciseType.fromCateColour("#f0ccf0"));
        assertEquals(ExerciseType.PINK, ExerciseType.fromCateColour("f0cCF0"));
        assertEquals(ExerciseType.PINK, ExerciseType.fromCateColour("f0cCF0"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testBSColour() {
        ExerciseType.fromCateColour("BS");
    }

    @Test (expected = NullPointerException.class)
    public void testWrongColour() {
        ExerciseType.fromCateColour("abcdef");
    }

}
