package li.winston.cateserver.scraper;

import li.winston.cateserver.data.out.timetable.Event;
import li.winston.cateserver.data.out.timetable.EventType;
import li.winston.cateserver.data.out.timetable.Timetable;
import li.winston.cateserver.data.parsed.handin.Handin;
import li.winston.cateserver.data.parsed.notes.Note;
import li.winston.cateserver.data.parsed.notes.SubjectNotes;
import li.winston.cateserver.data.parsed.personal.Personal;
import li.winston.cateserver.data.parsed.work.Exercise;
import li.winston.cateserver.data.parsed.work.ExerciseType;
import li.winston.cateserver.data.parsed.work.Subject;
import li.winston.cateserver.data.parsed.work.Work;
import li.winston.cateserver.transport.net.CateNetTransport;
import li.winston.cateserver.util.Log;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by winston on 17/01/2016.
 */
public class PageScraper {

    private final HtmlCleaner parser = new HtmlCleaner();

    private static String asHtmlString(TagNode root) {
        return new PrettyXmlSerializer(((Supplier<CleanerProperties>) () -> {
            CleanerProperties conf = new CleanerProperties();
            conf.setOmitXmlDeclaration(true);
            return conf;
        }).get()).getAsString(root);
    }

    private <R> R parse(InputStream in, Function<TagNode, R> f) throws IOException {
        TagNode htmlRoot = parser.clean(in);
        try {
            return f.apply(htmlRoot);
        } catch (Throwable t) {
            Log.warn(
                    "Exception thrown when parsing: html dumped\n"
                            + asHtmlString(htmlRoot),
                    t
            );
            throw new RuntimeException(t);
        }
    }

    public Personal parsePersonal(InputStream in) throws IOException {
        return parse(in, this::doParsePersonal);
    }

    public Work parseWork(InputStream in) throws IOException {
        return parse(in, this::doParseWork);
    }

    public Timetable parseTimetable(InputStream in) throws IOException {
        return parse(in, this::doParseTimetable);
    }

    public Handin parseHandin(InputStream in) throws IOException {
        return parse(in, this::doParseHandin);
    }

    public SubjectNotes parseNotes(InputStream in) throws IOException {
        return parse(in, this::doParseNotes);
    }

    private Personal doParsePersonal(TagNode htmlRoot) {
        TagNode tbodyUserInfo = htmlRoot.getElementsByName("tbody", true)[1];
        TagNode[] trRows = tbodyUserInfo.getElementsByName("tr", false);

        TagNode[] tdsWithPicAndName = trRows[0].getElementsByName("td", false);
        String picUrl = CateNetTransport.resolveUrl(tdsWithPicAndName[0].getElementsByName("img", false)[0].getAttributeByName("src"));

        Name name = Name.fromFullname(tdsWithPicAndName[1].getText().toString());

        String firstName = name.firstName;
        String lastName = name.lastName;

        TagNode[] tdsWithLoginAndCid = trRows[1].getElementsByName("td", false);

        String login = tdsWithLoginAndCid[0].getElementsByName("b", false)[0].getText().toString();

        String cid = tdsWithLoginAndCid[2].getElementsByName("b", false)[0].getText().toString();

        TagNode[] tdsWithStatusAndDep = trRows[2].getElementsByName("td", false);

        String status = tdsWithStatusAndDep[0].getElementsByName("b", false)[0].getText().toString();

        String dep = tdsWithStatusAndDep[2].getElementsByName("b", false)[0].getText().toString();

        String course = trRows[3].getElementsByName("td", false)[0].getElementsByName("b", false)[0].getText().toString().split(" ")[3];

        String email = trRows[4].getElementsByName("td", false)[0].getElementsByName("b", false)[0].getText().toString();

        List<Object> tutorTdChildren = trRows[5].getElementsByName("td", false)[0].getElementsByName("b", false)[0].getAllChildren();

        String personalTutor = tutorTdChildren.get(0).toString();

        String personalTutorLogin = tutorTdChildren.get(2).toString().replaceAll("\\(|\\)", "");

        TagNode[] trsPeriod = htmlRoot.getElementsByName("form", true)[1].getElementsByName("tbody", true)[3].getElementsByName("tr", false);

        int period = -1;

        for (int i = 0; i < trsPeriod.length; ++i) {
            if (trsPeriod[i].getElementsByName("img", true).length > 0) {
                period = i + 1;
                break;
            }
        }

        if (period == -1) {
            throw new IllegalStateException("Unable to get period");
        }

        return new Personal(
            picUrl,
            firstName,
            lastName,
            login,
            cid,
            status,
            dep,
            course,
            email,
            personalTutor,
            personalTutorLogin,
            period);
    }

