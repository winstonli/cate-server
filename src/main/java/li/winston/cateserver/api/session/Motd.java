package li.winston.cateserver.api.session;

import li.winston.cateserver.util.GsonInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by winston on 31/01/2016.
 */
public class Motd {

    public static final Motd DEFAULT = new Motd(new HashMap<String, String>(), "");

    private final Map<String, String> messages;
    private final String def;

    public Motd(Map<String, String> messages, String def) {
        this.messages = messages;
        this.def = def;
    }

    public String get(String login) {
        return Optional.ofNullable(messages.get(login)).orElse(def);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Motd)) {
            return false;
        }
        Motd that = (Motd) obj;
        return messages.equals(that.messages) && def.equals(that.def);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messages, def);
    }

    @Override
    public String toString() {
        return GsonInstance.gson.toJson(this);
    }

}
