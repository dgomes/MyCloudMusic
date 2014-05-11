package com.diogogomes.mycloudmusic.app;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.diogogomes.meocloud.Media;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dgomes on 02/05/14.
 */
public class MusicArrayAdapter extends ArrayAdapter<MusicArrayAdapter.MusicEntry> {
    private static String TAG = MusicArrayAdapter.class.getSimpleName();

    private static class MusicHolder {
        TextView tvName;
    }

    public MusicArrayAdapter(Context context, ArrayList<MusicEntry> users) {
        super(context, R.layout.music_layout, users);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e(TAG, "getView()");

        // Get the data item for this position
        MusicEntry music = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        MusicHolder musicHolder;
        if (convertView == null) {
            musicHolder = new MusicHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.music_layout, parent, false);
            musicHolder.tvName = (TextView) convertView.findViewById(R.id.label);
            convertView.setTag(musicHolder);
        } else {
            musicHolder = (MusicHolder) convertView.getTag();
        }

        if (music.isInfoLoaded()) {
            // Populate the data into the template view using the data object
            musicHolder.tvName.setText(music.toString());
        } else {
            musicHolder.tvName.setText(R.string.musicEntryLoading);
            music.getInfo(getContext().getApplicationContext(), this);
        }

        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class MusicEntry {
        private static String TAG = MusicEntry.class.getSimpleName();
        public String size;
        public String info;
        private String path;
        private String url;

        private String artist;
        private String title;
        private boolean infoLoaded = false;
        private boolean infoRequested = false;

        public boolean isInfoLoaded() {
            return infoLoaded;
        }
        public String getPath() {
            return this.path;
        }
        public String getUrl() {
            return this.url;
        }

        public MusicEntry(String size, String path) {
            this.size = size;
            this.path = path;
            this.info = null;

            Log.d(TAG, path);
        }

        @Override
        public String toString() {
            return artist + " - " + title;
        }

        public void getInfo(Context activity, final ArrayAdapter<MusicEntry> adapter) {
            if(infoRequested) {
                return;
            }
            infoRequested = true;
            Log.e(TAG, "getInfo()");

            Intent intent = new Intent(activity, MeoCloudIntentService.class);
            intent.setAction(MeoCloudIntentService.ACTION_MEDIA);
            Bundle params = new Bundle();
            params.putString(MeoCloudIntentService.PARAM_PATH, this.path);
            intent.putExtra(MeoCloudIntentService.EXTRA_PARAMS, params);
            intent.putExtra(MeoCloudIntentService.EXTRA_RESULT_RECEIVER, new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    if (resultCode == MeoCloudIntentService.RESULT_OK && resultData.containsKey(MeoCloudIntentService.PARAM_MEDIA)) {
                        Media m = resultData.getParcelable(MeoCloudIntentService.PARAM_MEDIA);
                        url = m.getUrl();

                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        try {
                            mmr.setDataSource(m.getUrl(), new HashMap<String, String>());
                            title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                            artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            Log.i(TAG, artist + " - " + title);
                            infoLoaded = true;
                            adapter.notifyDataSetChanged();
                        } catch (RuntimeException r) {
                            infoLoaded = false;
                        }
                    }
                }
            });
            activity.startService(intent);
        }

    }
}
