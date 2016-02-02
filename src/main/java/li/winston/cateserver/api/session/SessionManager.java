package li.winston.cateserver.api.session;

import com.corundumstudio.socketio.SocketIOClient;
import com.google.common.base.Preconditions;
import li.winston.cateserver.data.out.timetable.Timetable;
import li.winston.cateserver.data.out.user.UserInfo;
import li.winston.cateserver.data.out.work.WorkInfo;
import li.winston.cateserver.scraper.CateScraper;
import li.winston.cateserver.scraper.TimetableScraper;
import li.winston.cateserver.util.GsonInstance;
import li.winston.cateserver.util.Log;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by winston on 21/01/2016.
 */
public class SessionManager {

    private final Map<UUID, Session> sessions;

    private final CateScraper cateScraper;
    private final TimetableScraper timetableScraper;

    private Motd motd = Motd.DEFAULT;

    public SessionManager(CateScraper cateScraper, TimetableScraper timetableScraper, String motdFile) {
        assert cateScraper != null && timetableScraper != null;
        this.cateScraper = cateScraper;
        this.timetableScraper = timetableScraper;
        sessions = new ConcurrentHashMap<UUID, Session>();
        if (motdFile != null) {
            Log.info("Found motd file at: {}", motdFile);
            new Timer().scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    Motd motd;
                    try {
                        motd = GsonInstance.gson.fromJson(new FileReader(motdFile), Motd.class);
                    } catch (IOException e) {
                        Log.warn("Failed to open MOTD file", e);
                        return;
                    }
                    if (!motd.equals(SessionManager.this.motd)) {
                        Log.info("Updated motd: {}", motd);
                        SessionManager.this.motd = motd;
                        sessions.values().forEach(session -> session.motd(motd));
                    }
                }

            }, 10000, 10000);
        }
    }

    public Session startSession(SocketIOClient client) {
        Preconditions.checkNotNull(client);
        Session session = new Session(client.getRemoteAddress(), cateScraper, timetableScraper);
        sessions.put(client.getSessionId(), session);
        session.addSessionEventListener(new SessionEventListener() {
            @Override
            public void authSuccess(UserInfo userInfo) {
                if (motd != null) {
                    session.motd(motd);
                }
            }

            @Override
            public void authLoginFailed() {

            }

            @Override
            public void authKick() {

            }

            @Override
            public void updateWork(WorkInfo work) {

            }

            @Override
            public void updateTimetable(Timetable timetable) {

            }

            @Override
            public void motd(String motd) {

            }
        });
        return session;
    }

    public void endSession(SocketIOClient client) {
        assert client != null;
        Session session = sessions.remove(client.getSessionId());
        Preconditions.checkNotNull(session);
        session.end();
    }

    /* Can still arrive after a given session has ended. */
    public Optional<Session> getSession(SocketIOClient client) {
        assert client != null;
        return Optional.ofNullable(sessions.get(client.getSessionId()));
    }

}
