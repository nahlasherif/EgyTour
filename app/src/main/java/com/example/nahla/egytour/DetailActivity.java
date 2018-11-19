package com.example.nahla.egytour;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import activity.LoginActivity;
import activity.MainActivity;
import activity.RegisterActivity;
import app.AppConfig;
import app.AppController;

import static android.text.TextUtils.split;

public class DetailActivity extends AppCompatActivity {
    ImageView Logo;
    TextView Info;
    TextView rateN;
    TextView Placename;
    Button Call;
    Button Map;
    EditText Review;
    RatingBar RatingBar;
    Button SaveReview;
    Intent i;
    String name;
    float myrating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Placename = findViewById(R.id.placename);
        Logo = findViewById(R.id.logo);
        Info = findViewById(R.id.info);
        Call = findViewById(R.id.call);
        Map = findViewById(R.id.map);
        Review = findViewById(R.id.review);
        rateN = findViewById(R.id.ratenum);
        RatingBar = findViewById(R.id.ratingBar2);
        SaveReview = findViewById(R.id.save);

        i = getIntent();
        name = i.getStringExtra("Name");
        final String PhoneNumber = i.getStringExtra("Phone");
        String path = i.getStringExtra("Logo");
        String Distance = i.getStringExtra("Distance");
        String Rating = i.getStringExtra("Rating");
        String Reviews = i.getStringExtra("Reviews");

        Picasso.with(DetailActivity.this).load(path).resize(100, 100).into(Logo);
        if(Reviews.equals(""))
        {
            Reviews = " No previous Reviews" ;
        }
        String[] ReviewArray = split(Reviews,",");
        Placename.setText(name);
        Info.setText("PhoneNumber: " + PhoneNumber + '\n' + "Distance: " + Distance + '\n' +"Rating: " + Rating + '\n' + "Reviews: " );

        for(int i=0; i<ReviewArray.length;i++)
        Info.append(ReviewArray[i] + '\n');

        Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + PhoneNumber)));
            }
        });

        Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, MapsActivity.class);
                intent.putExtra("selectedlat", i.getStringExtra("selectedlat") + "");
                intent.putExtra("selectedlon", i.getStringExtra("selectedlon") + "");
                startActivity(intent);
                finish();
            }
        });
        RatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(android.widget.RatingBar ratingBar, float rating, boolean fromUser) {
                myrating=   RatingBar.getRating();
                rateN.setText(myrating+"");
            }
        });



        SaveReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Review.getText()!=null){
                    final String review = Review.getText().toString().trim();

                    StringRequest strReq = new StringRequest(Request.Method.POST,
                            AppConfig.URL_ADD,
                            new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jObj = new JSONObject(response);
                                boolean error = jObj.getBoolean("error");
                                if (!error) {
                                    Toast.makeText(getApplicationContext(),
                                            "Stored successfully", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    String errorMsg = jObj.getString("error_msg");
                                    Toast.makeText(getApplicationContext(),
                                            errorMsg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error", "Registration Error: " + error.getMessage());
                            Toast.makeText(getApplicationContext(),
                                    error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected java.util.Map<String, String> getParams() {
                            // Posting params to register url
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("review", review);
                            params.put("name",name);
                            params.put("myrating",myrating+"");
                            return params;
                        }
                    };
                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(strReq);
                }
            }
        });
    }

}
