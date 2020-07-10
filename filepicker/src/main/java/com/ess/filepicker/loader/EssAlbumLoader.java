package com.ess.filepicker.loader;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.SparseArray;

import androidx.loader.content.CursorLoader;

import com.ess.filepicker.SelectOptions;
import com.ess.filepicker.model.Album;

/**
 * EssAlbumLoader
 * Created by 李波 on 2018/2/27.
 */

public class EssAlbumLoader extends CursorLoader {

    public static final String COLUMN_COUNT = "count";
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String COLUMN_BUCKET_ID = "bucket_id";
    private static final String COLUMN_BUCKET_NAME = "bucket_display_name";
    private static final String[] COLUMNS = {
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_NAME,
            MediaStore.MediaColumns.DATA,
            COLUMN_COUNT};
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_NAME,
            MediaStore.MediaColumns.DATA,
            "COUNT(*) AS " + COLUMN_COUNT};
    private static final String[] PROJECTION_29 = {
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_NAME,
            MediaStore.MediaColumns.DATA};


    // === params for showSingleMediaType: false ===
    private static final String SELECTION =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + ") GROUP BY (bucket_id";
    private static final String SELECTION_29 =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    private static final String[] SELECTION_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };
    // =============================================

    // === params for showSingleMediaType: true ===
    private static final String SELECTION_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + ") GROUP BY (bucket_id";
    private static final String SELECTION_FOR_SINGLE_MEDIA_TYPE_29 =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";


    private static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }
    // =============================================

    private static final String BUCKET_ORDER_BY = "datetaken DESC";

    public EssAlbumLoader(Context context, String selection, String[] selectionArgs) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, BUCKET_ORDER_BY);
    }
    private EssAlbumLoader(Context context, String[] projection, String selection, String[] selectionArgs) {
        super(context, QUERY_URI, projection, selection, selectionArgs, BUCKET_ORDER_BY);
    }

    public static CursorLoader newInstance(Context context) {
        String selection;
        String[] selectionArgs;
        String[] projection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            projection = PROJECTION_29;
            if (SelectOptions.getInstance().onlyShowImages) {
                selection = SELECTION_FOR_SINGLE_MEDIA_TYPE_29;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            } else if (SelectOptions.getInstance().onlyShowVideos) {
                selection = SELECTION_FOR_SINGLE_MEDIA_TYPE_29;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            } else {
                selection = SELECTION_29;
                selectionArgs = SELECTION_ARGS;
            }
        }else {
            projection = PROJECTION;
            if (SelectOptions.getInstance().onlyShowImages) {
                selection = SELECTION_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            } else if (SelectOptions.getInstance().onlyShowVideos) {
                selection = SELECTION_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            } else {
                selection = SELECTION;
                selectionArgs = SELECTION_ARGS;
            }
        }
        return new EssAlbumLoader(context, projection,selection, selectionArgs);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor albums = super.loadInBackground();
        MatrixCursor allAlbum = new MatrixCursor(COLUMNS);
        int totalCount = 0;
        String allAlbumCoverPath = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (albums == null) {
                return allAlbum;
            }
            SparseArray<Album> albumList = new SparseArray<>();
            while (albums.moveToNext()) {
                int bucketId = albums.getInt(albums.getColumnIndex(COLUMN_BUCKET_ID));
                String bucketName = albums.getString(albums.getColumnIndex(COLUMN_BUCKET_NAME));
                Album album = albumList.get(bucketId);
                if (album == null) {
                    album = new Album(String.valueOf(bucketId), allAlbumCoverPath, bucketName, 0);
                    albumList.append(bucketId, album);
                }
                album.addCaptureCount();
            }
            for (int i = 0; i < albumList.size(); i++) {
                totalCount += albumList.valueAt(i).getCount();
            }
            if (albums.moveToFirst()) {
                allAlbumCoverPath = albums.getString(albums.getColumnIndex(MediaStore.MediaColumns.DATA));
                allAlbum.addRow(new String[]{Album.ALBUM_ID_ALL, Album.ALBUM_ID_ALL, Album.ALBUM_NAME_ALL, allAlbumCoverPath,
                        String.valueOf(totalCount)});
            }
            for (int i = 0; i < albumList.size(); i++) {
                Album album = albumList.valueAt(i);
                totalCount += album.getCount();
                allAlbum.addRow(new String[]{album.getId(), album.getId(), album.getDisplayName(getContext()),
                        album.getCoverPath(), String.valueOf(album.getCount())});
            }
            return allAlbum;
        }else {
            if (albums != null) {
                while (albums.moveToNext()) {
                    totalCount += albums.getInt(albums.getColumnIndex(COLUMN_COUNT));
                }
                if (albums.moveToFirst()) {
                    allAlbumCoverPath = albums.getString(albums.getColumnIndex(MediaStore.MediaColumns.DATA));
                }
            }
            allAlbum.addRow(new String[]{Album.ALBUM_ID_ALL, Album.ALBUM_ID_ALL, Album.ALBUM_NAME_ALL, allAlbumCoverPath,
                    String.valueOf(totalCount)});


            return new MergeCursor(new Cursor[]{allAlbum, albums});
        }
    }


}
