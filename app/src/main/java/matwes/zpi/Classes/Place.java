package matwes.zpi.Classes;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mateu on 05.04.2017.
 */

public class Place
{
    private String googleId;
    private String name;
    private String city;
    private String address;
    private double latitude;
    private double longitude;

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return name + " - " + city;
    }

    public String getGoogleId() {
        return googleId;
    }

    public LatLng getLatLng()
    {
        return new LatLng(latitude, longitude);
    }
}
