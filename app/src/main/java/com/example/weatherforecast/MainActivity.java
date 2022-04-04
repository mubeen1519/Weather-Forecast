package com.example.weatherforecast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout relativeLayout;
    private TextView cityName, temperatureTxt,conditionTxt;
    private TextInputEditText inputText;
    private ProgressBar LoadingPb;
    private ImageView backgroundImg,TemperatureIcon,searchIcon;
    private RecyclerView recyclerView;
    private ArrayList<WeatherModel> weatherModelArrayList;
    private WeatherAdapter weatherAdapter;
    private LocationManager locationManager;
    public static int PERMISSION_CODE = 1;
    private String citySs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
    //Relativelayout
        relativeLayout = findViewById(R.id.Relative1);
        //TextView
        cityName = findViewById(R.id.cityNameTxt);
        temperatureTxt = findViewById(R.id.temperature_txt);
        conditionTxt = findViewById(R.id.weather_condition);
        //edit text
        inputText = findViewById(R.id.inputTxt);
        //progressBar
        LoadingPb = findViewById(R.id.progress_bar);
        //imageView
        backgroundImg = findViewById(R.id.BackgroundImg);
        TemperatureIcon = findViewById(R.id.temperature_icon);
        searchIcon = findViewById(R.id.searchImg);
        //RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
    weatherModelArrayList = new ArrayList<>();
    weatherAdapter = new WeatherAdapter(this,weatherModelArrayList);
    recyclerView.setAdapter(weatherAdapter);

    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
    }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location != null) {
            citySs = getCityName(location.getLongitude(), location.getLatitude());
        } else {
            citySs = "Rahim yar khan";
        }
        weatherInfo(citySs);
        searchIcon.setOnClickListener(view -> {
            String city1 =inputText.getText().toString();
            if(city1.isEmpty()){
                Toast.makeText(MainActivity.this,"Please enter city Name", Toast.LENGTH_SHORT).show();
            }else {
                cityName.setText(citySs);
                weatherInfo(city1);
            }
        });
    }


    public void weatherInfo(String cityNameUrl){
        String url = "https://api.weatherapi.com/v1/forecast.json?key=b2910d9d2b96491fa9a212204220304&q=Rahim%20yar%20Khan&days=1&aqi=no&alerts=no";
        cityName.setText(cityNameUrl);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        @SuppressLint("NotifyDataSetChanged") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
        LoadingPb.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.VISIBLE);
        weatherModelArrayList.clear();

        try {
            String tempreatureQue = response.getJSONObject("current").getString("temp_c");
            temperatureTxt.setText(tempreatureQue.concat("°C"));
            int isDay = response.getJSONObject("current").getInt("is_day");
            String conditionObject = response.getJSONObject("current").getJSONObject("condition").getString("text");
            String conditionObjectIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
            Picasso.get().load("https:".concat(conditionObjectIcon)).into(TemperatureIcon);
            conditionTxt.setText(conditionObject);
            if(isDay == 1){
                Picasso.get().load("https://cdn.pixabay.com/photo/2022/02/28/15/28/sea-7039471_960_720.jpg").into(backgroundImg);
            } else {
                Picasso.get().load("https://images.pexels.com/photos/8246465/pexels-photo-8246465.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1").into(backgroundImg);
            }

            JSONObject forecastObj = response.getJSONObject("forecast");
            JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
            JSONArray hourArray =  forecast0.getJSONArray("hour");
            for(int i = 0; i<hourArray.length(); i++){
                JSONObject hourObj = hourArray.getJSONObject(i);
                String timeJason = hourObj.getString("time");
                String tempC = hourObj.getString("temp_c");
                String imgJason = hourObj.getJSONObject("condition").getString("icon");
                String wind = hourObj.getString("wind_kph");
                weatherModelArrayList.add(new WeatherModel(timeJason,tempC,imgJason,wind));
            }
            weatherAdapter.notifyDataSetChanged();

        } catch (JSONException e){
            e.printStackTrace();
        }
        }, error -> Toast.makeText(MainActivity.this,"Enter valid city Name",Toast.LENGTH_SHORT).show());
        requestQueue.add(jsonObjectRequest);
    }

    private String getCityName(double longitude, double latitude){
        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(longitude,latitude,10);
            for(Address adr : addresses){
                if(adr != null){
                    String city = adr.getLocality();
                    if(city != null && !city.equals("")){
                        cityName = city;
                    } else{
                        Log.d("TAG","City not Found");
                        Toast.makeText(this,"User city not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this,"Please Allow Permission",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}