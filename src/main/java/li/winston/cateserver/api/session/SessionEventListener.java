package li.winston.cateserver.api.session;

import li.winston.cateserver.data.out.timetable.Timetable;
import li.winston.cateserver.data.out.user.UserInfo;
import li.winston.cateserver.data.out.work.WorkInfo;

/**
 * Created by winston on 21/01/2016.
 */
public interface SessionEventListener {

    void authSuccess(UserInfo userInfo);
    void authLoginFailed();
    void authKick();
    void updateWork(WorkInfo work);
    void updateTimetable(Timetable timetable);
    void motd(String motd);

}
