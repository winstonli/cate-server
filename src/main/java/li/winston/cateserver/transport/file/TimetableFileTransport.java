package li.winston.cateserver.transport.file;

import li.winston.cateserver.transport.TimetableTransport;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by winston on 21/01/2016.
 */
public class TimetableFileTransport implements TimetableTransport {

    private final String timetableDir;

    public TimetableFileTransport(String timetableDir) {
        this.timetableDir = timetableDir;
    }

    enum WeekRange {

        ONE {
            @Override
            public String toString() {
                return "1";
            }
        },
        TWO_TEN {
            @Override
            public String toString() {
                return "2-10";
            }
        },
        ELEVEN {
            @Override
            public String toString() {
                return "11";
            }
        };

        public static WeekRange fromWeek(int week) {
            if (week == 1) {
                return ONE;
            }
            if (week >= 2 && week <= 10) {
                return TWO_TEN;
            }
            if (week == 11) {
                return ELEVEN;
            }
            throw new IllegalArgumentException(String.valueOf(week));
        }

    }

    @Override
    public InputStream timetable(String course, int period, int week) throws IOException {
        return new FileInputStream(String.format("%s/timetable_%s_%d_%s.html", timetableDir, course, period, WeekRange.fromWeek(week)));
    }

    public static void main(String[] args) {
        System.out.println();
    }

}
