package com.example.weatherforecast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    public WeatherAdapter(Context context, ArrayList<WeatherModel> weatherModelArrayList) {
        this.context = context;
        this.weatherModelArrayList = weatherModelArrayList;
    }

    private Context context;
    private ArrayList<WeatherModel> weatherModelArrayList;
    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {
        //cdn.weatherapi.com/weather/64x64/night/113.png
        WeatherModel weatherModel = weatherModelArrayList.get(position);
        holder.temperature2.setText(weatherModel.getTemperature().concat("°C"));
        Picasso.get().load("https:".concat(weatherModel.getIcon())).into(holder.conditionImg);
        holder.windSpeed.setText(weatherModel.getWindSpeed().concat("km/h"));
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
    try {
        Date t = input.parse(weatherModel.getTime());
        assert t != null;
        holder.time.setText(output.format(t));
    } catch (ParseException e){
        e.printStackTrace();
    }
    }

    @Override
    public int getItemCount() {
        return weatherModelArrayList.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView time,temperature2,windSpeed;
        private ImageView conditionImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            temperature2 = itemView.findViewById(R.id.temperature);
            windSpeed = itemView.findViewById(R.id.wind_speed);
            conditionImg = itemView.findViewById(R.id.condition_img);
        }
    }
}
