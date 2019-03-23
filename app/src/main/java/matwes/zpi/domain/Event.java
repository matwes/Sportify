package matwes.zpi.domain;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mateu on 05.04.2017.
 */

public class Event implements Comparable {
    private long id;
    private String name;
    private User creator;
    private Sport sport;
    private int maxMembers;
    private String date;
    private Place place;
    private String description;
    private String time;
    private String status;
    private String eventImage;
    private ArrayList<Message> messages;
    private ArrayList<Member> members;
    private Boolean blocked;

    public String getEventImage() { return eventImage; }

    public Event(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public String getSportName() {
        return sport.getName();
    }

    public String getTime() {
        return time;
    }

    public Sport getSport() {
        return sport;
    }

    public int getSportId() {
        return sport.getId();
    }

    public String getMembersStatus() {
        return members.size() + "/" + maxMembers;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public int getCurrMembers() {
        return members.size();
    }

    public String getDateWithTimeString() {
        if (date != null && time != null)
            return date + " " + time;
        else if (date != null)
            return date;
        else
            return "";
    }

    public String getDateString() {
        return date;
    }

    public Date getDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        if (date != null)
            try {
                return df.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        return null;
    }

    public Date getDateWithTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);

        if (date != null && time != null)
            try {
                return df.parse(date + " " + time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        return null;
    }

    public Event(long id, String name, User creator, Sport sport, int maxMembers, String date,
                 Place place, String description, String time, String status,
                 ArrayList<Message> messages, ArrayList<Member> members) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.sport = sport;
        this.maxMembers = maxMembers;
        this.date = date;
        this.place = place;
        this.description = description;
        this.time = time;
        this.status = status;
        this.messages = messages;
        this.members = members;
        this.blocked = false;
    }

    public Place getPlace() {
        return place;
    }

    @Override
    public String toString() {
        return id + ". " + name;
    }


    public static ArrayList<Event> jsonEventsToList(String json) {
        Gson gson = new GsonBuilder().create();
        TypeToken<ArrayList<Event>> token = new TypeToken<ArrayList<Event>>() {
        };
        return gson.fromJson(json, token.getType());
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Member> getMembers() {
        return members;
    }

    public int getCreatorId() {
        return (int) creator.getId();
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Date d1 = this.getDate();
        Date d2 = ((Event) o).getDate();
        return d1.compareTo(d2);
    }
}
