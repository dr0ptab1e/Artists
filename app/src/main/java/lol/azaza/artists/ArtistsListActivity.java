package lol.azaza.artists;

import android.content.Intent;
import android.database.Cursor;
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

    private CursorRecyclerAdapter<Holder> adapter;
    public static final String ARTIST = "lol.azaza.artists.artist";
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);
        setContentView(R.layout.activity_artists_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView artistsList = (RecyclerView) findViewById(R.id.artists_list);
        adapter = new CursorRecyclerAdapter<Holder>(dbHelper.getAllArtistsCursor()) {
            @Override
            public void onBindViewHolderCursor(Holder holder, Cursor cursor) {
                final Artist artist = dbHelper.getArtist(cursor);
                holder.name.setText(artist.getName());
                holder.genres.setText(artist.getGenres());
                holder.info.setText(artist.getInfo());
                Glide.with(ArtistsListActivity.this).load(artist.getCoverSmall()).into(holder.cover);
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ArtistsListActivity.this, ArtistDetailActivity.class).putExtra(ARTIST, artist);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(ArtistsListActivity.this).inflate(R.layout.artist_list_item, parent, false);
                return new Holder(v);
            }
        };
        artistsList.setAdapter(adapter);
        artistsList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.changeCursor(dbHelper.getAllArtistsCursor());
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
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    dbHelper.removeArtists();
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
                            List<String> genresList = new ArrayList<>();
                            if (jsonGenres != null) {
                                for (int j = 0; j < jsonGenres.length(); ++j) {
                                    genresList.add(jsonGenres.getString(j));
                                }
                            }
                            String genres = genresList.toString().replaceAll("[\\[\\]]", "");
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
                            dbHelper.addArtist(new Artist(id, name, genres, tracks, albums, link, description, coverBig, coverSmall));
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    adapter.changeCursor(dbHelper.getAllArtistsCursor());
                }
            }.execute();
        }
        return super.onOptionsItemSelected(item);
    }
}
