package li.winston.cateserver.scraper;

import li.winston.cateserver.data.out.timetable.Timetable;
import li.winston.cateserver.data.parsed.handin.Handin;
import li.winston.cateserver.data.parsed.notes.SubjectNotes;
import li.winston.cateserver.data.parsed.personal.Personal;
import li.winston.cateserver.data.parsed.work.Work;
import li.winston.cateserver.util.GsonInstance;
import org.htmlcleaner.HtmlCleaner;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

/**
 * Created by winston on 17/01/2016.
 */
public class PageScraperTest {

    private final PageScraper pageScraper = new PageScraper();

    private static final Personal winstonPersonal = new Personal(
        "https://cate.doc.ic.ac.uk/photo/student/pics12/wl3912.jpg",
        "Winston",
        "Li",
        "wl3912",
        "00733546",
        "Active",
        "CO",
        "c4",
        "winston.li12@imperial.ac.uk",
        "Timothy Kimber",
        "tk106",
        3);

    private InputStream getStream(String name) {
        return getClass().getResourceAsStream(name);
    }

    private <T> T fromJsonFile(String file, Class<T> type) {
        return GsonInstance.gson.fromJson(new InputStreamReader(getStream(file)), type);
    }

    @Test
    public void testOnePlusTwo() {
        assertEquals(3, 1 + 2);
    }

    @Test
    public void testParsePersonal() throws Exception {
        InputStream in = getStream("/li/winston/cateserver/scraper/personal1.html");
        Personal personal = pageScraper.parsePersonal(in);
        assertEquals(winstonPersonal, personal);
    }

    @Test
    public void testParseTimetable2015Autumn() throws Exception {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2015-1.html");
        Work work = pageScraper.parseWork(in);
        Work expected = fromJsonFile("/li/winston/cateserver/scraper/timetable_2015-1.json", Work.class);
        assertEquals(expected, work);
    }

    @Test
    public void testParseTimetable2015Christmas() throws Exception {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2015-2.html");
        Work work = pageScraper.parseWork(in);
        Work expected = fromJsonFile("/li/winston/cateserver/scraper/timetable_2015-2.json", Work.class);
        assertEquals(expected, work);
    }

    @Test
    public void testParseTimetable2015Spring() throws Exception {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2015-3.html");
        Work expected = fromJsonFile("/li/winston/cateserver/scraper/timetable_2015-3.json", Work.class);
        Work work = pageScraper.parseWork(in);
        assertEquals(expected, work);
    }

    @Test
    public void testParseTimetable2015Easter() throws Exception {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2015-4.html");
        Work expected = fromJsonFile("/li/winston/cateserver/scraper/timetable_2015-4.json", Work.class);
        Work work = pageScraper.parseWork(in);
        assertEquals(expected, work);
    }

    @Test
    public void testParseTimetable2015Summer() throws Exception {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2015-5.html");
        Work expected = fromJsonFile("/li/winston/cateserver/scraper/timetable_2015-5.json", Work.class);
        Work work = pageScraper.parseWork(in);
        assertEquals(expected, work);
    }

    @Test
    public void testParseTimetable2015JuneJuly() throws Exception {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2015-6.html");
        Work expected = fromJsonFile("/li/winston/cateserver/scraper/timetable_2015-6.json", Work.class);
        Work work = pageScraper.parseWork(in);
        assertEquals(expected, work);
    }

    @Test
    public void testParseTimetable2015AugustSeptember() throws Exception {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2015-7.html");
        Work expected = fromJsonFile("/li/winston/cateserver/scraper/timetable_2015-7.json", Work.class);
        Work work = pageScraper.parseWork(in);
        assertEquals(expected, work);
    }

    @Test
    public void testParseTimetableCrash28022016() throws Exception {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_crash-28022016.html");
        Work work = pageScraper.parseWork(in);
        Work expected = fromJsonFile("/li/winston/cateserver/scraper/timetable_crash-28022016.json", Work.class);
        assertEquals(expected, work);
    }

