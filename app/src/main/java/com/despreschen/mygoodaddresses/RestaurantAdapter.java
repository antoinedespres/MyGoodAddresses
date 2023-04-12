package com.despreschen.mygoodaddresses;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private static List<Restaurant> mRestaurantList;
    private static Context mContext;
    private static String PASS;

    public RestaurantAdapter(Context ctx, List<Restaurant> restaurantList, String dbPassword) {
        mRestaurantList = restaurantList;
        mContext = ctx;
        PASS = dbPassword;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_card, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = mRestaurantList.get(position);
        holder.restaurantIdTextView.setText(String.valueOf(restaurant.getId()));
        holder.restaurantNameTextView.setText(restaurant.getName());
        holder.restaurantTypeTextView.setText(restaurant.getType());
        holder.restaurantAddressTextView.setText(restaurant.getAddressLine());
        holder.restaurantCityTextView.setText(restaurant.getPostalCode() + " " + restaurant.getCity());

        holder.itemView.setOnClickListener(v -> {
            // log the name of the restaurant
            Toast.makeText(mContext, R.string.loading_map, Toast.LENGTH_SHORT).show();
            Log.d("Restaurant", "Clicked restaurant: " + restaurant.getName());
            Intent i = new Intent(mContext, RestaurantLocationActivity.class);
            i.putExtra("name", restaurant.getName());
            i.putExtra("address",
                    restaurant.getAddressLine() + " " + restaurant.getPostalCode() + " " + restaurant.getCity());
            mContext.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {
        private TextView restaurantIdTextView;
        private TextView restaurantNameTextView;
        private TextView restaurantTypeTextView;
        private TextView restaurantAddressTextView;
        private TextView restaurantCityTextView;
        private ImageView deleteIconImageView;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantIdTextView = itemView.findViewById(R.id.restaurant_id);
            restaurantNameTextView = itemView.findViewById(R.id.restaurant_name);
            restaurantTypeTextView = itemView.findViewById(R.id.restaurant_type);
            restaurantAddressTextView = itemView.findViewById(R.id.restaurant_address);
            restaurantCityTextView = itemView.findViewById(R.id.restaurant_city);
            deleteIconImageView = itemView.findViewById(R.id.delete_icon);
            deleteIconImageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    RemoveRestaurantAsyncTask task = new RemoveRestaurantAsyncTask();
                    task.execute(Integer.parseInt(restaurantIdTextView.getText().toString()));
                    mRestaurantList.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }

    public void removeRestaurantFromDatabase(Integer id) {
        Connection conn;
        try {
            Class.forName("org.postgresql.Driver");
            conn = java.sql.DriverManager.getConnection(DbCreds.DB_URL, DbCreds.USER, PASS);

            // prepared statement to avoid SQL injection
            String sql = "DELETE FROM Restaurant WHERE Id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private class RemoveRestaurantAsyncTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... ids) {
            removeRestaurantFromDatabase(ids[0]);
            return null;
        }
    }
}

