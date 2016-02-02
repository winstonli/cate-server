package li.winston.cateserver.data.out.work;

import li.winston.cateserver.data.parsed.work.ExerciseType;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

/**
 * Created by winston on 19/01/2016.
 */
public class Deadline implements Comparable<Deadline> {

    private static final Comparator<Deadline> comparator =
            Comparator.<Deadline, Boolean>comparing(d -> d.submitted)
            .thenComparing(Comparator.<Deadline, LocalDateTime>comparing(d -> d.dueTime))
            .thenComparing(Comparator.comparingInt(deadline -> deadline.sequence));

    private final String subjectId;

    private final int sequence;
    private final String name;
    private final String category;
    private final ExerciseType type;
    private final LocalDateTime startTime;
    private final LocalDateTime dueTime;
    private final boolean submitted;

    @Nullable
    private final String specUrl;

    public Deadline(String subjectId,
                    int sequence,
                    String name,
                    String category,
                    ExerciseType type,
                    LocalDateTime startTime,
                    LocalDateTime dueTime,
                    boolean submitted,
                    String specUrl) {
        this.subjectId = subjectId;
        this.sequence = sequence;
        this.name = name;
        this.category = category;
        this.type = type;
        this.startTime = startTime;
        this.dueTime = dueTime;
        this.submitted = submitted;
        this.specUrl = specUrl;
    }

    @Override
    public int compareTo(Deadline o) {
        return comparator.compare(this, o);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Deadline)) {
            return false;
        }
        Deadline that = (Deadline) obj;
        return subjectId.equals(that.subjectId) &&
               sequence == that.sequence &&
               name.equals(that.name) &&
               category.equals(that.category) &&
               type.equals(that.type) &&
               startTime.equals(that.startTime) &&
               dueTime.equals(that.dueTime) &&
               submitted == that.submitted &&
               Objects.equals(specUrl, that.specUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                subjectId,
                sequence,
                name,
                category,
                type,
                startTime,
                dueTime,
                submitted,
                specUrl
        );
    }

}
