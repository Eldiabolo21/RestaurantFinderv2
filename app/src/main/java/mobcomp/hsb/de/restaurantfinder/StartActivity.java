package mobcomp.hsb.de.restaurantfinder;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StartActivity extends Activity implements GoogleMap.OnMarkerClickListener, AsyncResponse, LocationListener {

    GoogleMap googleMap;
    Restaurant currentRestaurant;
    List<Restaurant> restaurantList, nearRestaurants;
    Map<String, Marker> markers;
    LocationManager locationManager;
    boolean refresh, favoritesActive;
    FavoriteStorage favoriteStorage;
    LatLng currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        restaurantList = new ArrayList<Restaurant>();
        nearRestaurants = new ArrayList<Restaurant>();
        markers = new HashMap<String, Marker>();
        refresh = true;
        favoritesActive = false;
        favoriteStorage = new FavoriteStorage(getBaseContext());

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

    //creates MapView
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

    // laedt die Restaurants von der foursquare Seite in die App mit Hilfe des FinderTask
    private void getRestaurants(LatLng latLng) {
        FinderTask request = new FinderTask();
        request.setDelegate(this);
        request.execute("https://api.foursquare.com/v2/venues/search?ll=" + latLng.latitude + "," + latLng.longitude + "&query=restaurant&oauth_token=NLHPJPLVI4UB2RZMEKV3D34NVLDHOIUUAUUTLR3UDKM1HQPM&v=20141203");
    }

    //draws Route bewtween current Position and selected restaurant
    public void drawRoute(LatLng position) {
        googleMap.clear();

        GMapV2Direction md = new GMapV2Direction();

        Document doc = md.getDocument(currentPosition, position, GMapV2Direction.MODE_DRIVING);

        ArrayList<LatLng> directionPoint = md.getDirection(doc);
        PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);

        for (int i = 0; i < directionPoint.size(); i++) {
            rectLine.add(directionPoint.get(i));
        }

        googleMap.addPolyline(rectLine);
        googleMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    //opens Detailview on Click ob Marker
    @Override
    public boolean onMarkerClick(final Marker marker) {
        for (Restaurant r : restaurantList) {
            if (markers.get(r.getId()).equals(marker)) {
                currentRestaurant = r;
                Intent i = new Intent(this, DetailView.class);
                Bundle b = new Bundle();
                b.putSerializable("restaurant", r);
                i.putExtras(b);
                startActivityForResult(i, 1);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            drawRoute(new LatLng(currentRestaurant.getLat(), currentRestaurant.getLng()));
        }
    }

    // zeigt alle favorisierten Restaurants auf der Map
    public void showFavorites(View view) {
        Button typeButton = (Button) findViewById(R.id.btnFav);
        if (favoritesActive) {
            restaurantList = nearRestaurants;
            typeButton.setText("F");
        } else {
            restaurantList = favoriteStorage.getFavorites();
            typeButton.setText("N");
        }
        drawMarkers();
        favoritesActive = !favoritesActive;
    }

    private void handleNotification() {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5000, pendingIntent);
    }

    // Methode zum Zeichnen der Markierungen
    public void drawMarkers() {
        googleMap.clear();
        markers.clear();
        for (Restaurant r : restaurantList) {
            addMarker(r);
        }
    }

    // Aktualisieren
    public void refresh(View view) {
        refresh = true;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
    }

    // Methode die fuer ein restaurant einen marker erstellt, mit den werten position, titel und icon
    private void addMarker(Restaurant restaurant) {
        if (googleMap != null) {
            markers.put(restaurant.getId(),
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(restaurant.getLat(), restaurant.getLng()))
                            .title(restaurant.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
            );
        } else {
        }
    }

    @Override
    public void processFinish(String output) {
        try {
            JSONObject response = new JSONObject(output);
            JSONArray jsonObjects = response.getJSONObject("response").getJSONArray("venues");
            for (int i = 0; i < jsonObjects.length(); i++) {
                JSONObject restaurantJSON = jsonObjects.getJSONObject(i);
                Restaurant restaurant = new Restaurant(restaurantJSON.toString());
                nearRestaurants.add(restaurant);
                restaurantList = nearRestaurants;
                favoritesActive = false;
                drawMarkers();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && refresh) {
            refresh = false;
            restaurantList = new ArrayList<Restaurant>();
            getRestaurants(new LatLng(location.getLatitude(), location.getLongitude()));
            currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(currentPosition, 12);
            googleMap.animateCamera(yourLocation);
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
