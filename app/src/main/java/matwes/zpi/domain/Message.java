package matwes.zpi.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by mateu on 19.04.2017.
 */

public class Message {
    private long id;
    private String message;
    private User author;
    private String time;

    public String getMessage() {
        return message;
    }

    public long getAuthorId() {
        return author.getId();
    }

    public static ArrayList<Message> jsonMessagesToList(String json) {
        Gson gson = new GsonBuilder().create();
        TypeToken<ArrayList<Message>> token = new TypeToken<ArrayList<Message>>() {
        };
        return gson.fromJson(json, token.getType());
    }

    public String getTime() {
        return time;
    }

    public String getAuthor() {
        return author.getName();
    }

}
