package li.winston.cateserver.data.out.work;

import com.google.common.base.Preconditions;
import li.winston.cateserver.data.parsed.notes.Note;

import java.util.List;
import java.util.Objects;

/**
 * Created by winston on 19/01/2016.
 */
public class SubjectInfo implements Comparable<SubjectInfo> {

    private final String id;
    private final String name;

    private final List<Note> notes;
    private final List<Tutorial> tutorials;

    public SubjectInfo(String id,
                       String name,
                       List<Note> notes,
                       List<Tutorial> tutorials) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(notes);
        Preconditions.checkNotNull(tutorials);
        this.id = id;
        this.name = name;
        this.notes = notes;
        this.tutorials = tutorials;
    }

    @Override
    public int compareTo(SubjectInfo o) {
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SubjectInfo)) {
            return false;
        }
        SubjectInfo that = (SubjectInfo) obj;
        return id.equals(that.id) &&
               name.equals(that.name) &&
               notes.equals(that.notes) &&
               tutorials.equals(that.tutorials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                name,
                notes,
                tutorials
        );
    }
}
