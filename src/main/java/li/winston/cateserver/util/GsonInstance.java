package li.winston.cateserver.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import li.winston.cateserver.data.out.timetable.EventType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by winston on 18/01/2016.
 */
public class GsonInstance {

    private static final DateTimeFormatter ISO8601_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss]'Z'");
    private static final DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern("HH:mm");

    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                @Override
                public void write(JsonWriter out, LocalDateTime value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value.format(ISO8601_DATE_TIME));
                    }
                }

                @Override
                public LocalDateTime read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    return LocalDateTime.parse(in.nextString(), ISO8601_DATE_TIME);
                }
            })
            .registerTypeAdapter(LocalTime.class, new TypeAdapter<LocalTime>() {
                @Override
                public void write(JsonWriter out, LocalTime value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value.format(FORMATTER_TIME));
                    }
                }

                @Override
                public LocalTime read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    return LocalTime.parse(in.nextString(), FORMATTER_TIME);
                }
            })
            .registerTypeAdapter(EventType.class, new TypeAdapter<EventType>() {

                @Override
                public void write(JsonWriter out, EventType value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value.toString());
                    }

                }

                @Override
                public EventType read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    return EventType.parse(in.nextString());
                }

            })
            .disableHtmlEscaping()
            .serializeNulls()
            .create();

}
