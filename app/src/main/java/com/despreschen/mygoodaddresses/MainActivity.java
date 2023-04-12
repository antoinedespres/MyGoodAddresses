package com.despreschen.mygoodaddresses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MainActivity extends AppCompatActivity{

    private static List<Restaurant> restaurantList = new ArrayList<>();
    private static String PASS;

    @Override
    protected void onStart() {

        super.onStart();
        Toast.makeText(this, R.string.fetching_restaurants, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InputStream inputStream = getResources().openRawResource(R.raw.config);

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PASS = properties.getProperty("database_password");
        RestaurantAsyncTask task = new RestaurantAsyncTask();
        task.execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restaurantList.clear();

        RecyclerView recyclerView = findViewById(R.id.restaurant_list);
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

    public static void getRestaurantsFromDatabase() {
        restaurantList.clear();
        // Connect to database and retrieve data
        Connection conn = null;
        Statement stmt = null;
        try {

            Class.forName("org.postgresql.Driver");
            conn = java.sql.DriverManager.getConnection(DbCreds.DB_URL, DbCreds.USER, PASS);
            stmt = conn.createStatement();
            String sql = "SELECT * FROM " + DbCreds.TABLE_NAME;
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                String addressLine = rs.getString("addressLine");
                String postalCode = rs.getString("postalcode");
                String city = rs.getString("city");
                Restaurant restaurant = new Restaurant(id, name, type,  addressLine, postalCode, city);
                restaurantList.add(restaurant);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    private class RestaurantAsyncTask extends AsyncTask<Void, Void, List<Restaurant>> {

        @Override
        protected List<Restaurant> doInBackground(Void... voids) {
            getRestaurantsFromDatabase();
            return restaurantList;
        }

        @Override
        protected void onPostExecute(List<Restaurant> restaurantList) {
            if (restaurantList != null) {
                RecyclerView recyclerView = findViewById(R.id.restaurant_list);
                RestaurantAdapter adapter = new RestaurantAdapter(MainActivity.this, restaurantList, PASS);
                recyclerView.setAdapter(adapter);
            }
        }
    }
}