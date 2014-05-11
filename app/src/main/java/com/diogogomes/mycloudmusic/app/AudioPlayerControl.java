package com.diogogomes.mycloudmusic.app;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.MediaController;

import com.diogogomes.meocloud.Media;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dgomes on 09/05/14.
 */
public class AudioPlayerControl implements MediaController.MediaPlayerControl {

    private static final String TAG = AudioPlayerControl.class.getSimpleName();

    private MediaPlayer player = null;
    private MusicArrayAdapter tracks;
    private MusicFragment listenerActivity;

    public AudioPlayerControl(MusicArrayAdapter tracks, MusicFragment listenerActivity)
            throws java.io.IOException
    {
        this.tracks = tracks;

        this.listenerActivity = listenerActivity;
    }

    public void loadTrack(int position) {
        player = new MediaPlayer();
        try {
            player.setDataSource(this.tracks.getItem(position).getUrl());
            Log.d(TAG, "setTrack " + this.tracks.getItem(position).getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setOnPreparedListener(listenerActivity);
        player.setOnInfoListener(listenerActivity);
        player.setOnErrorListener(listenerActivity);
        player.setOnCompletionListener(listenerActivity);
        player.prepareAsync();
    }
    public void loadNextTrack(int position) {
        loadTrack(position);
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
        if(player == null) return -1;
        int duration = player.getDuration();
//        Log.d(TAG, "AudioPlayerControl::getDuration returning " + duration);
        return duration;
    }

    public boolean isPlaying() {
        if(player == null) return false;

        boolean isp = player.isPlaying();
//        Log.d(TAG, "AudioPlayerControl::isPlaying returning " + isp);
        return isp;
    }

    public void pause() {
        if(player == null) return;

        Log.d(TAG, "pause()");
        player.pause();
    }

    public void seekTo(int pos) {
        if(player == null) return;

        //Log.d(TAG, "seekTo()" + pos);
        player.seekTo(pos);
    }

    public void start() {
        if(player == null) return ;

        Log.d(TAG, "start()");
        player.start();
    }

    public void destroy() {
        Log.i(TAG, "destroy() shutting down player");
        if (player != null) {
            player.reset();
            player.release();
            player = null;
        }
    }
}
