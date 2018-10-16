package activity;

import app.AppConfig;
import app.LandMarks;
import helper.SQLiteHandler;
import helper.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nahla.egytour.MapsActivity;
import com.example.nahla.egytour.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.nahla.egytour.MapsActivity.MY_PERMISSIONS_REQUEST_LOCATION;

public class MainActivity extends Activity  {
    private TextView txtName;
    private Button btnLogout;
    private Button btnLocation;
    private Spinner dropdown;
    private ListView nearby;
    private SQLiteHandler db;
    private SessionManager session;
    private Double lat;
    private Double lon;
    private LocationListener listener;
    private ArrayList<LandMarks> lm;
    private ArrayAdapter adapter;
    private ArrayList<String> land;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dropdown = findViewById(R.id.spinner);
        txtName = (TextView) findViewById(R.id.name);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLocation = (Button) findViewById(R.id.location);
        nearby = (ListView) findViewById(R.id.nearby);
        nearby.setAdapter(adapter);
        lm = new ArrayList<LandMarks>();

        nearby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedstring = land.get(position);
                String[] splitted = selectedstring.split(",");
                Double selectedlat = 0.0;
                Double selectedlon = 0.0;
                Log.i("selectedname",splitted[0]);
                for(int i = 0; i<lm.size();i++){
                    if(lm.get(i).getName().equalsIgnoreCase(splitted[0])){
                        Log.i("nameeee",lm.get(i).getName());
                        selectedlat = lm.get(i).getLat();
                        selectedlon = lm.get(i).getLon();
                        break;
                    }
                }
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
           //     Bundle mBundle = new Bundle();
                Log.i("selectedlatmain",selectedlat+"");
                intent.putExtra("selectedlat", selectedlat+ "");
                intent.putExtra("selectedlon", selectedlon+ "");
                startActivity(intent);
                finish();
            }
        });

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String s = location.getLatitude() + " " + location.getLongitude();
                lat = location.getLatitude();
                lon = location.getLongitude();
                Log.i("locaaation", s);
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
        else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locationManager.requestLocationUpdates("gps", 5000, 0, listener);
            lat = location.getLatitude();
            lon = location.getLongitude();
            loadList();
        }

        String[] items = new String[]{"Sort by Distance", "Sort by Rating"};
        ArrayAdapter<String> adapteritems = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        adapteritems.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapteritems);
        dropdown.setPrompt("Sort");
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        land = sortByDistance();
                        adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, land);
                        nearby.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        break;

                    case 1:
                        land = sortByRate();
                        adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, land);
                        nearby.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");

        // Displaying the user details on the screen
        txtName.setText("Welcome, " + name + "!");

        // Logout button click event
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void loadList() {
        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_RETRIEVE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {
                        try {
                            JSONArray ja = new JSONArray(jsonData);

                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject jo = ja.getJSONObject(i);

                                String name = jo.getString("Name");
                                Double Latitude = jo.getDouble("Latitude");
                                Double Longitude = jo.getDouble("Longitude");
                                Double Rating = jo.getDouble("Rating");
                               // String imgurl = jo.getString("Image Path");

                                Location startPoint = new Location("locationA");

                                if (lat != null && lon != null) {
                                    startPoint.setLatitude(lat);
                                    startPoint.setLongitude(lon);
                                }

                                Location endPoint = new Location("locationA");
                                endPoint.setLatitude(Latitude);
                                endPoint.setLongitude(Longitude);

                                Log.i("current location", lat + "" + lon);

                                double distance = startPoint.distanceTo(endPoint);
                                LandMarks lo = new LandMarks(name, Rating, distance,Latitude,Longitude);
                                lm.add(lo);
                              //  stringlist.add(name + " , " + Rating + " , " + String.format("%.2f", distance / 1000) + " km");

                            }
                            land = sortByDistance();
                            adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, land);
                            nearby.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public ArrayList<String> sortByRate() {
        ArrayList<String> sorted = new ArrayList<String>();
        Collections.sort(lm, new Comparator<LandMarks>() {
            public int compare(LandMarks p1, LandMarks p2) {
                if (p1.getRating() > p2.getRating()) return -1;
                if (p1.getRating() < p2.getRating()) return 1;
                return 0;
            }
        });
        for (int i = 0; i < lm.size(); i++) {
            LandMarks l = lm.get(i);
            String s = l.getName() + "," + l.getRating() + "," + String.format("%.2f", l.getDistance() / 1000) + " km";
            sorted.add(s);
        }
        return sorted;
    }

    public ArrayList<String> sortByDistance() {
        ArrayList<String> sorted = new ArrayList<String>();
        Collections.sort(lm, new Comparator<LandMarks>() {
            public int compare(LandMarks p1, LandMarks p2) {
                if (p1.getDistance() < p2.getDistance()) return -1;
                if (p1.getDistance() > p2.getDistance()) return 1;
                return 0;
            }
        });
        for (int i = 0; i < lm.size(); i++) {
            LandMarks l = lm.get(i);
            String s = l.getName() + "," + l.getRating() + "," + String.format("%.2f", l.getDistance() / 1000) + " km";
            sorted.add(s);
        }
        return sorted;
    }
}