package matwes.zpi.Classes;

import java.util.ArrayList;

/**
 * Created by mateu on 19.04.2017.
 */

public class User
{
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private String sex;
    private String birthday;
    private String description;
    private String image;
    private String languageCode;

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName==null?"":firstName;
    }

    public String getLastName() {
        return lastName==null?"":lastName;
    }

    public String getSex() {
        return sex==null?"":sex;
    }

    public String getDescription() {
        return description==null?"":description;
    }

    public String getImage() {
        return image;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getBirthday() {return birthday==null?"":birthday;}

    public String getName() {
        String name = "";
        if(firstName!=null)
            name+=firstName;
        if(lastName!=null)
            name+=(" "+lastName);
        return name;

    }

    public long getId() {
        return id;
    }
}
