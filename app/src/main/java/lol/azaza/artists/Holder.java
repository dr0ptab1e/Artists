package lol.azaza.artists;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by azaza on 04.04.2016.
 */
public class Holder extends RecyclerView.ViewHolder {

    public View root;
    public TextView name;
    public TextView genres;
    public TextView info;
    public ImageView cover;

    public Holder(View itemView) {
        super(itemView);
        root = itemView;
        name = (TextView) itemView.findViewById(R.id.artist_name);
        genres = (TextView) itemView.findViewById(R.id.artist_genres);
        info = (TextView) itemView.findViewById(R.id.artist_info);
        cover = (ImageView) itemView.findViewById(R.id.cover);
    }
}
