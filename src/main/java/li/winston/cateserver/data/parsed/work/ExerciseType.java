package li.winston.cateserver.data.parsed.work;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by winston on 17/01/2016.
 */
public enum ExerciseType {

    WHITE,
    GREY,
    GREEN,
    PINK;

    private static final
    Map<String, ExerciseType> hexMap = new HashMap<String, ExerciseType>() {
        {
            put("white", WHITE);
            put("cdcdcd", GREY);
            put("ccffcc", GREEN);
            put("f0ccf0", PINK);
        }
    };

    public static ExerciseType fromCateColour(String hexColour) {
        int len = hexColour.length();
        Preconditions.checkArgument(hexColour.equals("white") || len == 6 || len == 7 && hexColour.startsWith("#"));
        hexColour = hexColour.toLowerCase();
        if (len == 7) {
            hexColour = hexColour.substring(1);
        }
        ExerciseType type = hexMap.get(hexColour);
        Preconditions.checkNotNull(type);
        return type;
    }

    public static boolean isCateColour(String hexColour) {
        if (hexColour.length() == 7) {
            hexColour = hexColour.substring(1);
        }
        ExerciseType type = hexMap.get(hexColour.toLowerCase());
        return type != null;
    }

    public static void main(String[] args) {
        System.out.println();
    }

}
