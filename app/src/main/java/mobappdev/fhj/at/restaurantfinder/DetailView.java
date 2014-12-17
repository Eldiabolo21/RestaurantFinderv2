package mobappdev.fhj.at.restaurantfinder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class DetailView extends Activity {

    FavouriteStorage favouriteStorage;
    Restaurant restaurant;
    Button setFavouriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        favouriteStorage = new FavouriteStorage(getBaseContext());

        TextView name, url, address, city;
        setFavouriteButton = (Button)findViewById(R.id.setFavourite);

        Bundle b = getIntent().getExtras();
        restaurant = (Restaurant)b.getSerializable("restaurant");

        if(favouriteStorage.isFavourite(restaurant)) {
            setFavouriteButton.setText("REMOVE");
        } else {
            setFavouriteButton.setText("ADD");
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

    public void setFavourite(View v) {
        if(favouriteStorage.isFavourite(restaurant)) {
            favouriteStorage.remove(restaurant);
            setFavouriteButton.setText("ADD");
        } else {
            favouriteStorage.add(restaurant);
            setFavouriteButton.setText("REMOVE");
        }
    }

}
