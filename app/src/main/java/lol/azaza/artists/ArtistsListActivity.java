package lol.azaza.artists;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArtistsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private CursorRecyclerAdapter<Holder> adapter;
    private DBHelper dbHelper;
    private DataLoadedReceiver dataLoadedReceiver;
    private ErrorOccurredReceiver errorOccurredReceiver;
    public static final String ACTION_DATA_LOADED = "lol.azaza.artists.data_loaded";
    public static final String ACTION_ERROR_OCCURRED = "lol.azaza.artists.error_occurred";
    public static final String FLAG_IS_REFRESHING = "lol.azaza.artists.is_refreshing";
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout errorLayout;
    private LinearLayout hintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);
        setContentView(R.layout.activity_artists_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        errorLayout = (LinearLayout) findViewById(R.id.error_layout);
        Button tryAgainButton = (Button) findViewById(R.id.try_again_button);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArtistsListActivity.this.onRefresh();
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        RecyclerView artistsList = (RecyclerView) findViewById(R.id.artists_list);
        adapter = new ArtistAdapter(this, dbHelper.getAllArtistsCursor());
        artistsList.setAdapter(adapter);
        artistsList.setLayoutManager(new LinearLayoutManager(this));
        dataLoadedReceiver = new DataLoadedReceiver();
        registerReceiver(dataLoadedReceiver, new IntentFilter(ACTION_DATA_LOADED));
        errorOccurredReceiver = new ErrorOccurredReceiver();
        registerReceiver(errorOccurredReceiver, new IntentFilter(ACTION_ERROR_OCCURRED));
        hintLayout = (LinearLayout) findViewById(R.id.hint_layout);
        if (dbHelper.isArtistsTableEmpty()) {
            hintLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        new AsyncTask<Void, Void, Void>() {

            Context context;
            boolean isSuccess = true;

            @Override
            protected Void doInBackground(Void... params) {
                context = getApplicationContext();
                try {
                    URL url = new URL("http://download.cdn.yandex.net/mobilization-2016/artists.json");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String jsonString = IOUtils.toString(in);
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    Artist[] artists = gson.fromJson(jsonString, Artist[].class);
                    dbHelper.replaceArtists(artists);
                } catch (IOException | JsonParseException e) {
                    isSuccess = false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                Intent resultIntent = isSuccess ? new Intent(ACTION_DATA_LOADED) : new Intent(ACTION_ERROR_OCCURRED);
                context.sendBroadcast(resultIntent);
            }
        }.execute();
    }

    private class DataLoadedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!dbHelper.isArtistsTableEmpty()) {
                hintLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
            }
            adapter.changeCursor(dbHelper.getAllArtistsCursor());
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private class ErrorOccurredReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            hintLayout.setVisibility(View.GONE);
            if (dbHelper.isArtistsTableEmpty()) {
                errorLayout.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(ArtistsListActivity.this, getString(R.string.error), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.getCursor().close();
        unregisterReceiver(dataLoadedReceiver);
        unregisterReceiver(errorOccurredReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLAG_IS_REFRESHING, swipeRefreshLayout.isRefreshing());
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(savedInstanceState.getBoolean(FLAG_IS_REFRESHING));
            }
        });
    }

}
