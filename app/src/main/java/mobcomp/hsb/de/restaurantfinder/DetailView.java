package mobcomp.hsb.de.restaurantfinder;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;


public class DetailView extends Activity {

    FavoriteStorage favoriteStorage;
    Restaurant restaurant;
    Button setFavoriteButton;

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
            //TODO set timer for 30 Days to as Reminder
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

        }
    }

    public void showRoute(View v) {
        Intent result = new Intent();
        setResult(1, result);
        finish();
    }
}
