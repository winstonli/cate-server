package li.winston.cateserver.transport.net;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import li.winston.cateserver.data.Auth;
import li.winston.cateserver.transport.CateTransport;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by winston on 16/01/2016.
 */
public class CateNetTransport implements CateTransport {

    static class IntThrowableData extends RuntimeException {

        public int get() {
            return i;
        }

        private final int i;

        public IntThrowableData(int i) {
            this.i = i;
        }

    }

    static final String CATE_URL = "https://cate.doc.ic.ac.uk";

    public static final String resolveUrl(String url) {
        if (url.startsWith("/")) {
            return CATE_URL + url;
        }
        if (!url.startsWith(CATE_URL)) {
            return CATE_URL + '/' + url;
        }
        return url;
    }

    private final HttpRequestFactory httpRequestFactory;

    public CateNetTransport() {
        httpRequestFactory = new NetHttpTransport().createRequestFactory();
    }

    @Override
    public int home(Auth auth) throws IOException, UnauthorizedException {
        HttpRequest req = httpRequestFactory.buildGetRequest(new GenericUrl(resolveUrl("/")))
            .setInterceptor(httpRequest ->
                 new BasicAuthentication(auth.getUsername(), auth.getPassword()).intercept(httpRequest)
            )
            .setFollowRedirects(false)
            .setUnsuccessfulResponseHandler((request, response, supportsRetry) -> {
                if (response.getStatusCode() == HttpStatusCodes.STATUS_CODE_FOUND) {
                    int year = Integer.parseInt(StringUtils.substringBefore(StringUtils.substringAfter(response.getHeaders().getLocation(), "keyp="), ":"));
                    throw new IntThrowableData(year);
                }
                return false;
            });
        try {
            req.execute();
        } catch (IntThrowableData i) {
            return i.get();
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatusCodes.STATUS_CODE_UNAUTHORIZED) {
                throw new UnauthorizedException();
            }
            throw e;
        }
        throw new IllegalStateException("request finished without a redirect or exception");
    }

    @Override
    public InputStream personal(Auth auth, int year) throws IOException, UnauthorizedException {
        return getCateStream(
            auth,
            personalUrl(auth, year)
        );
    }

    static String personalUrl(Auth auth, int year) {
        return String.format(
            "/personal.cgi?keyp=%d:%s",
            year,
            auth.getUsername()
        );
    }

    @Override
    public InputStream timetable(Auth auth, int period, String course, int year) throws IOException, UnauthorizedException {
        return getCateStream(
            auth,
            timetableUrl(auth, period, course, year)
        );
    }

    @Override
    public InputStream url(Auth auth, String url) throws IOException, UnauthorizedException {
        return getCateStream(auth, url);
    }

    static String timetableUrl(Auth auth, int period, String course, int year) {
        return String.format(
            "/timetable.cgi?period=%d&class=%s&keyt=%d%%3Anone%%3Anone%%3A%s",
            period,
            course,
            year,
            auth.getUsername()
        );
    }

    private InputStream getCateStream(Auth auth, String target) throws IOException, UnauthorizedException {
        try {
            return httpRequestFactory
                  .buildGetRequest(new GenericUrl(resolveUrl(target)))
                  .setInterceptor(httpRequest ->
                      new BasicAuthentication(auth.getUsername(), auth.getPassword()).intercept(httpRequest)
                  )
                  .execute()
                  .getContent();
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatusCodes.STATUS_CODE_UNAUTHORIZED) {
                throw new UnauthorizedException();
            }
            throw e;
        }
    }

}
