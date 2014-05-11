package com.diogogomes.mycloudmusic.app;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.ListFragment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;


import com.diogogomes.meocloud.Media;
import com.diogogomes.meocloud.Metadata;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class MusicFragment extends ListFragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private static final String TAG = MusicFragment.class.getSimpleName();

    private AudioPlayerControl audioPlayerControl = null;
    private MediaController controller = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SectionNumber = "sectionNumber";

    // TODO: Rename and change types of parameters
    private int mSectionNumber;

    private static Music musicList = null;

    private OnFragmentInteractionListener mListener;

    private int currentPosition = -1;

    private Toast downloadToast;

    // TODO: Rename and change types of parameters
    public static MusicFragment newInstance(int sectionNumber) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SectionNumber, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MusicFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(ARG_SectionNumber);
        }

        if(musicList == null) {
            Intent intent = new Intent(getActivity(), MeoCloudIntentService.class);
            intent.setAction(MeoCloudIntentService.ACTION_SEARCH);
            intent.putExtra(MeoCloudIntentService.EXTRA_RESULT_RECEIVER, new ResultReceiver(new Handler()) {
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    if (resultCode == MeoCloudIntentService.RESULT_OK && resultData.containsKey(MeoCloudIntentService.PARAM_METADATA)) {
                        Log.d(TAG, "Update GUI Music");
                        ArrayList<Metadata> music = resultData.getParcelableArrayList(MeoCloudIntentService.PARAM_METADATA);
                        if (music == null) { // Request Again authorization
                            Log.d(TAG, "Open SettingsFragment");
                            Intent intent = new Intent(getActivity(), SettingsFragment.class);
                            startActivity(intent);
                        } else { //TODO should be smarter and implement a cache
                            musicList = new Music(getActivity(), new ArrayList<Music.MusicEntry>());
                            for (Metadata m : music) {
                                Log.d(TAG, m.getPath());
                                musicList.add(new Music.MusicEntry(m.getSize(), m.getPath()));
                            }
                            setListAdapter(musicList);
                        }
                    }
                }
            });
            getActivity().startService(intent);
        } else {
            setListAdapter(musicList);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.fragment_music, container, false);

        //TODO Loading message

        return rootview;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        try {
            if(audioPlayerControl == null || position != currentPosition) {
                if(audioPlayerControl != null) {
                    Log.d(TAG, "Stop previous AudioController");
                    controller.hide();
                    audioPlayerControl.destroy();
                    audioPlayerControl = null;
                    controller.setEnabled(false);
                    controller = null;
                }
                downloadToast = Toast.makeText(getActivity(), "Downloading track", Toast.LENGTH_LONG);
                downloadToast.show();
                audioPlayerControl = new AudioPlayerControl(musicList.getItem(position).getUrl(), this);
            }

            // creating the controller here fails.  Have to do it once our onCreate has finished?
            // do it in the onPrepared listener for the actual MediaPlayer
        } catch (java.io.IOException e) {
            Log.e(TAG, "CallPlayer onCreate failed while creating AudioPlayerControl", e);
            Toast et = Toast.makeText(getActivity(), "CallPlayer received error attempting to create AudioPlayerControl: " + e, Toast.LENGTH_LONG);
            et.show();
            getActivity().finish();
        }



        currentPosition = position;
        Log.d(TAG, "PLAY: " + musicList.getItem(position).info);
        Intent intent = new Intent(getActivity(), MeoCloudIntentService.class);
        intent.setAction(MeoCloudIntentService.ACTION_MEDIA);
        Bundle params = new Bundle();
        params.putString(MeoCloudIntentService.PARAM_PATH, musicList.getItem(position).getPath());
        intent.putExtra(MeoCloudIntentService.EXTRA_PARAMS, params);
        intent.putExtra(MeoCloudIntentService.EXTRA_RESULT_RECEIVER, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if(resultCode == MeoCloudIntentService.RESULT_OK && resultData.containsKey(MeoCloudIntentService.PARAM_MEDIA)) {
                    Media m = (Media) resultData.getParcelable(MeoCloudIntentService.PARAM_MEDIA);
                    Log.d(TAG, "streaming: "+ m.getUrl());
                    if (null != mListener) {
                        mListener.onMusicFragmentInteraction(m.getUrl());
                    }
                }
            }
        });
        getActivity().startService(intent);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        controller.setEnabled(false);
        controller = null;
        audioPlayerControl.destroy();

        audioPlayerControl = null;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        Log.i(TAG, "onError");
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
        Log.i(TAG, "onInfo");
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.i(TAG, "onPrepared about to construct MediaController object");

        if(!audioPlayerControl.isPlaying()) {
            audioPlayerControl.start();
        }

        downloadToast.cancel();

        controller = new MediaController(getActivity(), true); // enable fast forward

        View view = getActivity().findViewById(R.id.drawer_layout);

        controller.setMediaPlayer(audioPlayerControl);
        controller.setAnchorView(view);
        controller.setEnabled(true);
        controller.show(0); //audioPlayerControl.getDuration());

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    if(controller != null)
                        if(!controller.isShowing())
                            controller.show();
                }
                return true;
            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onMusicFragmentInteraction(String url);
    }

}
