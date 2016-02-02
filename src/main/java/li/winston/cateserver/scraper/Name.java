package li.winston.cateserver.scraper;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by winston on 19/01/2016.
 */
public class Name {

    public static Name fromFullname(String fullName) {
        String capitalised = WordUtils.capitalizeFully(fullName);
        List<String> names = new ArrayList<String>(Arrays.asList(capitalised.split(" ")));
        String lastName = names.remove(names.size() - 1);
        String firstName = StringUtils.join(names.toArray());
        return new Name(firstName, lastName);
    }

    String firstName;
    String lastName;

    Name(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
