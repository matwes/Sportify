package matwes.zpi.domain;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mateusz Wesołowski
 */
public class Event implements Comparable, Serializable {
    private String _id;
    private String id;
    private String name;
    private String image;
    private String date;
    private String time;
    private String type;
    private String promoter;
    private Price price;
    private Place place;
    private int interested;
    private boolean isInterested;
    private boolean isNotInterested;
    private boolean isCreator;

    public Event() {
    }

    public Event(String _id, String id, String name, String image, String date, String time,
                 String type, String promoter, Price price, Place place, int interested,
                 boolean isInterested, boolean isNotInterested, boolean isCreator) {
        this._id = _id;
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
        this.isInterested = isInterested;
        this.isNotInterested = isNotInterested;
        this.isCreator = isCreator;
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
                return df.parse(date.substring(0, 10));
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
        Date dateObject = getDateObject();

        DateFormat df = new SimpleDateFormat("EEEE, d MMMM, HH:mm", Locale.ENGLISH);

        return df.format(dateObject);
    }

    private String parseTime() {
        String[] s = time.split(":");
        return String.format("%s:%s", s[0], s[1]);
    }

    public void increaseInterested() {
        interested++;
    }

    public void decreaseInterested() {
        interested--;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
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

    public boolean isInterested() {
        return isInterested;
    }

    public void setInterested(int interested) {
        this.interested = interested;
    }

    public void setInterested(boolean interested) {
        isInterested = interested;
    }

    public boolean isCreator() {
        return isCreator;
    }

    public void setCreator(boolean creator) {
        isCreator = creator;
    }

    public boolean isNotInterested() {
        return isNotInterested;
    }

    public void setNotInterested(boolean notInterested) {
        isNotInterested = notInterested;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Date d1 = this.getDateObject();
        Date d2 = ((Event) o).getDateObject();
        return d1 == null ? -1 : d1.compareTo(d2);
    }

    @Override
    public String toString() {
        return "Event{" +
                "_id='" + _id + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", promoter='" + promoter + '\'' +
                ", price=" + price +
                ", place=" + place +
                ", interested=" + interested +
                ", isCreator='" + isCreator + '\'' +
                '}';
    }
}
