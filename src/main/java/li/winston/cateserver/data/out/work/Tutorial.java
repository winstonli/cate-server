package li.winston.cateserver.data.out.work;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Created by winston on 19/01/2016.
 */
public class Tutorial implements Comparable<Tutorial> {

    private final int sequence;
    private final String name;
    private final String category;

    @Nullable
    private final String specUrl;

    public Tutorial(int sequence, String name, String category, String specUrl) {
        this.sequence = sequence;
        this.name = name;
        this.category = category;
        this.specUrl = specUrl;
    }

    @Override
    public int compareTo(Tutorial o) {
        return Integer.compare(sequence, o.sequence);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tutorial)) {
            return false;
        }
        Tutorial that = (Tutorial) obj;
        return sequence == that.sequence &&
                name.equals(that.name) &&
                category.equals(that.category) &&
                Objects.equals(specUrl, that.specUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                sequence,
                name,
                category,
                specUrl
        );
    }

}
