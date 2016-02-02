package li.winston.cateserver.api.session;

import li.winston.cateserver.data.Auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by winston on 02/02/2016.
 */
public class AuthMapperImpl implements AuthMapper {

    private final Map<Auth, Auth> authMap;

    public AuthMapperImpl(List<AuthMapping> mappings) {
        this.authMap = new HashMap<>();
        mappings.forEach(m -> authMap.put(m.getFrom(), m.getTo()));
    }

    @Override
    public Auth map(Auth auth) {
        return Optional.ofNullable(authMap.get(auth)).orElse(auth);
    }

}
