package matwes.zpi.domain;

import java.io.Serializable;

/**
 * Created by Mateusz Wesołowski
 */
public class Place implements Serializable {
    private String name;
    private String postalCode;
    private String address;
    private Location location;

    public Place(String name, String postalCode, String address, Location location) {
        this.name = name;
        this.postalCode = postalCode;
        this.address = address;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
