package li.winston.cateserver.data.parsed.work;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Created by winston on 17/01/2016.
 */
public class Subject {

    private final String id;
    private final String name;

    @Nullable
    private final String notesUrl;

    private final List<Exercise> exercises;

    public Subject(String id, String name, String notesUrl, List<Exercise> exercises) {
        this.id = id;
        this.name = name;
        this.notesUrl = notesUrl;
        this.exercises = exercises;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getNotesUrl() {
        return notesUrl;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Subject)) {
            return false;
        }
        Subject that = (Subject) obj;
        return id.equals(that.id) &&
               name.equals(that.name) &&
               Objects.equals(notesUrl, that.notesUrl) &&
               exercises.equals(that.exercises);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, notesUrl, exercises);
    }

}
