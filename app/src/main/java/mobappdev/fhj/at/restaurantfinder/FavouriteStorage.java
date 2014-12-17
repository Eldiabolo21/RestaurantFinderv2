package mobappdev.fhj.at.restaurantfinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hannesfeichtl on 16.12.14.
 */
public class FavouriteStorage {

    private static final String MY_PREFS = "my_prefs";

    SharedPreferences prefs;
    SharedPreferences.Editor preferencesEditor;

    public FavouriteStorage(Context context) {
        prefs = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        preferencesEditor = prefs.edit();
    }

    public void add(Restaurant r) {
        preferencesEditor.putString(r.getId(), r.toJSON());
        preferencesEditor.apply();
    }

    public void remove(Restaurant r) {
        if(prefs.contains(r.getId())) {
            preferencesEditor.remove(r.getId());
        }
    }

    public boolean isFavourite(Restaurant r) {
        return prefs.contains(r.getId());
    }

    public ArrayList<Restaurant> getFavourites() {
        ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
        for(Map.Entry restaurant : prefs.getAll().entrySet()) {
            restaurants.add(new Restaurant(restaurant.getValue().toString()));
        }
        return restaurants;
    }
}
