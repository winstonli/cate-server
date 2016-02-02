package li.winston.cateserver;

import li.winston.cateserver.api.session.SessionServer;
import li.winston.cateserver.util.Log;

import java.util.Arrays;

/**
 * Created by winston on 16/01/2016.
 */
public class CateServer {

    public static void main(String[] args) throws ClassNotFoundException {
        if (args.length == 0) {
            System.err.println("usage: CateServer [port] [motd_file] [p12_file] [timetable_dir]");
            System.exit(1);
        }
        Log.info("args: {}", Arrays.toString(args));
        String authMapperFile = args.length < 5 ? null : args[4];
        new SessionServer(
                Integer.parseInt(args[0]),
                args[1],
                args[2],
                args[3],
                authMapperFile
        ).start();
    }

}
