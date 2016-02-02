package li.winston.cateserver.scraper;

import li.winston.cateserver.data.out.timetable.Timetable;
import li.winston.cateserver.transport.TimetableTransport;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by winston on 21/01/2016.
 */
public class TimetableScraper {

    private static final
    List<String> courses = Arrays.asList(
            "c1",
            "c2",
            "c3",
            "c4",
            "i2",
            "i3",
            "i4",
            "j1",
            "j2",
            "j3",
            "j4",
            "h5",
            "r5",
            "a5",
            "v5",
            "s5"
    );

    private final
    Map<Pair<String, Integer>, Timetable> timetables;

    private Timetable load(String course, int period) {
        PageScraper scraper = new PageScraper();
        Timetable timetable = new Timetable();
        for (Integer i : Arrays.asList(1, 2, 11)) {
            try {
                timetable = timetable.combine(scraper.parseTimetable(transport.timetable(course, period, i)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return timetable;
    }

    private final TimetableTransport transport;

    public TimetableScraper(TimetableTransport transport) {
        this.transport = transport;
        timetables = new HashMap<Pair<String, Integer>, Timetable>() {
            {
                PageScraper scraper = new PageScraper();
                for (String course : courses) {
                    Timetable timetable = new Timetable();
                    for (Integer i : Arrays.asList(1, 2, 11)) {
                        try {
                            timetable = timetable.combine(scraper.parseTimetable(transport.timetable(course, 3, i)));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    put(Pair.of(course, 3), timetable);
                }
            }
        };
    }

    public Timetable scrapeTimetable(String course, int period) {
        Timetable timetable = load(course, period);
        if (timetable == null) {
            throw new IllegalArgumentException();
        }
        return timetable;
    }

}
