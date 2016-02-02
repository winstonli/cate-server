package li.winston.cateserver.data.parsed.work;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by winston on 17/01/2016.
 */
public class Exercise {

    private final String name;
    private final String category;
    private final ExerciseType type;
    private final int sequence;
    private final LocalDateTime startTime;
    private final LocalDateTime endDay;
    private final String ownerEmail;

    @Nullable
    private final String handinUrl;

    @Nullable
    private final String givenUrl;

    @Nullable
    private final String specUrl;

    public Exercise(String name, String category, ExerciseType type, int sequence, LocalDateTime startTime, LocalDateTime endDay, String ownerEmail, String givenUrl, String specUrl, String handinUrl) {
        this.name = name;
        this.category = category;
        this.type = type;
        this.sequence = sequence;
        this.startTime = startTime;
        this.endDay = endDay;
        this.ownerEmail = ownerEmail;
        this.handinUrl = handinUrl;
        this.givenUrl = givenUrl;
        this.specUrl = specUrl;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public ExerciseType getType() {
        return type;
    }

    public int getSequence() {
        return sequence;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndDay() {
        return endDay;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    @Nullable
    public String getHandinUrl() {
        return handinUrl;
    }

    @Nullable
    public String getGivenUrl() {
        return givenUrl;
    }

    @Nullable
    public String getSpecUrl() {
        return specUrl;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Exercise)) {
            return false;
        }
        Exercise that = (Exercise) obj;
        return name.equals(that.name) &&
               category.equals(that.category) &&
               type.equals(that.type) &&
               sequence == that.sequence &&
               startTime.equals(that.startTime) &&
               endDay.equals(that.endDay) &&
               ownerEmail.equals(that.ownerEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            name,
            category,
            type,
            sequence,
            startTime,
            endDay,
            ownerEmail
        );
    }

}
