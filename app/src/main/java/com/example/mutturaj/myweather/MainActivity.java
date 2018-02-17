package com.example.mutturaj.myweather;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.mutturaj.myweather.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.WeakHashMap;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    final String WEATHER_URL="http://openweathermap.org/data/2.5/weather?lat=";
    final String WEATHER_API="&appid=b6907d289e10d714a6e88b30761fae22";

    private String TAG=MainActivity.class.getSimpleName();

    private ProgressDialog progressDialog;
    private String URL;

    LocationManager locationManager;
    double latti,longi;

    TextView editText2,editText1,editText3,editText4,editText5,
            editText6,editText7,editText8,editText9,editText10,ediText11,
            tvsummary;

    android.support.v7.widget.Toolbar toolbar;
    String datestr,outputStr,description,description2,description1,tempreture,wind,windspeed,
            humidity,winddirection,strHumidity,strpressure,weatherobj,mainobj,windy,Temptreture,
            pressure,desc,now,date,strsummary,strAddress,strWeather,mainstr;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDateAndTime();
        try {getPermission();} catch (IOException e) {}

        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

        //Toolbar

        toolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Weather details
        new GetWeather().execute();

        PlaceAutocompleteFragment placeAutocompleteFragment=(PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        placeAutocompleteFragment.setHint("Search your location");

        placeAutocompleteFragment.getView().forceLayout();

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG,"Places "+place.getName());
                Log.i(TAG,"Places "+place.getLatLng());

                LatLng queriedplace=place.getLatLng();

                Log.v("Latitude is ",""+queriedplace.latitude);
                Log.v("Longitude is ",""+queriedplace.longitude);

                latti=queriedplace.latitude;
                longi=queriedplace.longitude;

                try {
                    getAddress();
                } catch (IOException e) {

                }
                new GetWeather().execute();
                Toast.makeText(MainActivity.this, latti+"\n"+longi,Toast.LENGTH_SHORT).show();
                Log.i(TAG,"Places "+place.getName());


            }

            @Override
            public void onError(Status status) {
                String sstatus= String.valueOf(status);
                Log.i(TAG, "An error occurred: " + status);
                Toast.makeText(MainActivity.this, sstatus, Toast.LENGTH_SHORT).show();

            }
        });

        editText1=(TextView) findViewById(R.id.editText1);
        editText1.setText(strAddress);

        editText2=(TextView)findViewById(R.id.editText2);
        editText2.setText(strWeather);

        editText3=(TextView)findViewById(R.id.editText3);
        editText3.setText(strWeather);

        editText4=(TextView)findViewById(R.id.editText4);
        editText4.setText(strWeather);

        editText5=(TextView)findViewById(R.id.editText5);
        editText5.setText(strWeather);

        editText6=(TextView)findViewById(R.id.editText6);
        editText6.setText(strWeather);

        editText7=(TextView)findViewById(R.id.editText7);
        editText7.setText(strWeather);

        editText8=(TextView)findViewById(R.id.editText8);
        editText8.setText(strWeather);

        editText9=(TextView)findViewById(R.id.editText9);
        editText9.setText(strWeather);

        editText10=(TextView)findViewById(R.id.editText10);
        editText10.setText(strWeather);

        ediText11=(TextView)findViewById(R.id.tvdateandtime);
        ediText11.setText(date);

        tvsummary=(TextView)findViewById(R.id.editText100);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu) ;

    }