    private Work doParseWork(TagNode htmlRoot) {
        LocalDateTime startDate = parseTimetableStartDate(htmlRoot);

        LocalDateTime today = parseTimetableToday(htmlRoot, startDate);

        List<Subject> subjects = parseTimetableSubjects(htmlRoot, startDate);

        return new Work(today, startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)), subjects);
    }

    private Timetable doParseTimetable(TagNode htmlRoot) {
        Timetable timetable = new Timetable();
        TagNode[] trs;
        try {
            trs = ((TagNode) htmlRoot.evaluateXPath("//*[@id='timetable-container']")[0]).getElementsByName("tbody", true)[0].getElementsByName("tr", false);
        } catch (XPatherException e) {
            throw new RuntimeException(e);
        }
        TagNode[] trsTimeSlots = (TagNode[]) ArrayUtils.subarray(trs, 1, trs.length);
        for (TagNode trTimeSlots : trsTimeSlots) {
            LocalTime startTime = LocalTime.parse(trTimeSlots.getElementsByName("th", false)[0].getText().toString(), DateTimeFormatter.ofPattern("H:m"));
            LocalTime endTime = startTime.plusHours(1);
            TagNode[] tdDays = trTimeSlots.getElementsByName("td", false);
            for (int i = 0; i < 5; ++i) {
                DayOfWeek day = DayOfWeek.of(i + 1);
                TagNode[] ps = tdDays[i].getElementsByName("p", false);
                for (TagNode p : ps) {
                    List<Object> nodes = p.getAllChildren();
                    String title = ((TagNode) nodes.get(1)).getText().toString().trim();
                    String info = nodes.get(4).toString().trim();
                    String lecturer = nodes.get(6).toString().trim();

                    String subjectId = null;
                    String subjectName;

                    String subjectDesc = StringUtils.substringBefore(title, " ");
                    if (subjectDesc.matches(".*\\d.*")) {
                        subjectId = subjectDesc.replaceFirst("CO", "");
                        subjectName = StringUtils.substringAfter(title, " ");
                    } else {
                        subjectName = title;
                    }

                    EventType eventType = EventType.parse(StringUtils.substringBefore(info, "(").trim());

                    LinkedHashSet<Integer> weeks = new LinkedHashSet<>();
                    String[] separated = StringUtils.substringBefore(StringUtils.substringAfter(info, "("), ")").split(",");
                    for (String week : separated) {
                        if (week.contains("-")) {
                            String[] range = week.split("-");
                            weeks.addAll(IntStream.range(Integer.parseInt(range[0]), Integer.parseInt(range[1]) + 1).boxed().collect(Collectors.toList()));
                        } else {
                            weeks.add(Integer.parseInt(week));
                        }
                    }

                    Set<String> courses = new HashSet<>(Arrays.asList(StringUtils.substringBefore(StringUtils.substringAfter(info, "/"), "/").trim().split(",")));

                    List<String> rooms = Arrays.asList(StringUtils.substringAfter(StringUtils.substringAfter(info, "/"), "/").trim().split("; ")).stream().filter(room -> !room.isEmpty()).collect(Collectors.toList());

                    List<String> lecturers = Arrays.asList(lecturer.split("; ")).stream().filter(l -> !l.isEmpty()).collect(Collectors.toList());

                    timetable.add(
                            new Event(
                                    day,
                                    subjectId,
                                    subjectName,
                                    eventType,
                                    weeks,
                                    courses,
                                    rooms,
                                    lecturers,
                                    startTime,
                                    endTime
                            )
                    );
                }
            }
        }
        return timetable;
    }

    LocalDateTime parseTimetableStartDate(TagNode htmlRoot) {
        TagNode[] trsMain = htmlRoot.getElementsByName("body", false)[0].getElementsByName("table", false)[0].getElementsByName("tbody", false)[0].getElementsByName("tr", false);

        String[] periodTokens = htmlRoot.getElementsByName("h1", true)[0].getText().toString().trim().split("\\s+");
        String period = periodTokens[0].toLowerCase();
        int year = Integer.parseInt(periodTokens[2].split("-")[0]);

        String startMonthText = trsMain[0].getText().toString().trim().split(" |\\n")[0];

        LocalDateTime monthAndYear = MonthParser.getMonthAndYear(period, year, startMonthText);

        TagNode[] thsDate = trsMain[2].getElementsByName("th", false);

        int startDay;

        String first = thsDate[1].getText().toString().trim();
        if (first.isEmpty()) {
            startDay = Integer.parseInt(thsDate[3].getText().toString()) - 2;
        } else {
            startDay = Integer.parseInt(first);
        }

        int daysInMonth = YearMonth.of(monthAndYear.getYear(), monthAndYear.getMonth()).lengthOfMonth();
        startDay = (startDay + daysInMonth) % daysInMonth;

        return monthAndYear.withDayOfMonth(startDay);
    }

    private LocalDateTime parseTimetableToday(TagNode htmlRoot, LocalDateTime startDate) {
        TagNode[] ths = htmlRoot.getElementsByName("body", false)[0].getElementsByName("table", false)[0].getElementsByName("tbody", false)[0].getElementsByName("tr", false)[2].getElementsByName("th", false);
        int offset = -4;
        for (TagNode th : ths) {
            if (th.hasAttribute("bgcolor") && th.getAttributeByName("bgcolor").toLowerCase().equals("lightskyblue")) {
                return startDate.plusDays(offset);
            }
            if (th.hasAttribute("colspan")) {
                offset += Integer.parseInt(th.getAttributeByName("colspan"));
            } else {
                ++offset;
            }
        }
        return null;
    }

    private List<Subject> parseTimetableSubjects(TagNode htmlRoot, LocalDateTime startDate) {
        TagNode[] trsMain = htmlRoot.getElementsByName("body", false)[0].getElementsByName("table", false)[0].getElementsByName("tbody", false)[0].getElementsByName("tr", false);

        List<Subject> subjects = new ArrayList<Subject>();

        for (int i = 0; i < trsMain.length; ++i) {
            String possibleBreak = trsMain[i].getText().toString().toLowerCase().replaceAll("\\s+", " ");
            if (possibleBreak.contains("modules above this line are subscribed to at level 2 or higher")) {
                break;
            }
            TagNode[] tdsTr = trsMain[i].getElementsByName("td", false);
            if (tdsTr.length > 0 && tdsTr[0].hasAttribute("rowspan")) {
                TagNode tdSubject = tdsTr[1];
                TagNode bSubjectDesc = tdSubject.getElementsByName("b", false)[0];
                String subjectId = bSubjectDesc.getElementsByName("font", false)[0].getText().toString();
                String subjectName = StringUtils.stripStart(bSubjectDesc.getAllChildren().get(1).toString(), " -");

                TagNode[] aSubjectLinks = tdSubject.getElementsByName("a", false);

                String notesUrl = null;

                for (TagNode aSubjectLink : aSubjectLinks) {
                    String href = aSubjectLink.getAttributeByName("href");
                    if (href != null && href.startsWith("notes.cgi")) {
                        notesUrl = CateNetTransport.resolveUrl(href);
                        break;
                    }
                }

                int rows = Integer.parseInt(tdSubject.getAttributeByName("rowspan"));

                TagNode[][] exRowTdArr = new TagNode[rows][];
                exRowTdArr[0] = (TagNode[]) ArrayUtils.subarray(tdsTr, 3, tdsTr.length);

                for (int j = 1; j < rows; ++j) {
                    exRowTdArr[j] = trsMain[i + j].getElementsByName("td", false);
                }

                int totalDays = htmlRoot.getElementsByName(
                        "body",
                        false
                )[0].getElementsByName(
                        "table",
                        false
                )[0].getElementsByName(
                        "tbody",
                        false
                )[0].getElementsByName(
                        "tr",
                        false
                )[2].getElementsByName(
                        "th",
                        false
                ).length - 1;

                List<Exercise> exercises = getExercises(exRowTdArr, startDate, totalDays);

                subjects.add(
                    new Subject(
                        subjectId,
                        subjectName,
                        notesUrl,
                        exercises
                    )
                );
            }
        }

        return subjects;
    }

    private List<Exercise> getExercises(TagNode[][] exRowTdArr, LocalDateTime startDate, int totalDays) {
        List<Exercise> exercises = new ArrayList<>();
        for (TagNode[] tdsExRow : exRowTdArr) {
            List<Exercise> exercisesFromRow = getExercisesFromRow(tdsExRow, startDate, totalDays);
            exercises.addAll(exercisesFromRow);
        }
        return exercises;
    }

    private List<Exercise> getExercisesFromRow(TagNode[] tdsExRow, LocalDateTime startDate, int totalDays) {
        int len = tdsExRow.length;
        Optional<LocalDateTime> before = Optional.empty();
        Optional<LocalDateTime> after = Optional.empty();
        TagNode tdFirst = tdsExRow[0];
        if (!tdFirst.isEmpty()) {
            before = Optional.of(LocalDate.parse(tdFirst.getText().toString().replaceAll("\\s+", ""), DateTimeFormatter.ofPattern("dMMMyyyy")).atStartOfDay());
        }
        TagNode tdLast = tdsExRow[len - 1];
        if (tdLast.hasAttribute("align") && tdLast.getAttributeByName("align").toLowerCase().equals("center")) {
            after = Optional.of(LocalDate.parse(tdLast.getText().toString().replaceAll("\\s+", ""), DateTimeFormatter.ofPattern("dMMMyyyy")).plusDays(1).atStartOfDay());
            --len;
        }
        List<Exercise> exercises = new ArrayList<Exercise>();
        int col = 0;
        for (int i = 1; i < len; ++i) {
            int colspan = 1;
            TagNode td = tdsExRow[i];
            if (td.hasAttribute("colspan")) {
                colspan = Integer.parseInt(td.getAttributeByName("colspan"));
            }
            if (td.hasAttribute("bgcolor")) {
                String bgcolor = td.getAttributeByName("bgcolor");
                if (ExerciseType.isCateColour(bgcolor)) {
                    ExerciseType type = ExerciseType.fromCateColour(bgcolor);
                    String name = null;
                    TagNode[] bs = td.getElementsByName("b", false);
                    String[] seqAndCategory;
                    if (bs.length == 0) {
                        /* There's no first bold element. Either the exercise
                         * name was too short and so the 1:CW was wrapped in
                          * a span with alt text, or the exercise had no name */
                        TagNode[] spans = td.getElementsByName("span", false);
                        if (spans.length == 0) {
                            String seqAndCategoryStr = td.getElementsByName("b", true)[0].getText().toString();
                            name = "No name (" + seqAndCategoryStr + ")";
                            seqAndCategory = seqAndCategoryStr.split(":");
                        } else {
                            TagNode span = td.getElementsByName("span", false)[0];
                            name = span.getAttributeByName("alt");
                            seqAndCategory = span.getElementsByName("b", false)[0].getText().toString().split(":");
                        }
                    } else {
                        /* The first bold element contains the sequence and
                        * category, e.g. 1:CW */
                        seqAndCategory = bs[0].getText().toString().split(":");
                    }
                    int seq = Integer.parseInt(seqAndCategory[0]);
                    String category = seqAndCategory[1];
                    TagNode[] aLinks = td.getElementsByName("a", false);
                    String handinUrl = null;
                    String givenUrl = null;
                    String specUrl = null;
                    String ownerEmail = null;
                    for (TagNode aLink : aLinks) {
                        String href = aLink.getAttributeByName("href");
                        if (href.startsWith("showfile.cgi")) {
                            specUrl = CateNetTransport.resolveUrl(href);
                            /* It could have been a nameless exercise */
                            if (name == null) {
                                name = aLink.getText().toString().trim();
                            }
                        } else if (href.startsWith("given.cgi")) {
                            givenUrl = CateNetTransport.resolveUrl(href);
                        } else if (href.startsWith("handins.cgi")) {
                            handinUrl = CateNetTransport.resolveUrl(href);
                        } else if (href.startsWith("mailto:")) {
                            ownerEmail = StringUtils.substringBefore(StringUtils.substringAfter(href, "mailto:"), "?");
                        }
                    }
                    if (specUrl == null && name == null) {
                        name = StringUtils.join(
                            ((List<Object>) td.getAllChildren())
                                .stream()
                                .filter(c -> c instanceof ContentNode)
                                .map(Object::toString)
                                .collect(Collectors.toList()),
                            ""
                        ).trim();
                    }
                    LocalDateTime startTime;
                    if (col == 0 && before.isPresent()) {
                        startTime = before.get();
                    } else {
                        startTime = startDate.plusDays(col);
                    }
                    LocalDateTime endDay;
                    if (col + colspan == totalDays && after.isPresent()) {
                        endDay = after.get();
                    } else {
                        endDay = startDate.plusDays(col + colspan);
                    }
                    exercises.add(
                        new Exercise(
                            name,
                            category,
                            type,
                            seq,
                            startTime,
                            endDay,
                            ownerEmail,
                            givenUrl,
                            specUrl,
                            handinUrl
                        )
                    );
                }
            }
            col += colspan;
        }
        return exercises;
    }

    private Handin doParseHandin(TagNode htmlRoot) {
        TagNode tdWithDueTime = htmlRoot.getElementsByName("body", false)[0].getElementsByName("ul", false)[1].getElementsByName("tbody", true)[1].getElementsByName("tr", false)[1].getElementsByName("td", false)[3];
        String dueDate = StringUtils.substringBefore(tdWithDueTime.getText().toString().split(" - ")[1], "*");
        LocalDateTime due = LocalDateTime.parse(dueDate, DateTimeFormatter.ofPattern("d MMM yyyy(H:m)"));
        String pageText = htmlRoot.getText().toString();
        boolean submitted = !pageText.contains("No declaration record exists for this exercise") && !pageText.contains("NOT SUBMITTED");
        return new Handin(due, submitted);
    }

    private SubjectNotes doParseNotes(TagNode htmlRoot) {
        String courseId = parseNotesCourseId(htmlRoot);
        List<Note> noteList = parseNoteList(htmlRoot.getElementsByName("form", true)[0].getElementsByName("tbody", true)[1]);
        return new SubjectNotes(courseId, noteList);
    }

    String parseNotesCourseId(TagNode htmlRoot) {
        return StringUtils.substringBefore(htmlRoot.getElementsByName("center", true)[0].getElementsByName("h3", false)[1].getElementsByName("font", false)[0].getText().toString(), ":");
    }

    List<Note> parseNoteList(TagNode tagNode) {
        TagNode[] trsNoteTable = tagNode.getElementsByName("tr", false);
        TagNode[] trsNoteRows = (TagNode[]) ArrayUtils.subarray(trsNoteTable, 1, trsNoteTable.length);
        List<Note> noteList = new ArrayList<Note>(trsNoteRows.length - 1);
        for (TagNode nodeRow : trsNoteRows) {
            TagNode[] tdNoteCols = nodeRow.getElementsByName("td", false);
            if (tdNoteCols.length < 2) {
                continue;
            }
            int index = Integer.parseInt(tdNoteCols[0].getText().toString());
            TagNode[] aSpecLink = tdNoteCols[1].getElementsByName("a", false);
            String specLink = null;
            if (aSpecLink.length > 0) {
                if (aSpecLink[0].getAttributeByName("href").isEmpty()) {
                    specLink = aSpecLink[0].getAttributeByName("title");
                } else {
                    specLink = CateNetTransport.resolveUrl(aSpecLink[0].getAttributeByName("href"));
                }
            }
            String specName = tdNoteCols[1].getText().toString();
            String fileType = StringUtils.stripEnd(tdNoteCols[2].getText().toString().toLowerCase(), "*");
            long size = Long.parseLong(tdNoteCols[3].getText().toString());
            LocalDateTime loaded = LocalDateTime.parse(tdNoteCols[4].getText().toString().replaceAll(" +", " "), DateTimeFormatter.ofPattern("EEE MMM d HH:mm:ss yyyy"));
            String owner = tdNoteCols[5].getText().toString();
            int hits = Integer.parseInt(tdNoteCols[6].getText().toString());
            noteList.add(new Note(
                    index,
                    specName,
                    specLink,
                    fileType,
                    size,
                    loaded,
                    owner,
                    hits
            ));
        }
        return noteList;
    }

}
