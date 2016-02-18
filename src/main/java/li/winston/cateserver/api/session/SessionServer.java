package li.winston.cateserver.api.session;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.listener.ExceptionListener;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import li.winston.cateserver.data.Auth;
import li.winston.cateserver.data.out.timetable.Timetable;
import li.winston.cateserver.data.out.user.UserInfo;
import li.winston.cateserver.data.out.work.WorkInfo;
import li.winston.cateserver.scraper.CateScraper;
import li.winston.cateserver.scraper.TimetableScraper;
import li.winston.cateserver.transport.file.TimetableFileTransport;
import li.winston.cateserver.util.GsonInstance;
import li.winston.cateserver.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by winston on 22/01/2016.
 */
public class SessionServer {

    private final int port;
    private final SessionManager sessionManager;
    private final String p12;
    private final String authMappingFile;
    private SocketIOServer server;

    public SessionServer(int port,
                         String motdFile,
                         String p12File,
                         String timetableDir,
                         String authMappingFile) {
        this.port = port;
        this.p12 = p12File;
        this.authMappingFile = authMappingFile;
        sessionManager = new SessionManager(
                new CateScraper(),
                new TimetableScraper(new TimetableFileTransport(timetableDir)),
                motdFile
        );
    }

    static class NonShitJsonSupport extends JacksonJsonSupport {
        @Override
        public void writeValue(ByteBufOutputStream out, Object value) throws IOException {
            GsonInstance.gson.toJson(value, new Appendable() {
                @Override
                public Appendable append(CharSequence csq) throws IOException {
                    out.write(csq.toString().getBytes(StandardCharsets.UTF_8));
                    return this;
                }

                @Override
                public Appendable append(CharSequence csq, int start, int end) throws IOException {
                    out.write(csq.subSequence(start, end).toString().getBytes(StandardCharsets.UTF_8));
                    return this;
                }

                @Override
                public Appendable append(char c) throws IOException {
                    out.writeByte(c);
                    return this;
                }
            });
        }

    }

    public void start() {
        Configuration config = new Configuration();
        config.getSocketConfig().setReuseAddress(true);
        config.setPort(port);
        config.setJsonSupport(new NonShitJsonSupport());
        if (p12 != null) {
            try {
                FileInputStream in = new FileInputStream(p12);
                config.setKeyStore(in);
                config.setKeyStoreFormat("PKCS12");
                config.setKeyStorePassword("");
            } catch (FileNotFoundException e) {
                Log.error("Couldn't load .p12 file", e);
                throw new RuntimeException(e);
            }
        }
        AuthMapper authMapper;
        if (authMappingFile != null) {
            try (FileReader json = new FileReader(authMappingFile)) {
                authMapper = new AuthMapperImpl(
                        GsonInstance.gson.fromJson(
                                json,
                                new TypeToken<List<AuthMapping>>() {
                                }.getType()
                        )
                );
            } catch (IOException e) {
                Log.error("Couldn't load auth mapping file", e);
                throw new RuntimeException(e);
            }
        } else {
            authMapper = new AuthMapperEmpty();
        }
        config.setExceptionListener(new ExceptionListener() {
            @Override
            public void onEventException(Exception e, List<Object> args, SocketIOClient client) {
                Log.warn(
                        "onEventException ("
                                + client.getRemoteAddress() + "): "
                                + args,
                        e
                );
            }

            @Override
            public void onDisconnectException(Exception e, SocketIOClient client) {
                Log.warn(
                        "onDisconnectException ("
                                + client.getRemoteAddress() + ")",
                        e
                );
            }

            @Override
            public void onConnectException(Exception e, SocketIOClient client) {
                Log.warn(
                        "onDisconnectException ("
                                + client.getRemoteAddress() + ")",
                        e
                );
            }

            @Override
            public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
                Log.warn(
                        "exceptionCaught ("
                                + ctx.channel().remoteAddress() + ")",
                        e
                );
                return true;
            }
        });
        server = new SocketIOServer(config);
        SocketIONamespace v0 = server.addNamespace("/app/cate/api/v0");
        v0.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                Session session = sessionManager.startSession(client);
                session.addSessionEventListener(new SessionEventListener() {

                    @Override
                    public void authSuccess(UserInfo userInfo) {
                        client.sendEvent("authSuccess", userInfo);
                    }

                    @Override
                    public void authLoginFailed() {
                        client.sendEvent("authLoginFailed");
                    }

                    @Override
                    public void authKick() {
                        client.sendEvent("authKick");
                    }

                    @Override
                    public void updateWork(WorkInfo work) {
                        client.sendEvent("updateWork", work);
                    }

                    @Override
                    public void updateTimetable(Timetable timetable) {
                        client.sendEvent("updateTimetable", timetable);
                    }

                    @Override
                    public void motd(String motd) {
                        client.sendEvent("motd", motd);
                    }

                });
                Log.info("({}) Connected", client.getRemoteAddress());
            }
        });
        v0.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                sessionManager.endSession(client);
                Log.info("({}) Disconnected", client.getRemoteAddress());
            }
        });
        v0.addEventListener("auth", Auth.class, new DataListener<Auth>() {
            @Override
            public void onData(SocketIOClient client, Auth data, AckRequest ackSender) throws Exception {
                Auth mapped = authMapper.map(data);
                sessionManager.getSession(client).ifPresent(session ->
                        session.authenticate(
                                mapped.getUsername(),
                                mapped.getPassword()
                        )
                );
            }
        });
        v0.addEventListener("deauth", null, new DataListener<Object>() {
            @Override
            public void onData(SocketIOClient client, Object data, AckRequest ackSender) throws Exception {
                sessionManager.getSession(client).ifPresent(Session::deauth);
            }
        });
        server.start();
    }

    public void stop() {
        server.stop();
    }

}
