package matwes.zpi.domain;

/**
 * Created by Mateusz Wesołowski
 */
public class Place {
    private String name;
    private String postalCode;
    private String address;
    private String country;
    private String city;
    private Location location;

    public Place(String name, String postalCode, String address, String country, String city, Location location) {
        this.name = name;
        this.postalCode = postalCode;
        this.address = address;
        this.country = country;
        this.city = city;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
