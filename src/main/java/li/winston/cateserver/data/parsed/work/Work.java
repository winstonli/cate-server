package li.winston.cateserver.data.parsed.work;

import li.winston.cateserver.util.GsonInstance;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Created by winston on 17/01/2016.
 */
public class Work {

    @Nullable
    private final LocalDateTime today;

    private final LocalDateTime firstDayOfTerm;

    private final List<Subject> subjects;

    public Work(LocalDateTime today, LocalDateTime firstDayOfTerm, List<Subject> subjects) {
        this.today = today;
        this.firstDayOfTerm = firstDayOfTerm;
        this.subjects = subjects;
    }

    public LocalDateTime getToday() {
        return today;
    }

    public LocalDateTime getFirstDayOfTerm() {
        return firstDayOfTerm;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Work)) {
            return false;
        }
        Work that = (Work) obj;
        return Objects.equals(today, that.today) &&
               subjects.equals(that.subjects);
    }

    @Override
    public int hashCode() {
        return subjects.hashCode();
    }

    @Override
    public String toString() {
        return GsonInstance.gson.toJson(this);
    }

}
