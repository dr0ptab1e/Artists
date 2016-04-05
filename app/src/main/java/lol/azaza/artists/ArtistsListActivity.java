package lol.azaza.artists;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ArtistsListActivity extends AppCompatActivity {

    RecyclerView artistsList;
    RecyclerView.Adapter adapter;
    List<Artist> artists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        artistsList = (RecyclerView) findViewById(R.id.artists_list);
        adapter = new RecyclerView.Adapter<Holder>() {

            @Override
            public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(ArtistsListActivity.this).inflate(R.layout.artist_list_item, parent, false);
                return new Holder(v);
            }

            @Override
            public void onBindViewHolder(Holder holder, final int position) {
                holder.name.setText(artists.get(position).getName());
                holder.genres.setText(artists.get(position).getGenres());
                holder.info.setText(artists.get(position).getInfo());
                Glide.with(ArtistsListActivity.this).load(artists.get(position).getCoverSmall()).into(holder.cover);
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ArtistsListActivity.this, ArtistDetailActivity.class).putExtra("artist", artists.get(position));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return artists.size();
            }
        };
        artistsList.setAdapter(adapter);
        artistsList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artists_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            new AsyncTask<Void, Void, List<Artist>>() {

                @Override
                protected List<Artist> doInBackground(Void... params) {
                    List<Artist> artists = new ArrayList<>();
                    try {
                        URL url = new URL("http://download.cdn.yandex.net/mobilization-2016/artists.json");
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        String jsonString = IOUtils.toString(in);
                        JSONArray array = new JSONArray(jsonString);
                        for (int i = 0; i < array.length(); ++i) {
                            JSONObject object = array.getJSONObject(i);
                            long id = object.getInt("id");
                            String name = object.getString("name");
                            JSONArray jsonGenres = object.optJSONArray("genres");
                            List<String> genres = new ArrayList<>();
                            if (jsonGenres != null) {
                                for (int j = 0; j < jsonGenres.length(); ++j) {
                                    genres.add(jsonGenres.getString(j));
                                }
                            }
                            int tracks = object.optInt("tracks");
                            int albums = object.optInt("albums");
                            String link = object.optString("link");
                            String description = object.optString("description");
                            JSONObject jsonCover = object.optJSONObject("cover");
                            String coverBig = null;
                            String coverSmall = null;
                            if (jsonCover != null) {
                                coverBig = jsonCover.getString("big");
                                coverSmall = jsonCover.getString("small");
                            }
                            artists.add(new Artist(id, name, genres, tracks, albums, link, description, coverBig, coverSmall));
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    return artists;
                }

                @Override
                protected void onPostExecute(List<Artist> result) {
                    artists = result;
                    adapter.notifyDataSetChanged();
                }
            }.execute();
        }

        return super.onOptionsItemSelected(item);
    }
}
