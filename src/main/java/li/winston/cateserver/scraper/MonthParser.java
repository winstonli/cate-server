package li.winston.cateserver.scraper;

import javafx.util.Pair;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by winston on 19/01/2016.
 */
public class MonthParser {

    private static final
    Map<String, Function<Pair<Integer, String>, LocalDateTime>> periodMonthResolvers = new HashMap<String, Function<Pair<Integer, String>, LocalDateTime>>() {
        {
            put("autumn", p -> {
                String mon = p.getValue().toLowerCase();
                if (mon.startsWith("a")) {
                    return LocalDateTime.of(p.getKey(), 8, 1, 0, 0);
                }
                if (mon.startsWith("s")) {
                    return LocalDateTime.of(p.getKey(), 9, 1, 0, 0);
                }
                if (mon.startsWith("o")) {
                    return LocalDateTime.of(p.getKey(), 10, 1, 0, 0);
                }
                if (mon.startsWith("n")) {
                    return LocalDateTime.of(p.getKey(), 11, 1, 0, 0);
                }
                throw new IllegalArgumentException("autumn " + p.getValue());
            });
            put("christmas", p -> LocalDateTime.of(p.getKey(), 12, 1, 0, 0));
            put("spring", p -> {
                String mon = p.getValue().toLowerCase();
                if (mon.startsWith("d")) {
                    return LocalDateTime.of(p.getKey(), 12, 1, 0, 0);
                }
                if (mon.startsWith("j")) {
                    return LocalDateTime.of(p.getKey() + 1, 1, 1, 0, 0);
                }
                throw new IllegalArgumentException("spring " + p.getValue());
            });
            put("easter", p -> {
                String mon = p.getValue().toLowerCase();
                if (mon.startsWith("m")) {
                    return LocalDateTime.of(p.getKey() + 1, 3, 1, 0, 0);
                }
                if (mon.startsWith("a")) {
                    return LocalDateTime.of(p.getKey() + 1, 4, 1, 0, 0);
                }
                throw new IllegalArgumentException("easter " + p.getValue());
            });
            put("summer", p -> {
                String mon = p.getValue().toLowerCase();
                if (mon.startsWith("a")) {
                    return LocalDateTime.of(p.getKey() + 1, 4, 1, 0, 0);
                }
                if (mon.startsWith("m")) {
                    return LocalDateTime.of(p.getKey() + 1, 5, 1, 0, 0);
                }
                throw new IllegalArgumentException("summer " + p.getValue());
            });
            put("june-july", p -> {
                String mon = p.getValue().toLowerCase();
                if (mon.equals("july")) {
                    return LocalDateTime.of(p.getKey() + 1, 7, 1, 0, 0);
                }
                return LocalDateTime.of(p.getKey() + 1, 6, 1, 0, 0);
            });
            put("august-september", p -> {
                String mon = p.getValue().toLowerCase();
                if (mon.startsWith("a")) {
                    return LocalDateTime.of(p.getKey() + 1, 8, 1, 0, 0);
                }
                return LocalDateTime.of(p.getKey() + 1, 9, 1, 0, 0);
            });
        }
    };

    public static LocalDateTime getMonthAndYear(String period, int year, String startMonthText) {
        return periodMonthResolvers.get(period).apply(new Pair<>(year, startMonthText));
    }

}
