package com.despreschen.mygoodaddresses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private Context context;
    private List<Restaurant> restaurantList;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurant_card, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        holder.nameTextView.setText(restaurant.getName());
        holder.typeTextView.setText(restaurant.getType());
        holder.addressTextView.setText(restaurant.getNumber() + " " + restaurant.getStreet());
        holder.cityTextView.setText(restaurant.getPostalCode() + " " + restaurant.getCity());
        holder.imageView.setImageResource(restaurant.getImageResourceId());
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        TextView typeTextView;
        TextView addressTextView;
        TextView cityTextView;
        ImageView imageView;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.restaurant_name);
            typeTextView = itemView.findViewById(R.id.restaurant_type);
            addressTextView = itemView.findViewById(R.id.restaurant_address);
            cityTextView = itemView.findViewById(R.id.restaurant_city);
            imageView = itemView.findViewById(R.id.restaurant_image);
        }


    }
}

