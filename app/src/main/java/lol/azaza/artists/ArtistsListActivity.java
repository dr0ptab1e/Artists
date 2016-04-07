package lol.azaza.artists;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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
    private DBHelper dbHelper;
    private DataLoadedReceiver dataLoadedReceiver;
    public static final String ACTION_DATA_LOADED = "lol.azaza.artists.data_loaded";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);
        setContentView(R.layout.activity_artists_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView artistsList = (RecyclerView) findViewById(R.id.artists_list);
        adapter = new ArtistAdapter(this, dbHelper.getAllArtistsCursor());
        artistsList.setAdapter(adapter);
        artistsList.setLayoutManager(new LinearLayoutManager(this));
        dataLoadedReceiver = new DataLoadedReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(dataLoadedReceiver, new IntentFilter(ACTION_DATA_LOADED));
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

                Context context;

                @Override
                protected Void doInBackground(Void... params) {
                    context = getApplicationContext();
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
                    context.sendBroadcast(new Intent(ACTION_DATA_LOADED));
                }
            }.execute();
        }
        return super.onOptionsItemSelected(item);
    }

    private class DataLoadedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.changeCursor(dbHelper.getAllArtistsCursor());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(dataLoadedReceiver);
    }
}
