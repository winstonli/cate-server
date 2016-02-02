package li.winston.cateserver.data;

import java.util.Objects;

/**
 * Created by winston on 16/01/2016.
 */
public class Auth {

    private final String username;
    private final String password;

    public Auth() {
        username = null;
        password = null;
    }

    public Auth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Auth)) {
            return false;
        }
        Auth that = (Auth) obj;
        return username.equals(that.username) && password.equals(that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

}
