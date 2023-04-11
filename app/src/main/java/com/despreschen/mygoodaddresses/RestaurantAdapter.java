package com.despreschen.mygoodaddresses;

import android.content.Context;
import android.content.Intent;
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

    private static List<Restaurant> mRestaurantList;
    private static Context mContext;

    public RestaurantAdapter(Context ctx, List<Restaurant> restaurantList) {
        mRestaurantList = restaurantList;
        mContext = ctx;
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
        holder.restaurantAddressTextView.setText(restaurant.getNumber() + " " + restaurant.getStreet());
        holder.restaurantCityTextView.setText(restaurant.getPostalCode() + " " + restaurant.getCity());

        holder.itemView.setOnClickListener(v -> {
            // log the name of the restaurant
            Log.d("Restaurant", "Clicked restaurant: " + restaurant.getName());
            Intent i = new Intent(mContext, RestaurantLocationActivity.class);
            i.putExtra("name", restaurant.getName());
            i.putExtra("address",
                    restaurant.getNumber() + " " + restaurant.getStreet() + " " + restaurant.getPostalCode() + " " + restaurant.getCity());
            mContext.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {
        private TextView restaurantNameTextView;
        private TextView restaurantTypeTextView;
        private TextView restaurantAddressTextView;
        private TextView restaurantCityTextView;
        private ImageView deleteIconImageView;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantNameTextView = itemView.findViewById(R.id.restaurant_name);
            restaurantTypeTextView = itemView.findViewById(R.id.restaurant_type);
            restaurantAddressTextView = itemView.findViewById(R.id.restaurant_address);
            restaurantCityTextView = itemView.findViewById(R.id.restaurant_city);
            deleteIconImageView = itemView.findViewById(R.id.delete_icon);
            deleteIconImageView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mRestaurantList.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }
}

