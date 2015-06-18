package mobcomp.hsb.de.restaurantfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


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
        restaurant = (Restaurant)b.getSerializable("restaurant");

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
        }
    }

    public void showRoute(View v) {
        Intent result = new Intent();
        setResult(1, result);
        finish();
    }
}
