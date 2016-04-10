package lol.azaza.artists;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

public class ArtistAdapter extends CursorRecyclerAdapter<Holder> {

    private Context context;
    public static final String ARTIST = "lol.azaza.artists.artist";

    public ArtistAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
    }

    @Override
    public void onBindViewHolderCursor(Holder holder, Cursor cursor) {
        final Artist artist = DBHelper.getArtist(cursor);
        holder.name.setText(artist.getName());
        holder.genres.setText(artist.getGenres());
        holder.info.setText(artist.getInfo());
        Glide.with(context).load(artist.getCoverSmall()).into(holder.cover);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ArtistDetailActivity.class).putExtra(ARTIST, artist);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.artist_list_item, parent, false);
        return new Holder(v);
    }
}