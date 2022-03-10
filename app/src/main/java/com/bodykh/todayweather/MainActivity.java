package com.bodykh.todayweather;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    TextView locationCurrent, temperatureCurrent, temperatureToday, temperatureTomorrow,
            temperatureAfTomorrow, conditionCurrent, conditionToday,
            conditionTomorrow, conditionAfTomorrow;
    ImageView imageCurrent, imageToday, imageTomorrow, imageAfTomorrow;
    private static final String TAG = "tag";
    String url, stringTemperatureCurrent, stringTemperatureTodayMax, stringTemperatureTodayMin, stringTemperatureTomorrowMax,
            stringTemperatureTomorrowMin, stringTemperatureAfTomorrowMax, stringTemperatureAfTomorrowMin, stringConditionCurrent,
            stringConditionToday, stringConditionTomorrow, stringConditionAfTomorrow,
            stringImageCurrent, stringImageToday, stringImageTomorrow, stringImageAfTomorrow;
    String tag_json_obj = "json_obj_req";
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationCurrent = findViewById(R.id.location);
        temperatureCurrent = findViewById(R.id.textTemp);
        imageCurrent = findViewById(R.id.imgTemp);
        conditionCurrent = findViewById(R.id.currentCondition);

        temperatureToday = findViewById(R.id.maxAndMin1);
        imageToday = findViewById(R.id.weatherPng1);
        conditionToday = findViewById(R.id.condition1);

        temperatureTomorrow = findViewById(R.id.maxAndMin2);
        imageTomorrow = findViewById(R.id.weatherPng2);
        conditionTomorrow = findViewById(R.id.condition2);

        temperatureAfTomorrow = findViewById(R.id.maxAndMin3);
        imageAfTomorrow = findViewById(R.id.weatherPng3);
        conditionAfTomorrow = findViewById(R.id.condition3);

        swipeRefreshLayout = findViewById(R.id.refreshLayout);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        overridePendingTransition(0, 0);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(MainActivity.this,
                            Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(),
                                location.getLongitude(),
                                1
                        );
                        //double lat= location.getLatitude();
                        //double log= location.getLongitude();
                        url = "http://api.weatherapi.com/v1/forecast.json?key=048a027ba95240bdacc185547220803&q=" +
                                location.getLatitude() + "," + location.getLongitude() + "&days=7";
                        locationCurrent.setText(addresses.get(0).getSubAdminArea() + ", " + addresses.get(0).getCountryName());
                        getWeatherFromApi(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void getWeatherFromApi(String url) {

        final ProgressDialog pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    // Temperatures
                    stringTemperatureCurrent = response.getJSONObject("current").get("temp_c").toString();

                    stringTemperatureTodayMax = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day").get("maxtemp_c").toString();
                    stringTemperatureTodayMin = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day").get("mintemp_c").toString();

                    stringTemperatureTomorrowMax = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(1).getJSONObject("day").get("maxtemp_c").toString();
                    stringTemperatureTomorrowMin = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(1).getJSONObject("day").get("mintemp_c").toString();


                    stringTemperatureAfTomorrowMax = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(2).getJSONObject("day").get("maxtemp_c").toString();
                    stringTemperatureAfTomorrowMin = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(2).getJSONObject("day").get("mintemp_c").toString();

                    // Conditions
                    stringConditionCurrent = response.getJSONObject("current").getJSONObject("condition").get("text").toString();

                    stringConditionToday = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day").getJSONObject("condition").get("text").toString();

                    stringConditionTomorrow = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(1).getJSONObject("day").getJSONObject("condition").get("text").toString();

                    stringConditionAfTomorrow = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(2).getJSONObject("day").getJSONObject("condition").get("text").toString();

                    // Images
                    stringImageCurrent = response.getJSONObject("current").getJSONObject("condition").get("icon").toString();
                    stringImageToday = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day").getJSONObject("condition").get("icon").toString();
                    stringImageTomorrow = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(1).getJSONObject("day").getJSONObject("condition").get("icon").toString();
                    stringImageAfTomorrow = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(2).getJSONObject("day").getJSONObject("condition").get("icon").toString();

                    //Setters
                    int tempCur = (int) Float.parseFloat(stringTemperatureCurrent);
                    temperatureCurrent.setText(tempCur + "°c");
                    conditionCurrent.setText(stringConditionCurrent);


                    int tempMaxToday = (int) Float.parseFloat(stringTemperatureTodayMax);
                    int tempMinToday = (int) Float.parseFloat(stringTemperatureTodayMin);
                    temperatureToday.setText(tempMaxToday + "°/" + tempMinToday + "°");
                    conditionToday.setText(stringConditionToday);

                    int tempMaxTom = (int) Float.parseFloat(stringTemperatureTomorrowMax);
                    int tempMinTom = (int) Float.parseFloat(stringTemperatureTomorrowMin);
                    temperatureTomorrow.setText(tempMaxTom + "°/" + tempMinTom + "°");
                    conditionTomorrow.setText(stringConditionTomorrow);

                    int tempMaxAfTom = (int) Float.parseFloat(stringTemperatureAfTomorrowMax);
                    int tempMinAfTom = (int) Float.parseFloat(stringTemperatureAfTomorrowMin);
                    temperatureAfTomorrow.setText(tempMaxAfTom + "°/" + tempMinAfTom + "°");
                    conditionAfTomorrow.setText(stringConditionAfTomorrow);


                    Picasso.get().load("http:"+ stringImageCurrent).into(imageCurrent);
                    Picasso.get().load("http:"+ stringImageToday).into(imageToday);
                    Picasso.get().load("http:"+ stringImageTomorrow).into(imageTomorrow);
                    Picasso.get().load("http:"+ stringImageAfTomorrow).into(imageAfTomorrow);


                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Error, Please Refresh !", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                pDialog.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Error, Please Check Your Connection !",
                        Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                pDialog.hide();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }
}