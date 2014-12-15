package mobappdev.fhj.at.restaurantfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class DetailView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        Bundle b = getIntent().getExtras();
        Restaurant restaurant = (Restaurant)b.getSerializable("restaurant");
        //Log.v("xxx", restaurant.getName());
    }
}
