package li.winston.cateserver.data.out.work;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by winston on 22/01/2016.
 */
public class DeadlineTest {

    public static final Deadline EARLIER_SUBMITTED = new Deadline(
            null,
            1,
            null,
            null,
            null,
            null,
            LocalDateTime.of(2016, 05, 01, 00, 00),
            true,
            null
    );
    public static final Deadline LATER_NOT_SUBMITTED = new Deadline(
            null,
            2,
            null,
            null,
            null,
            null,
            LocalDateTime.of(2016, 05, 03, 00, 00),
            false,
            null
    );
    public static final Deadline LATER_SUBMITTED = new Deadline(
            null,
            2,
            null,
            null,
            null,
            null,
            LocalDateTime.of(2016, 05, 03, 00, 00),
            true,
            null
    );

    @Test
    public void testSubmittedIsLater() {
        List<Deadline> deadlines = Arrays.asList(
                EARLIER_SUBMITTED,
                LATER_NOT_SUBMITTED
        );
        deadlines.sort(null);
        assertEquals(Arrays.asList(LATER_NOT_SUBMITTED, EARLIER_SUBMITTED), deadlines);
    }

    @Test
    public void testOrdering() {
        List<Deadline> deadlines = Arrays.asList(
                LATER_SUBMITTED,
                EARLIER_SUBMITTED
        );
        deadlines.sort(null);
        assertEquals(Arrays.asList(EARLIER_SUBMITTED, LATER_SUBMITTED), deadlines);
    }

}