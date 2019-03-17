package matwes.zpi.domain;

/**
 * Created by mateu on 19.04.2017.
 */

public class Sport {
    private int id;
    private String name;

    public Sport(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
