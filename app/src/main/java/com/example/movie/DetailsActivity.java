package com.example.movie;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity {
    ProgressBar progressBar;
    ImageView imageViewPoster1;
    ImageView imageViewPoster2;
    TextView textViewTitle;
    TextView textViewOverview;
    TextView textViewReleaseDate;
    TextView textViewGenres;
    RatingBar ratingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        progressBar = findViewById(R.id.progressBar);
        imageViewPoster1 = findViewById(R.id.imageViewPoster1);
        imageViewPoster2 = findViewById(R.id.imageViewPoster2);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOverview = findViewById(R.id.textViewOverview);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewGenres = findViewById(R.id.textViewGenres);
        ratingBar = findViewById(R.id.ratingBar);
        getDetails();

    }

    private void getDetails() {
        final String url =
                "https://api.themoviedb.org/3/movie/" +
                        getIntent().getLongExtra("id", 0) +
                        "?api_key=04f504439e16e5eec6e5f0ffc6b80e28&language=en-US";

        // prepare the Request
        RequestQueue queue = Volley.newRequestQueue(this); // this = context
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        // display response
                        Log.d("Response: ", response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("genres");
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                stringBuilder.append(jsonObject.getString("name"));
                                stringBuilder.append(", ");
                            }
                            Glide.with(DetailsActivity.this).load("https://image.tmdb.org/t/p/w500/" + response.getString("backdrop_path")).into(imageViewPoster1);
                            Glide.with(DetailsActivity.this).load("https://image.tmdb.org/t/p/w500/" + response.getString("poster_path")).into(imageViewPoster2);

                            textViewGenres.setText("Genres: " + stringBuilder.toString());
                            textViewTitle.setText(response.getString("title"));
                            textViewReleaseDate.setText("Release date: " + response.getString("release_date"));
                            textViewOverview.setText(response.getString("overview"));
                            ratingBar.setRating((float) response.getDouble("vote_average"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // display error
                        progressBar.setVisibility(View.GONE);
                        Log.d("Error: ", error.getMessage());
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
    }
}
