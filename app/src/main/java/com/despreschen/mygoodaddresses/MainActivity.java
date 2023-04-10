package com.despreschen.mygoodaddresses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private List<Restaurant> restaurantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restaurantList.add(new Restaurant("Trattoria dell'Angelo", "Italienne",
                "6", "avenue Rapp", "75007", "Paris", R.drawable.ic_restaurant_plate));
        restaurantList.add(new Restaurant("Au bon couscous", "Marocaine",
                "7", "rue Xavier Privas", "75005", "Paris", R.drawable.ic_restaurant_plate));
        RecyclerView recyclerView = findViewById(R.id.restaurant_list);
        RestaurantAdapter adapter = new RestaurantAdapter(this, restaurantList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("my_good_addresses", Context.MODE_PRIVATE);
        if (prefs.getBoolean("isFirstStart", true)) {
            showDialog();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstStart", false);
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add_restaurant_item) {
            Intent i = new Intent(this, AddRestaurantActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.welcome);
        builder.setMessage(R.string.app_description);
        builder.setPositiveButton(R.string.close, (dialog, which) -> { });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}