    @Test
    public void testStartDate2012Autumn() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2012-1.html");
        LocalDateTime startDate = pageScraper.parseTimetableStartDate(new HtmlCleaner().clean(in));
        assertEquals(LocalDateTime.of(2012, 9, 29, 0, 0), startDate);
    }

    @Test
    public void testStartDate2012Christmas() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2012-2.html");
        LocalDateTime startDate = pageScraper.parseTimetableStartDate(new HtmlCleaner().clean(in));
        assertEquals(LocalDateTime.of(2012, 12, 15, 0, 0), startDate);
    }

    @Test
    public void testStartDate2012Spring() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2012-3.html");
        LocalDateTime startDate = pageScraper.parseTimetableStartDate(new HtmlCleaner().clean(in));
        assertEquals(LocalDateTime.of(2013, 1, 5, 0, 0), startDate);
    }

    @Test
    public void testStartDate2012Easter() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2012-4.html");
        LocalDateTime startDate = pageScraper.parseTimetableStartDate(new HtmlCleaner().clean(in));
        assertEquals(LocalDateTime.of(2013, 3, 23, 0, 0), startDate);
    }

    @Test
    public void testStartDate2012Summer() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2012-5.html");
        LocalDateTime startDate = pageScraper.parseTimetableStartDate(new HtmlCleaner().clean(in));
        assertEquals(LocalDateTime.of(2013, 4, 27, 0, 0), startDate);
    }

    @Test
    public void testStartDate2012JuneJuly() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2012-6.html");
        LocalDateTime startDate = pageScraper.parseTimetableStartDate(new HtmlCleaner().clean(in));
        assertEquals(LocalDateTime.of(2013, 6, 29, 0, 0), startDate);
    }

    @Test
    public void testStartDate2012AugustSeptember() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2012-7.html");
        LocalDateTime startDate = pageScraper.parseTimetableStartDate(new HtmlCleaner().clean(in));
        assertEquals(LocalDateTime.of(2013, 8, 1, 0, 0), startDate);
    }

    @Test
    public void testStartDate2015Autumn() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2015-1.html");
        LocalDateTime startDate = pageScraper.parseTimetableStartDate(new HtmlCleaner().clean(in));
        assertEquals(LocalDateTime.of(2015, 10, 3, 0, 0), startDate);
    }

    @Test
    public void testStartDate2015Christmas() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2015-2.html");
        LocalDateTime startDate = pageScraper.parseTimetableStartDate(new HtmlCleaner().clean(in));
        assertEquals(LocalDateTime.of(2015, 12, 19, 0, 0), startDate);
    }

    @Test
    public void testStartDate2015Spring() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2015-3.html");
        LocalDateTime startDate = pageScraper.parseTimetableStartDate(new HtmlCleaner().clean(in));
        assertEquals(LocalDateTime.of(2016, 1, 9, 0, 0), startDate);
    }

    @Test
    public void testStartDate2015Easter() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2015-4.html");
        LocalDateTime startDate = pageScraper.parseTimetableStartDate(new HtmlCleaner().clean(in));
        assertEquals(LocalDateTime.of(2016, 3, 24, 0, 0), startDate);
    }

    @Test
    public void testStartDate2015Summer() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2015-5.html");
        LocalDateTime startDate = pageScraper.parseTimetableStartDate(new HtmlCleaner().clean(in));
        assertEquals(LocalDateTime.of(2016, 4, 23, 0, 0), startDate);
    }

    @Test
    public void testStartDate2015JuneJuly() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2015-6.html");
        LocalDateTime startDate = pageScraper.parseTimetableStartDate(new HtmlCleaner().clean(in));
        assertEquals(LocalDateTime.of(2016, 6, 25, 0, 0), startDate);
    }

    @Test
    public void testStartDate2015AugustSeptember() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_2015-7.html");
        LocalDateTime startDate = pageScraper.parseTimetableStartDate(new HtmlCleaner().clean(in));
        assertEquals(LocalDateTime.of(2016, 8, 1, 0, 0), startDate);
    }

    @Test
    public void testParseHandin() throws Exception {
        InputStream in = getStream("/li/winston/cateserver/scraper/handin_nosubmit_2015-120.html");
        Handin handin = pageScraper.parseHandin(in);
        Handin expected = fromJsonFile("/li/winston/cateserver/scraper/handin_nosubmit_2015-120.json", Handin.class);
        assertEquals(expected, handin);
    }

    @Test
    public void testParseHandinFilesMissing() throws Exception {
        InputStream in = getStream("/li/winston/cateserver/scraper/handin_files_missing_2015-120.html");
        Handin handin = pageScraper.parseHandin(in);
        Handin expected = fromJsonFile("/li/winston/cateserver/scraper/handin_files_missing_2015-120.json", Handin.class);
        assertEquals(expected, handin);
    }

    @Test
    public void testParseHandinSubmitted() throws Exception {
        InputStream in = getStream("/li/winston/cateserver/scraper/handin_submitted_2015-120.html");
        Handin handin = pageScraper.parseHandin(in);
        Handin expected = fromJsonFile("/li/winston/cateserver/scraper/handin_submitted_2015-120.json", Handin.class);
        assertEquals(expected, handin);
    }

    @Test
    public void testParseNotes() throws Exception {
        InputStream in = getStream("/li/winston/cateserver/scraper/notes_crypto_2015.html");
        SubjectNotes subjectNotes = pageScraper.parseNotes(in);
        SubjectNotes expected = fromJsonFile("/li/winston/cateserver/scraper/notes_crypto_2015.json", SubjectNotes.class);
        assertEquals(expected, subjectNotes);
    }

    @Test
    public void testParseNotesWithWeirdLinks() throws Exception {
        InputStream in = getStream("/li/winston/cateserver/scraper/notes_topics_2012.html");
        SubjectNotes subjectNotes = pageScraper.parseNotes(in);
        SubjectNotes expected = fromJsonFile("/li/winston/cateserver/scraper/notes_topics_2012.json", SubjectNotes.class);
        assertEquals(expected, subjectNotes);
    }

    @Test
    public void testParseTimetablec1() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_c1_3_2-10.html");
        Timetable timetable = pageScraper.parseTimetable(in);
        Timetable expected = fromJsonFile("/li/winston/cateserver/scraper/timetable_c1_3_2-10.json", Timetable.class);
        assertEquals(expected, timetable);
    }

    @Test
    public void testParseTimetablec2() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_c2_3_2-10.html");
        Timetable timetable = pageScraper.parseTimetable(in);
        Timetable expected = fromJsonFile("/li/winston/cateserver/scraper/timetable_c2_3_2-10.json", Timetable.class);
        assertEquals(expected, timetable);
    }

    @Test
    public void testParseTimetablec3() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_c3_3_2-10.html");
        Timetable timetable = pageScraper.parseTimetable(in);
        Timetable expected = fromJsonFile("/li/winston/cateserver/scraper/timetable_c3_3_2-10.json", Timetable.class);
        assertEquals(expected, timetable);
    }

    @Test
    public void testParseTimetablec4() throws IOException {
        InputStream in = getStream("/li/winston/cateserver/scraper/timetable_c4_3_2-10.html");
        Timetable timetable = pageScraper.parseTimetable(in);
        Timetable expected = fromJsonFile("/li/winston/cateserver/scraper/timetable_c4_3_2-10.json", Timetable.class);
        assertEquals(expected, timetable);
    }

}
