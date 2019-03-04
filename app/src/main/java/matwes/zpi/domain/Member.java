package matwes.zpi.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by mateu on 19.04.2017.
 */

public class Member {
    private long id;
    private String status;
    private User user;

    public long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }

    public static ArrayList<Member> jsonMembersToList(String json) {
        Gson gson = new GsonBuilder().create();
        TypeToken<ArrayList<Member>> token = new TypeToken<ArrayList<Member>>() {
        };
        return gson.fromJson(json, token.getType());
    }
}
