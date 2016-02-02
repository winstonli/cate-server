package li.winston.cateserver.data.out.work;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Created by winston on 19/01/2016.
 */
public class WorkInfo {

    private final List<SubjectInfo> subjects;
    private final List<Deadline> deadlines;
    private final List<Upcoming> upcoming;
    private final LocalDateTime firstDayOfTerm;

    public WorkInfo(List<SubjectInfo> subjects,
                    List<Deadline> deadlines,
                    List<Upcoming> upcoming,
                    LocalDateTime firstDayOfTerm) {
        subjects.sort(null);
        deadlines.sort(null);
        upcoming.sort(null);
        this.subjects = subjects;
        this.deadlines = deadlines;
        this.upcoming = upcoming;
        this.firstDayOfTerm = firstDayOfTerm;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof WorkInfo)) {
            return false;
        }
        WorkInfo that = (WorkInfo) obj;
        return subjects.equals(that.subjects) &&
               deadlines.equals(that.deadlines) &&
               upcoming.equals(that.upcoming) &&
               firstDayOfTerm.equals(that.firstDayOfTerm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                subjects,
                deadlines,
                upcoming,
                firstDayOfTerm
        );
    }

}
