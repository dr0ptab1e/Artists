package lol.azaza.artists;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Artists.db";

    public static final String TABLE_NAME = "artists";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_GENRES = "genres";
    public static final String COLUMN_TRACKS = "tracks";
    public static final String COLUMN_ALBUMS = "albums";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_COVER_BIG = "cover_big";
    public static final String COLUMN_COVER_SMALL = "cover_small";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_GENRES + " TEXT," +
                    COLUMN_TRACKS + " INTEGER," +
                    COLUMN_ALBUMS + " INTEGER," +
                    COLUMN_LINK + " TEXT," +
                    COLUMN_DESCRIPTION + " TEXT," +
                    COLUMN_COVER_BIG + " TEXT," +
                    COLUMN_COVER_SMALL + " TEXT" +
                    ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public ArrayList<Artist> getArtists() {
        ArrayList<Artist> artists = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_GENRES, COLUMN_TRACKS, COLUMN_ALBUMS, COLUMN_LINK, COLUMN_DESCRIPTION, COLUMN_COVER_BIG, COLUMN_COVER_SMALL}, null, null, null, null, COLUMN_ID);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    artists.add(new Artist(
                            cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_GENRES)),
                            cursor.getInt(cursor.getColumnIndex(COLUMN_TRACKS)),
                            cursor.getInt(cursor.getColumnIndex(COLUMN_ALBUMS)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_LINK)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_COVER_BIG)),
                            cursor.getString(cursor.getColumnIndex(COLUMN_COVER_SMALL))));
                    cursor.moveToNext();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return artists;
    }

    public void removeArtists() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void addArtist(Artist artist) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, artist.getId());
        values.put(COLUMN_NAME, artist.getName());
        values.put(COLUMN_GENRES, artist.getGenres());
        values.put(COLUMN_TRACKS, artist.getTracks());
        values.put(COLUMN_ALBUMS, artist.getAlbums());
        values.put(COLUMN_LINK, artist.getLink());
        values.put(COLUMN_DESCRIPTION, artist.getDescription());
        values.put(COLUMN_COVER_BIG, artist.getCoverBig());
        values.put(COLUMN_COVER_SMALL, artist.getCoverSmall());
        db.insert(TABLE_NAME, null, values);
    }
}
