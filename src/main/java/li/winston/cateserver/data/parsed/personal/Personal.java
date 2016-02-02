package li.winston.cateserver.data.parsed.personal;

import java.util.Objects;

/**
 * Created by winston on 17/01/2016.
 */
public class Personal {

    private final String picUrl;
    private final String firstName;
    private final String lastName;
    private final String login;
    private final String cid;
    private final String status;
    private final String department;
    private final String course;
    private final String email;
    private final String personalTutor;
    private final String personalTutorLogin;
    private final int period;

    public Personal(String picUrl,
                    String firstName,
                    String lastName,
                    String login,
                    String cid,
                    String status,
                    String department,
                    String course,
                    String email,
                    String personalTutor,
                    String personalTutorLogin,
                    int period) {
        this.picUrl = picUrl;
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.cid = cid;
        this.status = status;
        this.department = department;
        this.course = course;
        this.email = email;
        this.personalTutor = personalTutor;
        this.personalTutorLogin = personalTutorLogin;
        this.period = period;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLogin() {
        return login;
    }

    public String getCid() {
        return cid;
    }

    public String getStatus() {
        return status;
    }

    public String getDepartment() {
        return department;
    }

    public String getCourse() {
        return course;
    }

    public String getEmail() {
        return email;
    }

    public String getPersonalTutor() {
        return personalTutor;
    }

    public String getPersonalTutorLogin() {
        return personalTutorLogin;
    }

    public int getPeriod() {
        return period;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Personal)) {
            return false;
        }
        Personal that = (Personal) obj;
        return picUrl.equals(that.picUrl) &&
               firstName.equals(that.firstName) &&
               lastName.equals(that.lastName) &&
               login.equals(that.login) &&
               cid.equals(that.cid) &&
               status.equals(that.status) &&
               department.equals(that.department) &&
               course.equals(that.course) &&
               email.equals(that.email) &&
               personalTutor.equals(that.personalTutor) &&
               personalTutorLogin.equals(that.personalTutorLogin) &&
               period == that.period;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            picUrl,
            firstName,
            lastName,
            login,
            cid,
            status,
            department,
            course,
            email,
            personalTutor,
            personalTutorLogin,
            period
        );
    }

}
