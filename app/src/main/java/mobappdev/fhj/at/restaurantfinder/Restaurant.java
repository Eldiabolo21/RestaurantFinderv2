package mobappdev.fhj.at.restaurantfinder;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

/**
 * Created by hannesfeichtl on 12.12.14.
 */
public class Restaurant implements Serializable {

    String id;
    String name;
    String www;
    double lat;
    double lng;

    Restaurant(String id, String name, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.lng = lng;
        this.lat = lat;
    }

    public String getId() {
        return id;
    }

    public String getWww() {
        return www;
    }

    public void setWww(String www) {
        this.www = www;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLatLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
