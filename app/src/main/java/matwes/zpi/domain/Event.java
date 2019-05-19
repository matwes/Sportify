package matwes.zpi.domain;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mateusz Wesołowski
 */
public class Event implements Comparable, Serializable {
    private String _id;
    private String id;
    private String name;
    private String image;
    private String date;
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

    public Event(String _id, String id, String name, String image, String date, String type,
                 String promoter, Price price, Place place, int interested, boolean isInterested,
                 boolean isNotInterested, boolean isCreator) {
        this._id = _id;
        this.id = id;
        this.name = name;
        this.image = image;
        this.date = date;
        this.type = type;
        this.promoter = promoter;
        this.price = price;
        this.place = place;
        this.interested = interested;
        this.isInterested = isInterested;
        this.isNotInterested = isNotInterested;
        this.isCreator = isCreator;
    }

    public Date getDateObject() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());

        if (date != null) {
            try {
                return df.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return new Date();
    }

    public String getDateWithTimeString() {
        Date dateObject = getDateObject();

        DateFormat df = new SimpleDateFormat("EEEE, d MMMM, HH:mm", java.util.Locale.getDefault());

        return df.format(dateObject);
    }

    public String getDateWithoutTimeString() {
        Date dateObject = getDateObject();

        DateFormat df = new SimpleDateFormat("EEEE, d MMMM", java.util.Locale.getDefault());

        return df.format(dateObject);
    }

    public String getFormDateString() {
        Date dateObject = getDateObject();

        DateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());

        return shortFormat.format(dateObject);
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
                ", type='" + type + '\'' +
                ", promoter='" + promoter + '\'' +
                ", price=" + price +
                ", place=" + place +
                ", interested=" + interested +
                ", isCreator='" + isCreator + '\'' +
                '}';
    }
}
