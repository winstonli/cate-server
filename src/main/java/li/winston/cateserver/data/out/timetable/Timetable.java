package li.winston.cateserver.data.out.timetable;

import li.winston.cateserver.util.GsonInstance;
import org.apache.commons.lang3.tuple.Pair;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by winston on 20/01/2016.
 */
public class Timetable {

    private static List<DayOfWeek> weekdays() {
        return Arrays.stream(DayOfWeek.values()).filter(day ->
            day.compareTo(DayOfWeek.SATURDAY) < 0
        ).collect(Collectors.toList());
    }

    private static SortedMap<DayOfWeek, SortedSet<Event>> createEmptyDays() {
        SortedMap<DayOfWeek, SortedSet<Event>> days = new TreeMap<>();
        for (DayOfWeek weekday : weekdays()) {
            days.put(weekday, new TreeSet<>());
        }
        return days;
    }

    private final SortedMap<DayOfWeek, SortedSet<Event>> days;

    public Timetable(SortedMap<DayOfWeek, SortedSet<Event>> days) {
        this.days = days;
    }

    public Timetable() {
        this(createEmptyDays());
    }

    public void add(Event e) {
        days.get(e.getDay()).add(e);
    }

    public Timetable combine(Timetable timetable) {
        SortedMap<DayOfWeek, SortedSet<Event>> combined = createEmptyDays();
        days.entrySet().forEach(des -> combined.get(des.getKey()).addAll(des.getValue()));
        timetable.days.entrySet().forEach(des -> combined.get(des.getKey()).addAll(des.getValue()));
        return new Timetable(combined);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Timetable)) {
            return false;
        }
        Timetable that = (Timetable) obj;
        return getHashEntrySet(days).equals(getHashEntrySet(that.days));
    }

    private List<Pair<DayOfWeek, ArrayList<Event>>> getHashEntrySet(SortedMap<DayOfWeek, SortedSet<Event>> d) {
        return d.entrySet().stream().map(de -> Pair.of(de.getKey(), new ArrayList<>(de.getValue()))).collect(Collectors.toList());
    }

    @Override
    public int hashCode() {
        return days.hashCode();
    }

    @Override
    public String toString() {
        return GsonInstance.gson.toJson(this);
    }

}
