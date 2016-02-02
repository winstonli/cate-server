package li.winston.cateserver.data.out.timetable;

/**
 * Created by winston on 20/01/2016.
 */
public class EventTypeCustom implements EventType {

    private final String name;

    public EventTypeCustom(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EventTypeCustom)) {
            return false;
        }
        EventTypeCustom that = (EventTypeCustom) obj;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

}
