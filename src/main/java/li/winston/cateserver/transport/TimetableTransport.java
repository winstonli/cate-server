package li.winston.cateserver.transport;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by winston on 21/01/2016.
 */
public interface TimetableTransport {

    InputStream timetable(String course, int period, int week) throws IOException;

}
