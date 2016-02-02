package li.winston.cateserver.api.session;

import li.winston.cateserver.data.Auth;

/**
 * Created by winston on 02/02/2016.
 */
public class AuthMapping {

    private final Auth from;
    private final Auth to;

    private AuthMapping(Auth from, Auth to) {
        this.from = from;
        this.to = to;
    }

    public Auth getFrom() {
        return from;
    }

    public Auth getTo() {
        return to;
    }

}
