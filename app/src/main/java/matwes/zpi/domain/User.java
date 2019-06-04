package matwes.zpi.domain;

/**
 * Created by mateu on 19.04.2017.
 */

public class User {
    private String id;
    private String email;
    private String name;
    private String surname;
    private String sex;
    private String birthday;

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    public String getName() {
        return name == null ? "" : name;
    }

    public String getSurname() {
        return surname == null ? "" : surname;
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

    public String getUserName() {
        String userName = "";
        if (name != null)
            userName += name;
        if (surname != null)
            userName += (" " + surname);
        return userName;

    }

    public String getId() {
        return id;
    }
}
