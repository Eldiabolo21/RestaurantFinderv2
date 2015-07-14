package mobcomp.hsb.de.restaurantfinder;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;


public class DetailView extends Activity {

    FavoriteStorage favoriteStorage;
    Restaurant restaurant;
    Button setFavoriteButton;

    private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in Milliseconds
    private static final long POINT_RADIUS = 100; // in Meters
    private static final long PROX_ALERT_EXPIRATION = -1; // It will never expire
    private static final String PROX_ALERT_INTENT = "com.androidmyway.demo.ProximityAlert";
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        favoriteStorage = new FavoriteStorage(getBaseContext());

        TextView name, url, address, city;
        setFavoriteButton = (Button)findViewById(R.id.setFavorite);

        Bundle b = getIntent().getExtras();
        if (b == null) {
            restaurant = (Restaurant) savedInstanceState.getSerializable("restaurant");
        } else{
                restaurant = (Restaurant) b.getSerializable("restaurant");
        }

        if(favoriteStorage.isFavorite(restaurant)) {
            setFavoriteButton.setText(getString(R.string.remFav));
        } else {
            setFavoriteButton.setText(getString(R.string.addFav));
        }

        name = (TextView)findViewById(R.id.nameText);
        url = (TextView)findViewById(R.id.urlText);
        address = (TextView)findViewById(R.id.addressText);
        city = (TextView)findViewById(R.id.cityText);

        name.setText(restaurant.getName());
        url.setText(restaurant.getWww());
        address.setText(restaurant.getAddress());
        city.setText(restaurant.getCity());
    }

    public void setFavorite(View v) {
        if(favoriteStorage.isFavorite(restaurant)) {
            favoriteStorage.remove(restaurant);
            setFavoriteButton.setText(getString(R.string.addFav));
        } else {
            favoriteStorage.add(restaurant);
            setFavoriteButton.setText(getString(R.string.remFav));
            Log.d("Favorite", "Favorite");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            Intent activate = new Intent(this, AlarmReceiver.class);
            Bundle b = new Bundle();
            b.putSerializable("restaurant", restaurant);
            activate.putExtras(b);
            activate.setAction("restaurant");
            activate.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //activate.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            AlarmManager alarms ;
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, activate, 0);
            //resultIntent.setFlags(Intent.;
            alarms = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarms.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + 10000, alarmIntent);
            addProximityAlert();
        }
    }
            //Set Proximity alert
            private void addProximityAlert() {
                double latitude = restaurant.getLat();
                double longitude = restaurant.getLng();
                Intent intent = new Intent(this, ProxReciever.class);
                PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
                locationManager.addProximityAlert(
                        latitude, // the latitude of the central point of the alert region
                        longitude, // the longitude of the central point of the alert region
                        POINT_RADIUS, // the radius of the central point of the alert region, in meters
                        PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no                           expiration
                        proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
                );

                IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
                registerReceiver(new ProxReciever(), filter);
                //TODO Remoce debug!
                Toast.makeText(getApplicationContext(), "Alert Added", Toast.LENGTH_SHORT).show();
            }



    public void showRoute(View v) {
        Intent result = new Intent();
        setResult(1, result);
        finish();
    }
}
