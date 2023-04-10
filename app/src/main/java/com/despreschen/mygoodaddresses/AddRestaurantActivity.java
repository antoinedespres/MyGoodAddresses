package com.despreschen.mygoodaddresses;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;

public class AddRestaurantActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int PERMISSIONS_REQUEST_LOCATION = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private double latitude, longitude;

    private ImageView mImageView;
    private Bitmap mImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        mImageView = findViewById(R.id.restaurant_image);
        mImageView.setOnClickListener(view -> {
            handlePictureTaking();
        });

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(view -> {
            saveRestaurant();
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ImageView useLocationIcon = findViewById(R.id.use_location_icon);
        useLocationIcon.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);
            } else {
                getLocation();
            }
        });

    }

    private void handlePictureTaking() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            mImageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(mImageBitmap);
        }
    }

    private void saveRestaurant() {
        String name = ((EditText) findViewById(R.id.restaurant_name)).getText().toString();
        String type = ((EditText) findViewById(R.id.restaurant_type)).getText().toString();
        String streetNumber = ((EditText) findViewById(R.id.restaurant_street_number)).getText().toString();
        String streetName = ((EditText) findViewById(R.id.restaurant_street_name)).getText().toString();
        String postCode = ((EditText) findViewById(R.id.restaurant_post_code)).getText().toString();
        String city = ((EditText) findViewById(R.id.restaurant_city)).getText().toString();

        // TODO save restaurant on database
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fillLocationFields(latitude, longitude);
                    }
                });
    }

    private void fillLocationFields(double latitude, double longitude) {
        TextView streetNumberTextView = findViewById(R.id.restaurant_street_number);
        TextView streetNameTextView = findViewById(R.id.restaurant_street_name);
        TextView postalCodeTextView = findViewById(R.id.restaurant_post_code);
        TextView cityTextView = findViewById(R.id.restaurant_city);

        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                streetNumberTextView.setText(address.getSubThoroughfare());
                streetNameTextView.setText(address.getThoroughfare());
                postalCodeTextView.setText(address.getPostalCode());
                cityTextView.setText(address.getLocality());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}