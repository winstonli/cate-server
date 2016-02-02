package li.winston.cateserver.api.session;

import li.winston.cateserver.data.Auth;

/**
 * Created by winston on 02/02/2016.
 */
public class AuthMapperEmpty implements AuthMapper {

    @Override
    public Auth map(Auth auth) {
        return auth;
    }

}
