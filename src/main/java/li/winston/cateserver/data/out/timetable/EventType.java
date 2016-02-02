package li.winston.cateserver.data.out.timetable;

import java.util.Optional;

/**
 * Created by winston on 20/01/2016.
 */
public interface EventType {

    static EventType parse(String name) {
        return Optional.ofNullable(EventTypeDefault.fromString(name))
                       .orElse(new EventTypeCustom(name));
    }

}
