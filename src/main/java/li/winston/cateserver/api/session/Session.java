package li.winston.cateserver.api.session;

import li.winston.cateserver.data.Auth;
import li.winston.cateserver.data.out.timetable.Timetable;
import li.winston.cateserver.data.out.user.UserInfo;
import li.winston.cateserver.data.out.work.WorkInfo;
import li.winston.cateserver.scraper.CateScraper;
import li.winston.cateserver.scraper.TimetableScraper;
import li.winston.cateserver.transport.net.UnauthorizedException;
import li.winston.cateserver.util.Log;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by winston on 21/01/2016.
 */
public class Session {

    private final SocketAddress clientAddress;
    private final CateScraper cateScraper;
    private final TimetableScraper timetableScraper;

    private final ScheduledThreadPoolExecutor executor;
    private final Set<SessionEventListener> listeners;

    private SessionState state;

    private UserInfo userInfo;
    private Auth auth;
    private ScheduledFuture<?> workScrapeTask;
    private ScheduledFuture<?> timetableScrapeTask;

    public Session(SocketAddress clientAddress, CateScraper cateScraper, TimetableScraper timetableScraper) {
        this.clientAddress = clientAddress;
        this.cateScraper = cateScraper;
        this.timetableScraper = timetableScraper;
        executor = new ScheduledThreadPoolExecutor(1);
        listeners = new HashSet<>();
        setState(SessionState.NO_AUTH);
    }

    public synchronized void addSessionEventListener(SessionEventListener l) {
        listeners.add(l);
    }

    public synchronized void removeSessionEventListener(SessionEventListener l) {
        listeners.remove(l);
    }

    public synchronized void messageListeners(Consumer<SessionEventListener> msg) {
        listeners.forEach(msg);
    }

    public synchronized void authenticate(String username, String password) {
        if (state == SessionState.NO_AUTH) {
            Log.info("({}, {}) Authenticating", clientAddress, username);
            setState(SessionState.AUTH_PENDING);
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        setAuth(new Auth(username, password));
                        UserInfo userInfo = cateScraper.scrapeUser(auth);
                        setUserInfo(userInfo);
                        setState(SessionState.AUTH);
                        messageListeners(l -> l.authSuccess(userInfo));
                        Log.info("({}, {}) Successfully authenticated user: {} {} ({})", clientAddress, userInfo.getLogin(), userInfo.getFirstName(), userInfo.getLastName(), userInfo.getCourse());
                    } catch (IOException e) {
                        Log.warn(String.format("(%s, %s) ", clientAddress, userInfo.getLogin()) + "Scraping user threw IOException ... retrying in 1000 ms", e);
                        executor.schedule(this, 1, TimeUnit.SECONDS);
                    } catch (UnauthorizedException e) {
                        Log.info("({}, {}) Authentication failed: unsuccessful", clientAddress, username);
                        messageListeners(SessionEventListener::authLoginFailed);
                        setState(SessionState.NO_AUTH);
                    }

                }
            });
        } else {
            Log.warn("({}, {}) authenticate() called when state was {}", clientAddress, userInfo.getLogin(), state.toString());
        }
    }

    public synchronized void deauth() {
        if (state == SessionState.AUTH) {
            if (timetableScrapeTask != null && !timetableScrapeTask.isCancelled()) {
                timetableScrapeTask.cancel(true);
            }
            if (workScrapeTask != null && !workScrapeTask.isCancelled()) {
                workScrapeTask.cancel(true);
            }
            setState(SessionState.NO_AUTH);
            Log.info("({}, {}) deauth", clientAddress, userInfo.getLogin());
        }
    }

    private synchronized void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    private synchronized void setState(SessionState state) {
        this.state = state;
        if (state == SessionState.AUTH) {
            timetableScrapeTask = executor.scheduleAtFixedRate(new Runnable() {

                private Timetable previous = null;

                @Override
                public void run() {
                    UserInfo userInfo = getUserInfo();
                    Timetable timetable = timetableScraper.scrapeTimetable(userInfo.getCourse(), userInfo.getPeriod());
                    if (!timetable.equals(previous)) {
                        messageListeners(l -> l.updateTimetable(timetable));
                        previous = timetable;
                        Log.info("({}, {}) updateTimetable", clientAddress, userInfo.getLogin());
                    }
                }
            }, 0, 30000, TimeUnit.MILLISECONDS);
            workScrapeTask = executor.scheduleAtFixedRate(new Runnable() {

                private WorkInfo previous = null;

                @Override
                public void run() {
                    Auth auth = getAuth();
                    UserInfo userInfo = getUserInfo();
                    try {
                        WorkInfo work = cateScraper.scrapeWork(auth, userInfo.getYear(), userInfo.getPeriod(), userInfo.getCourse());
                        if (!work.equals(previous)) {
                            messageListeners(l -> l.updateWork(work));
                            previous = work;
                            Log.info("({}, {}) updateWork", clientAddress, userInfo.getLogin());
                        }
                    } catch (IOException e) {
                        Log.warn(String.format("(%s, %s) ", clientAddress, userInfo.getLogin()) + "Scraping work threw IOException", e);
                    } catch (UnauthorizedException e) {
                        Log.info("({}, {}) Authenticated user no longer able to auth. Kicking", clientAddress, userInfo.getLogin());
                        timetableScrapeTask.cancel(false);
                        workScrapeTask.cancel(false);
                        setState(SessionState.NO_AUTH);
                        messageListeners(SessionEventListener::authKick);
                    }
                }
            }, 0, 5000, TimeUnit.MILLISECONDS);
        }
    }

    private synchronized void setAuth(Auth auth) {
        this.auth = auth;
    }

    private synchronized Auth getAuth() {
        return auth;
    }

    private synchronized UserInfo getUserInfo() {
        return userInfo;
    }

    public synchronized void end() {
        deauth();
        listeners.clear();
    }

    public synchronized void motd(Motd motd) {
        String login = userInfo.getLogin();
        Log.info("({}, {}) Motd sent", clientAddress, login);
        messageListeners(l -> l.motd(motd.get(login)));
    }

}
