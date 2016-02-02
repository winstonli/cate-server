package li.winston.cateserver.data.parsed.notes;

import li.winston.cateserver.util.GsonInstance;

import java.util.List;

/**
 * Created by winston on 19/01/2016.
 */
public class SubjectNotes {

    private final String subjectId;
    private final List<Note> notes;

    public SubjectNotes(String subjectId, List<Note> notes) {
        this.subjectId = subjectId;
        this.notes = notes;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public List<Note> getNotes() {
        return notes;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SubjectNotes)) {
            return false;
        }
        SubjectNotes that = (SubjectNotes) obj;
        return notes.equals(that.notes);
    }

    @Override
    public int hashCode() {
        return notes.hashCode();
    }

    @Override
    public String toString() {
        return GsonInstance.gson.toJson(this);
    }

}
