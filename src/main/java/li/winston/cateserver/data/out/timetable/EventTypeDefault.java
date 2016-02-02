package li.winston.cateserver.data.out.timetable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by winston on 20/01/2016.
 */
public enum EventTypeDefault implements EventType {

    LECTURE,
    TUTORIAL,
    LAB,
    EXAM,
    TEST;

    private static
    Map<String, EventType> EVENT_MAP = new HashMap<String, EventType>() {
        {
            put("LEC", LECTURE);
            put("TUT", TUTORIAL);
            put("Laboratory Session", LAB);
            put("Examination", EXAM);
            put("Test", TEST);
        }
    };

    public static EventType fromString(String name) {
        EventType type = EVENT_MAP.get(name);
        if (type != null) {
            return type;
        }
        try {
            type = EventTypeDefault.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return type;
    }

}
