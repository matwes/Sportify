package matwes.zpi.domain;

/**
 * Created by mateu on 19.04.2017.
 */

public class User {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String sex;
    private String birthday;

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    private String description;
    private String image;
    private String languageCode;

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName == null ? "" : firstName;
    }

    public String getLastName() {
        return lastName == null ? "" : lastName;
    }

    public String getSex() {
        return sex == null ? "" : sex;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public String getImage() {
        return image;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getBirthday() {
        return birthday == null ? "" : birthday;
    }

    public String getName() {
        String name = "";
        if (firstName != null)
            name += firstName;
        if (lastName != null)
            name += (" " + lastName);
        return name;

    }

    public String getId() {
        return id;
    }
}