/*
    public boolean onOptionsItemSelected(MenuItem item){

        int id=item.getItemId();

        if (id==R.id.ad) {

            Toast.makeText(this,"Add selected",Toast.LENGTH_SHORT).show();

            return true;
        }else if (id==R.id.search){
            Toast.makeText(this, "Search for your location", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }*/

    public void getPermission() throws IOException {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();

        }
    }
    public void getLocation() throws IOException {
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED&&ActivityCompat.
                checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        }else{
            Location location1=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location2=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location3=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if(location1 != null){
                latti=location1.getLatitude();
                longi=location1.getLongitude();
                Toast.makeText(this,latti+"\n"+longi,Toast.LENGTH_SHORT).show();
                getAddress();
            }
            else if(location2 !=null){
                latti=location2.getLatitude();
                longi=location2.getLongitude();
                Toast.makeText(this,latti+"\n"+longi,Toast.LENGTH_SHORT).show();
                getAddress();

            }
            else if(location3 !=null){
                latti=location3.getLatitude();
                longi=location3.getLongitude();
                Toast.makeText(this,latti+"\n"+longi,Toast.LENGTH_SHORT).show();
                getAddress();

            }else {
                Toast.makeText(this,"Unable to find location",Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setMessage("Please TUR ON your GPS Connection").setCancelable(false).
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }

    private void getAddress() throws IOException {
        Geocoder geocoder=new Geocoder(this);
        if (geocoder.isPresent()){
            List<Address> list=geocoder.getFromLocation(latti,longi,1);
            Address address=list.get(0);
            StringBuffer stringBuffer=new StringBuffer();

            String subAdminArea=address.getSubAdminArea();
            String adminArea=address.getAdminArea();
            String locality=address.getLocality();

            if(subAdminArea==null){
                subAdminArea="";

            }if (adminArea==null){
                adminArea="";
            }if (locality==null){
                locality="";
            }
            stringBuffer.append(locality+"\n"+subAdminArea+" "+adminArea);
            strAddress=stringBuffer.toString();

        }else{
            strAddress="Unable to find location";
            Toast.makeText(MainActivity.this,strAddress,Toast.LENGTH_SHORT).show();
        }
    }

    private class GetWeather extends AsyncTask<Void,Void,Void >{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog=new
                    ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try{
                java.net.URL url=
                        new URL(WEATHER_URL
                                +MainActivity.this.latti+ "&lon="+MainActivity.this.longi+
                                WEATHER_API);

                HttpURLConnection httpURLConnection=
                        (HttpURLConnection) url.openConnection();

                try {
                    BufferedReader bufferedReader =
                            new BufferedReader(
                                    new InputStreamReader(httpURLConnection.getInputStream()));

                    StringBuilder stringBuilder = new StringBuilder();

                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");

                    }
                    bufferedReader.close();

                    outputStr = stringBuilder.toString();

                    JSONObject weatherMain=new JSONObject(outputStr);

                    //Main report get description from main
                    weatherobj = weatherMain.getString("weather");

                    //Getting Tempareture pressure
                    mainobj=weatherMain.getString("main");
                    JSONObject objTemp=new JSONObject(mainobj);

                    Temptreture=objTemp.getString("temp");
                    tempreture=Temptreture+"\u2103";

                    pressure=objTemp.getString("pressure");

                    strpressure=pressure+" \u33A9";
                    humidity=objTemp.getString("humidity");
                    strHumidity=humidity+"\u0025";

                    //getting wind speed and direction

                    windy=weatherMain.getString("wind");

                    JSONObject objWind=new JSONObject(windy);

                    windspeed=objWind.getString("speed");
                    winddirection=objWind.getString("deg");

                    wind=windspeed+" km/hr";

                    winddirection=winddirection +"\u00B0";

                    JSONArray jsonArray=(JSONArray)weatherMain.get("weather");

                    JSONObject jsonObject=(JSONObject)jsonArray.get(0);

                    strsummary=(String)jsonObject.get("description");

                    strsummary=strsummary.toUpperCase();

                } finally {httpURLConnection.disconnect();}


            }catch(Exception e){e.fillInStackTrace();}

            return null;

        }

        protected void onPostExecute(Void strWeather){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
                if(outputStr==null){
                    Log.i("INFO",outputStr);

                    editText2.setText("There was an error");
                }else {
                    Log.i("INFO",outputStr);

                    editText1.setText(strAddress);

                    tvsummary.setText(strsummary);

                    editText2.setText(tempreture);

                    editText3.setText("HUMIDITY");
                    editText4.setText(strHumidity);

                    editText5.setText("PRESSURE");
                    editText6.setText(strpressure);

                    editText7.setText("WIND SPEED");
                    editText8.setText(wind);

                    editText9.setText("WIND DIRECTION");
                    editText10.setText(winddirection);

                }

            }

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getDateAndTime(){

        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat format=new SimpleDateFormat("E dd:MM:yyyy hh:mm a");

        date=format.format(calendar.getTime());
    }
}
