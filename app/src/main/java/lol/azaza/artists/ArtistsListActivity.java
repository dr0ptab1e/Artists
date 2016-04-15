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

public class ArtistsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private CursorRecyclerAdapter<Holder> adapter;
    private DBHelper dbHelper;
    private DataLoadedReceiver dataLoadedReceiver;
    private ErrorOccurredReceiver errorOccurredReceiver;
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
        registerReceiver(dataLoadedReceiver, new IntentFilter(LoadDataTask.ACTION_DATA_LOADED));
        errorOccurredReceiver = new ErrorOccurredReceiver();
        registerReceiver(errorOccurredReceiver, new IntentFilter(LoadDataTask.ACTION_ERROR_OCCURRED));
        hintLayout = (LinearLayout) findViewById(R.id.hint_layout);
        if (dbHelper.isArtistsTableEmpty()) {
            hintLayout.setVisibility(View.VISIBLE);
        }
        final AsyncTask task = ((App) getApplication()).getTask();
        if (task != null) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(task.getStatus().toString().equals("RUNNING"));
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        new LoadDataTask().execute(getApplicationContext());
    }

    private class DataLoadedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            swipeRefreshLayout.setRefreshing(false);
            if (!dbHelper.isArtistsTableEmpty()) {
                hintLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
            }
            adapter.changeCursor(dbHelper.getAllArtistsCursor());
        }
    }

    private class ErrorOccurredReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            swipeRefreshLayout.setRefreshing(false);
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

}
