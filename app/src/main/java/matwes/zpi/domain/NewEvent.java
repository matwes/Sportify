package matwes.zpi.domain;

/**
 * Created by Mateusz Wesołowski
 */
public class NewEvent {
    private String _id;
    private String name;
    private String image;
    private String date;
    private String type;
    private String promoter;
    private Price price;
    private Place place;

    public NewEvent(String eventId) {
        this._id = eventId;
    }

    public NewEvent(String _id, String name, String image, String date, String type, String promoter, Price price, Place place) {
        this._id = _id;
        this.name = name;
        this.image = image;
        this.date = date;
        this.type = type;
        this.promoter = promoter;
        this.price = price;
        this.place = place;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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
}
