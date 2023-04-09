package com.despreschen.mygoodaddresses;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private List<Restaurant> mRestaurantList;

    public RestaurantAdapter(List<Restaurant> restaurantList) {
        mRestaurantList = restaurantList;
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
        holder.restaurantNameTextView.setText(restaurant.getName());
        holder.restaurantTypeTextView.setText(restaurant.getType());
        holder.restaurantAddressTextView.setText(restaurant.getStreet());
        holder.restaurantCityTextView.setText(restaurant.getCity());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // log the name of the restaurant
                Log.d("Restaurant", "Clicked restaurant: " + restaurant.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        private TextView restaurantNameTextView;
        private TextView restaurantTypeTextView;
        private TextView restaurantAddressTextView;
        private TextView restaurantCityTextView;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantNameTextView = itemView.findViewById(R.id.restaurant_name);
            restaurantTypeTextView = itemView.findViewById(R.id.restaurant_type);
            restaurantAddressTextView = itemView.findViewById(R.id.restaurant_address);
            restaurantCityTextView = itemView.findViewById(R.id.restaurant_city);
        }
    }
}

