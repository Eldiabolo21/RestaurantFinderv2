package mobcomp.hsb.de.restaurantfinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

// Verwaltung der Favoriten, die lokal in einer JSON Datei gespeichert werden

public class FavoriteStorage {

    private static final String MY_PREFS = "my_prefs";

    SharedPreferences prefs;
    SharedPreferences.Editor preferencesEditor;

    public FavoriteStorage(Context context) {
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
            preferencesEditor.apply();
        }
    }

    public boolean isFavorite(Restaurant r) {
        return prefs.contains(r.getId());
    }

    public ArrayList<Restaurant> getFavorites() {
        ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
        for(Map.Entry restaurant : prefs.getAll().entrySet()) {
            restaurants.add(new Restaurant(restaurant.getValue().toString()));
        }
        return restaurants;
    }
}
