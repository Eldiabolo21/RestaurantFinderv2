package mobappdev.fhj.at.restaurantfinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by hannesfeichtl on 12.12.14.
 */
public class Restaurant implements Serializable {

    String id;
    String name;
    String www;
    String address;
    String city;
    String postalCode;
    double lat;
    double lng;

    Restaurant(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            this.id = json.getString("id");
            this.name = json.getString("name");
            JSONObject location = json.getJSONObject("location");
            this.lng = location.getDouble("lng");
            this.lat = location.getDouble("lat");
            this.address = location.getString("address");
            this.postalCode = location.getString("postalCode");
            this.city =  location.getString("city");
            this.www = json.getString("url");
        } catch (JSONException e) {
        }
    }

    public String getCity() {
        return postalCode+" "+ city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String toJSON(){

        JSONObject restaurant = new JSONObject();
        JSONObject location = new JSONObject();
        try {
            restaurant.put("id", id);
            restaurant.put("name", name);
            location.put("lng", lng);
            location.put("lat", lat);
            location.put("postalCode", postalCode);
            location.put("address", address);
            location.put("city", city);
            restaurant.put("location", location);
            location.put("url", www);
            return restaurant.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

    }
}
