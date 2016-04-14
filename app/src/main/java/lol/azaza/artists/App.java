package lol.azaza.artists;

import android.app.Application;
import android.os.AsyncTask;

public class App extends Application {
    private AsyncTask task;

    public AsyncTask getTask() {
        return task;
    }

    public void setTask(AsyncTask task) {
        this.task = task;
    }
}
