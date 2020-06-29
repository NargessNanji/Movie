package com.example.movie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

public class PopularFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Film> mFilmList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_popular, container, false);
        recyclerView = mRootView.findViewById(R.id.recyclerView);
        progressBar = mRootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(new FilmAdapter(getContext(), mFilmList));
        search();
        return mRootView;
    }

    private class FilmAdapter extends RecyclerView.Adapter<FilmVH> {
        List<Film> filmList;
        Context context;

        public FilmAdapter(Context context, List<Film> filmList) {
            this.filmList = filmList;
            this.context = context;
        }

        @NonNull
        @Override
        public FilmVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FilmVH(LayoutInflater.from(context).inflate(R.layout.listitem, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull FilmVH holder, int position) {
            final Film film = filmList.get(position);
            holder.textViewTitle.setText(film.title);
            Glide.with(context).load("https://image.tmdb.org/t/p/w500/" + film.poster_path).into(holder.imageViewPoster);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(context, DetailsActivity.class);
//                    intent.putExtra("id", film.id);
//                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return filmList == null ? 0 : filmList.size();
        }
    }

    private class FilmVH extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        ImageView imageViewPoster;

        public FilmVH(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
        }
    }

    private void search() {
        final String url = "https://api.themoviedb.org/3/movie/popular?api_key=04f504439e16e5eec6e5f0ffc6b80e28&language=en-US";

        // prepare the Request
        RequestQueue queue = Volley.newRequestQueue(getContext()); // this = context
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        recyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        // display response
                        Log.d("Response: ", response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            List<Film> filmList = new ArrayList<>();
                            if (jsonArray != null)
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Film film = new Film(
                                            jsonObject.getLong("id"),
                                            jsonObject.getString("title"),
                                            jsonObject.getString("poster_path")
                                    );
                                    filmList.add(film);
                                }
                            PopularFragment.this.mFilmList = filmList;
                            recyclerView.setAdapter(new FilmAdapter(PopularFragment.this.getContext(), mFilmList));
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
