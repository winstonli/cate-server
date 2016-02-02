package li.winston.cateserver.transport.net;

import li.winston.cateserver.data.Auth;
import org.junit.Test;

import static li.winston.cateserver.transport.net.CateNetTransport.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by winston on 17/01/2016.
 */
public class CateNetTransportTest {

    @Test
    public void testResolveUrl() throws Exception {
        String expected = CATE_URL + "/photo/student/pics12/wl3912.jpg";
        assertEquals(expected, resolveUrl("photo/student/pics12/wl3912.jpg"));
        assertEquals(expected, resolveUrl("/photo/student/pics12/wl3912.jpg"));
    }

    @Test
    public void testPersonalUrl() throws Exception {
        assertEquals("/personal.cgi?keyp=2015:username", personalUrl(new Auth("username", null), 2015));
    }

    @Test
    public void testTimetableUrl() throws Exception {
        assertEquals("/timetable.cgi?period=3&class=c4&keyt=2015%3Anone%3Anone%3Ausername", timetableUrl(new Auth("username", null), 3, "c4", 2015));
    }

}
