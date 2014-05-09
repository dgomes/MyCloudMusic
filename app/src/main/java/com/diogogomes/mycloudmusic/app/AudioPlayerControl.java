package com.diogogomes.mycloudmusic.app;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.MediaController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dgomes on 09/05/14.
 */
public class AudioPlayerControl implements MediaController.MediaPlayerControl {

    private static final String TAG = AudioPlayerControl.class.getSimpleName();

    private MediaPlayer player = null;
    private String path = null;

    public AudioPlayerControl(String path, MainActivity listenerActivity)
            throws java.io.IOException
    {
        Log.i(TAG, "AudioPlayerControl constructed with path " + path);
        this.path = path;

        player = new MediaPlayer();
        player.setDataSource(path);

        player.setOnPreparedListener(listenerActivity);
        player.setOnInfoListener(listenerActivity);
        player.setOnErrorListener(listenerActivity);
        player.setOnCompletionListener(listenerActivity);

        /*player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    Log.i(TAG, "AudioPlayerControl onCompletion called");
                    player.reset();
                }
            });*/
        player.prepareAsync();

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path, new HashMap<String, String>());
        String albumartist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        Log.i(TAG, "Title = " + albumartist);
    }

    //
    // MediaController.MediaPlayerControl implementation
    //
    public boolean canPause() { return true; }
    public boolean canSeekBackward() { return true; }
    public boolean canSeekForward() { return true; }

    @Override
    public int getAudioSessionId() {
        return player.getAudioSessionId();
    }

    public int getBufferPercentage() {
        return 100;
    }

    public int getCurrentPosition() {
        if(player == null) return -1;
        int pos = player.getCurrentPosition();
//        Log.d(TAG, "AudioPlayerControl::getCurrentPosition returning " + pos);
        return pos;
    }

    public int getDuration() {
        int duration = player.getDuration();
//        Log.d(TAG, "AudioPlayerControl::getDuration returning " + duration);
        return duration;
    }

    public boolean isPlaying() {
        boolean isp = player.isPlaying();
//        Log.d(TAG, "AudioPlayerControl::isPlaying returning " + isp);
        return isp;
    }

    public void pause() {
        Log.d(TAG, "AudioPlayerControl::pause");
        player.pause();
    }

    public void seekTo(int pos) {
        Log.d(TAG, "AudioPlayerControl::seekTo " + pos);
        player.seekTo(pos);
    }

    public void start() {
        Log.d(TAG, "AudioPlayerControl::start");
        player.start();
    }

    public void destroy() {
        Log.i(TAG, "AudioPlayerControll::destroy shutting down player");
        if (player != null) {
            player.reset();
            player.release();
            player = null;
        }
    }
}
