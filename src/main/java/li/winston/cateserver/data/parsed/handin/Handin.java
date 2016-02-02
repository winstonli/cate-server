package li.winston.cateserver.data.parsed.handin;

import li.winston.cateserver.util.GsonInstance;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by winston on 18/01/2016.
 */
public class Handin {

    public static Handin NONE = new Handin(null, false);

    private final LocalDateTime due;
    private final boolean submitted;

    public Handin(LocalDateTime due, boolean submitted) {
        this.due = due;
        this.submitted = submitted;
    }

    public LocalDateTime getDue() {
        return due;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Handin)) {
            return false;
        }
        Handin that = (Handin) obj;
        return due.equals(that.due) && submitted == that.submitted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(due, submitted);
    }

    @Override
    public String toString() {
        return GsonInstance.gson.toJson(this);
    }

}
