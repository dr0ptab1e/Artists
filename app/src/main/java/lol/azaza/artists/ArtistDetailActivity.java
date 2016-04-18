package lol.azaza.artists;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ArtistDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Artist artist = (Artist) getIntent().getSerializableExtra(ArtistAdapter.ARTIST);
        setTitle(artist.getName());
        ImageView cover = (ImageView) findViewById(R.id.cover);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        cover.setMaxWidth((int) (width * 0.55));
        cover.setMaxHeight((int) (height * 0.62));
        Glide.with(this).load(artist.getCoverBig()).fitCenter().into(cover);
        TextView genres = (TextView) findViewById(R.id.artist_genres);
        genres.setText(artist.getGenres());
        TextView info = (TextView) findViewById(R.id.artist_info);
        info.setText(artist.getInfo());
        TextView description = (TextView) findViewById(R.id.artist_description);
        description.setText(artist.getDescription());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
