package com.despreschen.mygoodaddresses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Restaurant> restaurantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restaurantList.add(new Restaurant("Trattoria dell'Angelo", "Italienne", 6, "avenue Rapp", "75007", "Paris", R.drawable.ic_restaurant_plate));
        RecyclerView recyclerView = findViewById(R.id.restaurant_list);
        RestaurantAdapter adapter = new RestaurantAdapter(this, restaurantList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(this, AddRestaurantActivity.class);
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }
}