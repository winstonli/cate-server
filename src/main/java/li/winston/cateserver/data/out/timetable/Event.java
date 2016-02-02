package li.winston.cateserver.data.out.timetable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;
import org.apache.commons.lang.ObjectUtils;

import javax.annotation.Nullable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

/**
 * Created by winston on 20/01/2016.
 */
public class Event implements Comparable<Event> {

    private static final Comparator<Event> comparator = Comparator.<Event, DayOfWeek>comparing(e ->
        e.day
    )
    .thenComparing(
        Comparator.<Event, LocalTime>comparing(e ->
            e.startTime
        )
    ).thenComparing((o1, o2) ->
        Integer.compare(o1.weeks.iterator().next(), o2.weeks.iterator().next())
    ).thenComparing((o1, o2) ->
        ObjectUtils.compare(o1.getSubjectId(), o2.getSubjectId())
    ).thenComparing(
        Comparator.<Event, String>comparing(e ->
            e.getSubjectName()
        )
    );

    private final DayOfWeek day;

    @Nullable
    private final String subjectId;
    private final String subjectName;
    private final EventType type;
    private final LinkedHashSet<Integer> weeks;
    private final Set<String> courses;
    private final List<String> rooms;
    private final List<String> lecturers;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public Event(DayOfWeek day,
                 String subjectId,
                 String subjectName,
                 EventType type,
                 LinkedHashSet<Integer> weeks,
                 Set<String> courses,
                 List<String> rooms,
                 List<String> lecturers,
                 LocalTime startTime,
                 LocalTime endTime) {
        Preconditions.checkArgument(Ordering.natural().isOrdered(weeks));
        Preconditions.checkArgument(!weeks.isEmpty());
        this.day = day;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.type = type;
        this.weeks = weeks;
        this.courses = courses;
        this.rooms = rooms;
        this.lecturers = lecturers;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public EventType getType() {
        return type;
    }

    public Set<Integer> getWeeks() {
        return weeks;
    }

    public Set<String> getCourses() {
        return courses;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public List<String> getLecturers() {
        return lecturers;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Event)) {
            return false;
        }
        Event that = (Event) obj;
        return day.equals(that.day) &&
               Objects.equals(subjectId, that.subjectId) &&
               subjectName.equals(that.subjectName) &&
               type.equals(that.type) &&
               weeks.equals(that.weeks) &&
               courses.equals(that.courses) &&
               rooms.equals(that.rooms) &&
               lecturers.equals(that.lecturers) &&
               startTime.equals(that.startTime) &&
               endTime.equals(that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                day,
                subjectId,
                subjectName,
                type,
                weeks,
                courses,
                rooms,
                lecturers,
                startTime,
                endTime
        );
    }

    @Override
    public int compareTo(Event o) {
        return comparator.compare(this, o);
    }

}
