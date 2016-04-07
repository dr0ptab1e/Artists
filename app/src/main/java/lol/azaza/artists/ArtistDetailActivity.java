package lol.azaza.artists;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        Glide.with(this).load(artist.getCoverBig()).into(cover);
        TextView genres = (TextView) findViewById(R.id.artist_genres);
        genres.setText(artist.getGenres());
        TextView info = (TextView) findViewById(R.id.artist_info);
        info.setText(artist.getInfo());
        TextView description = (TextView) findViewById(R.id.artist_description);
        description.setText(artist.getDescription());
    }

}
