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
import java.util.List;
import java.util.Locale;

/**
 * Created by Mateusz Wesołowski
 */
public class Event implements Comparable {
    private String id;
    private String name;
    private String image;
    private String date;
    private String time;
    private String type;
    private String promoter;
    private List<Price> price;
    private Place place;
    private int interested;
    private String creatorId;

    public Event(String id, String name, String image, String date, String time, String type,
                 String promoter, List<Price> price, Place place, int interested, String creatorId) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.date = date;
        this.time = time;
        this.type = type;
        this.promoter = promoter;
        this.price = price;
        this.place = place;
        this.interested = interested;
        this.creatorId = creatorId;
    }

    public static ArrayList<Event> jsonEventsToList(String json) {
        Gson gson = new GsonBuilder().create();
        TypeToken<ArrayList<Event>> token = new TypeToken<ArrayList<Event>>() {
        };
        return gson.fromJson(json, token.getType());
    }

    public Date getDateObject() {
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
                return df.parse(date + " " + parseTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        return null;
    }

    public String getDateWithTimeString() {
        return date != null && time != null ? date + " " + parseTime() : date != null ? date : "";
    }

    private String parseTime() {
        String[] s = time.split("S");
        return String.format("%s:%s", s[0], s[1]);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPromoter() {
        return promoter;
    }

    public void setPromoter(String promoter) {
        this.promoter = promoter;
    }

    public List<Price> getPrice() {
        return price;
    }

    public void setPrice(List<Price> price) {
        this.price = price;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public int getInterested() {
        return interested;
    }

    public void setInterested(int interested) {
        this.interested = interested;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Date d1 = this.getDateObject();
        Date d2 = ((Event) o).getDateObject();
        return d1.compareTo(d2);
    }
}
