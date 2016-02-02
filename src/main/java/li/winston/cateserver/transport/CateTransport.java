package li.winston.cateserver.transport;

import li.winston.cateserver.data.Auth;
import li.winston.cateserver.transport.net.UnauthorizedException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by winston on 17/01/2016.
 */
public interface CateTransport {

    int home(Auth auth) throws IOException, UnauthorizedException;
    InputStream personal(Auth auth, int year) throws IOException, UnauthorizedException;
    InputStream timetable(Auth auth, int period, String course, int year) throws IOException, UnauthorizedException;
    InputStream url(Auth auth, String url) throws IOException, UnauthorizedException;

}
