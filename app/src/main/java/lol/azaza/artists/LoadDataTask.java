package lol.azaza.artists;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadDataTask extends AsyncTask<Context, Void, Void> {
    App app;
    boolean isSuccess = true;

    public static final String ACTION_DATA_LOADED = "lol.azaza.artists.data_loaded";
    public static final String ACTION_ERROR_OCCURRED = "lol.azaza.artists.error_occurred";

    @Override
    protected Void doInBackground(Context... params) {
        app = (App) params[0];
        app.setTask(this);
        try {
            URL url = new URL("http://download.cdn.yandex.net/mobilization-2016/artists.json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String jsonString = IOUtils.toString(in);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            Artist[] artists = gson.fromJson(jsonString, Artist[].class);
            DBHelper dbHelper = new DBHelper(app);
            dbHelper.replaceArtists(artists);
        } catch (IOException | JsonParseException e) {
            isSuccess = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Intent resultIntent = isSuccess ? new Intent(ACTION_DATA_LOADED) : new Intent(ACTION_ERROR_OCCURRED);
        app.sendBroadcast(resultIntent);
    }
}
