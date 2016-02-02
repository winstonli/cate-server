package li.winston.cateserver.scraper;

import li.winston.cateserver.data.Auth;
import li.winston.cateserver.data.out.timetable.Event;
import li.winston.cateserver.data.out.user.UserInfo;
import li.winston.cateserver.data.out.work.*;
import li.winston.cateserver.data.parsed.handin.Handin;
import li.winston.cateserver.data.parsed.notes.Note;
import li.winston.cateserver.data.parsed.notes.SubjectNotes;
import li.winston.cateserver.data.parsed.personal.Personal;
import li.winston.cateserver.data.parsed.work.Exercise;
import li.winston.cateserver.data.parsed.work.ExerciseType;
import li.winston.cateserver.data.parsed.work.Subject;
import li.winston.cateserver.data.parsed.work.Work;
import li.winston.cateserver.transport.CateTransport;
import li.winston.cateserver.transport.net.CateNetTransport;
import li.winston.cateserver.transport.net.UnauthorizedException;
import li.winston.cateserver.transport.net.UncheckedUnauthorizedException;
import li.winston.cateserver.util.Log;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by winston on 19/01/2016.
 */
public class CateScraper {

    private final CateTransport transport;
    private final PageScraper scraper;

    public CateScraper() {
        this(new CateNetTransport());
    }

    public CateScraper(CateTransport transport) {
        this.transport = transport;
        scraper = new PageScraper();
    }

    public UserInfo scrapeUser(Auth auth) throws IOException, UnauthorizedException {
        int year = transport.home(auth);
        Personal personal = scraper.parsePersonal(transport.personal(auth, year));
        return new UserInfo(
                personal.getPicUrl(),
                personal.getFirstName(),
                personal.getLastName(),
                personal.getLogin(),
                personal.getCid(),
                personal.getStatus(),
                personal.getDepartment(),
                personal.getCourse(),
                personal.getEmail(),
                personal.getPersonalTutor(),
                personal.getPersonalTutorLogin(),
                personal.getPeriod(),
                year
        );
    }

    public WorkInfo scrapeWork(Auth auth, int year, int period, String course) throws IOException, UnauthorizedException {
        try {
            return uncheckedScrapeWork(auth, year, period, course);
        } catch (UncheckedIOException ioe) {
            throw ioe.getCause();
        } catch (UncheckedUnauthorizedException ue) {
            throw ue.getCause();
        }
    }

    public Event scrapeTimetable(Auth auth, String course) {
        throw new UnsupportedOperationException();
    }

    private WorkInfo uncheckedScrapeWork(Auth auth, int year, int period, String course) throws IOException, UnauthorizedException {
        Work work = scraper.parseWork(transport.timetable(auth, period, course, year));

        List<SubjectNotes> notes = work.getSubjects().parallelStream().filter(subject -> subject.getNotesUrl() != null).map(subject1 -> {
            try {
                return scraper.parseNotes(transport.url(auth, subject1.getNotesUrl()));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (UnauthorizedException e) {
                throw new UncheckedUnauthorizedException(e);
            } catch (Throwable t) {
                Log.warn("throwable", t);
                throw new RuntimeException(t);
            }
        }).collect(Collectors.toList());
        List<Pair<Subject, List<Pair<Exercise, Handin>>>> subjectsWithHandins = work.getSubjects().parallelStream().map(subject ->
                Pair.of(subject, subject.getExercises().stream().map(ex -> {
                    String handinUrl = ex.getHandinUrl();
                    if (handinUrl != null) {
                        try {
                            Pair<Exercise, Handin> p = Pair.of(ex, scraper.parseHandin(transport.url(auth, handinUrl)));
                            return p;
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        } catch (UnauthorizedException e) {
                            throw new UncheckedUnauthorizedException(e);
                        } catch (Throwable t) {
                            Log.warn("throwable", t);
                            throw new RuntimeException(t);
                        }
                    }
                    return Pair.of(ex, Handin.NONE);
                }).collect(Collectors.toList()))
        ).collect(Collectors.toList());

        return extractWorkInfo(work, notes, subjectsWithHandins);
    }

    WorkInfo extractWorkInfo(Work work, List<SubjectNotes> notesList, List<Pair<Subject, List<Pair<Exercise, Handin>>>> subjectsWithHandins) {
        Map<String, List<Note>> subjectNotes = new HashMap<>();
        Map<String, List<Tutorial>> subjectTutorials = new HashMap<>();
        List<Subject> parsedSubjects = work.getSubjects();
        parsedSubjects.forEach(subject -> {
            subjectNotes.put(subject.getId(), new ArrayList<>());
            subjectTutorials.put(subject.getId(), new ArrayList<>());
        });
        notesList.forEach(notes ->
            notes.getNotes().forEach(noteList ->
                subjectNotes.put(notes.getSubjectId(), notes.getNotes())
            )
        );

        List<Deadline> deadlines = new ArrayList<Deadline>();
        List<Upcoming> upcoming = new ArrayList<Upcoming>();
        subjectsWithHandins.forEach(sh -> {
            LocalDateTime today = work.getToday();
            sh.getRight().forEach(eh -> {
                String subjectId = sh.getLeft().getId();
                Exercise ex = eh.getLeft();
                Handin handin = eh.getRight();
                if (ex.getType() == ExerciseType.WHITE) {
                    subjectTutorials.get(subjectId).add(new Tutorial(ex.getSequence(), ex.getName(), ex.getCategory(), ex.getSpecUrl()));
                } else {
                    LocalDateTime startTime = ex.getStartTime();
                    LocalDateTime dueTime = (LocalDateTime) ObjectUtils.min(ex.getEndDay(), handin.getDue());
                    if (today.compareTo(startTime) >= 0 && today.compareTo(dueTime) <= 0) {
                        if (ex.getSpecUrl() == null || handin != Handin.NONE) {
                            deadlines.add(
                                new Deadline(
                                    subjectId,
                                    ex.getSequence(),
                                    ex.getName(),
                                    ex.getCategory(),
                                    ex.getType(),
                                    startTime,
                                    dueTime,
                                    handin.isSubmitted(),
                                    ex.getSpecUrl()
                                )
                            );
                        }
                    } else if (today.compareTo(startTime) < 0) {
                        upcoming.add(
                            new Upcoming(
                                subjectId,
                                ex.getSequence(),
                                ex.getName(),
                                ex.getCategory(),
                                ex.getType(),
                                startTime
                            )
                        );
                    }
                }
            });
        });
        List<SubjectInfo> subjects = parsedSubjects.stream().map(subject -> {
            List<Tutorial> tutorials = subjectTutorials.get(subject.getId());
            tutorials.sort(null);
            return new SubjectInfo(
                    subject.getId(),
                    subject.getName(),
                    subjectNotes.get(subject.getId()),
                    tutorials
            );
        }
        ).collect(Collectors.toList());
        return new WorkInfo(subjects, deadlines, upcoming, work.getFirstDayOfTerm());
    }

}
