package li.winston.cateserver.data.out.work;

import li.winston.cateserver.data.parsed.work.ExerciseType;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

/**
 * Created by winston on 19/01/2016.
 */
public class Upcoming implements Comparable<Upcoming> {

    private static final Comparator<Upcoming> comparator = new Comparator<Upcoming>() {

        @Override
        public int compare(Upcoming o1, Upcoming o2) {
            return o1.startTime.compareTo(o2.startTime);
        }

    }.thenComparing(Comparator.comparingInt(upcoming -> upcoming.sequence));

    private final String subjectId;

    private final int sequence;
    private final String name;
    private final String category;
    private final ExerciseType type;
    private final LocalDateTime startTime;

    public Upcoming(String subjectId,
                    int sequence,
                    String name,
                    String category,
                    ExerciseType type,
                    LocalDateTime startTime) {
        this.subjectId = subjectId;
        this.sequence = sequence;
        this.name = name;
        this.category = category;
        this.type = type;
        this.startTime = startTime;
    }

    @Override
    public int compareTo(Upcoming o) {
        return comparator.compare(this, o);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Upcoming)) {
            return false;
        }
        Upcoming that = (Upcoming) obj;
        return subjectId.equals(that.subjectId) &&
               sequence == that.sequence &&
               name.equals(that.name) &&
               category.equals(that.category) &&
               type.equals(that.type) &&
               startTime.equals(that.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                subjectId,
                sequence,
                name,
                category,
                type,
                startTime
        );
    }

}
