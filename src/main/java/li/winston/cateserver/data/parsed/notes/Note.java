package li.winston.cateserver.data.parsed.notes;

import com.google.common.base.Preconditions;
import li.winston.cateserver.util.GsonInstance;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by winston on 19/01/2016.
 */
public class Note implements Comparable<Note> {

    private final int sequence;
    private final String specName;

    @Nullable
    private final String specLink;
    private final String fileType;
    private final long size;
    private final LocalDateTime loaded;
    private final String owner;
    private final int hits;

    public Note(int sequence,
                String specName,
                String specLink,
                String fileType,
                long size,
                LocalDateTime loaded,
                String owner,
                int hits) {
        Preconditions.checkNotNull(specName);
        Preconditions.checkNotNull(fileType);
        Preconditions.checkNotNull(loaded);
        Preconditions.checkNotNull(owner);
        Preconditions.checkArgument(sequence > 0 && size >= 0 && hits >= 0);
        this.sequence = sequence;
        this.specName = specName;
        this.specLink = specLink;
        this.fileType = fileType;
        this.size = size;
        this.loaded = loaded;
        this.owner = owner;
        this.hits = hits;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Note)) {
            return false;
        }
        Note that = (Note) obj;
        return sequence == that.sequence &&
               specName.equals(that.specName) &&
               Objects.equals(specLink, that.specLink) &&
               fileType.equals(that.fileType) &&
               size == that.size &&
               loaded.equals(that.loaded) &&
               owner.equals(that.owner) &&
               hits == that.hits;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                sequence,
            specName,
            specLink,
            fileType,
            size,
            loaded,
            owner,
            hits
        );
    }

    @Override
    public String toString() {
        return GsonInstance.gson.toJson(this);
    }

    @Override
    public int compareTo(Note o) {
        return Integer.compare(sequence, o.sequence);
    }

}
