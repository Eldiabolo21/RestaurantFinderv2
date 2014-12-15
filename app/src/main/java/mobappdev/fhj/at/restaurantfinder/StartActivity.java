package mobappdev.fhj.at.restaurantfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StartActivity extends Activity implements GoogleMap.OnMarkerClickListener, View.OnClickListener, AsyncResponse, LocationListener {

    GoogleMap googleMap;
    List<Restaurant> restaurantList;
    Map<String, Marker> markers;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        restaurantList = new ArrayList<Restaurant>();
        markers = new HashMap<String, Marker>();

        // Enables Internet tasks in the main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_start);
        createMapView();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        if (location != null)
            onLocationChanged(location);
        else
            Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();

    }

    /*

     */

    private void createMapView() {
        try {
            if (null == googleMap) {
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();
                googleMap.setMyLocationEnabled(true);
                googleMap.setOnMarkerClickListener(this);

                if (null == googleMap) {
                    Toast.makeText(getApplicationContext(), "Error creating map", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NullPointerException exception) {
            Log.e("mapApp", exception.toString());
        }
    }

    private void getRestaurants(LatLng latLng) {
        FinderTask request = new FinderTask();
        request.setDelegate(this);
        request.execute("https://api.foursquare.com/v2/venues/search?ll=" + latLng.latitude + "," + latLng.longitude + "&query=restaurant&oauth_token=NLHPJPLVI4UB2RZMEKV3D34NVLDHOIUUAUUTLR3UDKM1HQPM&v=20141203");
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        for (Restaurant r : restaurantList) {
            if (markers.containsKey(r.getId())) {
                Intent i = new Intent(this, DetailView.class);
                Bundle b = new Bundle();
                b.putSerializable("restaurant", r);
                i.putExtras(b);
                startActivity(i);
                return true;
            }
        }


        return false;
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(this, Favorites.class);
        startActivity(i);
    }

    @Override
    public void processFinish(String output) {
        try {
            JSONObject response = new JSONObject(output);
            JSONArray jsonObjects = response.getJSONObject("response").getJSONArray("venues");
            for (int i = 0; i < jsonObjects.length(); i++) {
                JSONObject restaurantJSON = jsonObjects.getJSONObject(i);
                Restaurant restaurant = new Restaurant(restaurantJSON.getString("id"), restaurantJSON.getString("name"), restaurantJSON.getJSONObject("location").getDouble("lat"), restaurantJSON.getJSONObject("location").getDouble("lng"));
                restaurantList.add(restaurant);
                addMarker(restaurant);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addMarker(Restaurant restaurant) {
        if (null != googleMap) {
            markers.put(restaurant.getId(),
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(restaurant.getLat(), restaurant.getLng()))
                            .title(restaurant.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
            );

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            getRestaurants(new LatLng(location.getLatitude(), location.getLongitude()));
            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(myLocation, 12);
            googleMap.animateCamera(yourLocation);
        } else {
            Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
