package com.diogogomes.mycloudmusic.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.ListFragment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.diogogomes.meocloud.AccountInfo;
import com.diogogomes.meocloud.Metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class MusicFragment extends ListFragment {
    private static final String TAG = MusicFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SectionNumber = "sectionNumber";

    // TODO: Rename and change types of parameters
    private int mSectionNumber;

    private Music myMusic = new Music();

    private OnFragmentInteractionListener mListener;

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
        Intent intent = new Intent(getActivity(), MeoCloudIntentService.class);
        intent.setAction(MeoCloudIntentService.ACTION_SEARCH);
        intent.putExtra(MeoCloudIntentService.EXTRA_RESULT_RECEIVER, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if(resultCode == MeoCloudIntentService.RESULT_OK && resultData.containsKey(MeoCloudIntentService.PARAM_METADATA)) {
                    Log.d(TAG, "Update GUI Music");
                    ArrayList<Metadata> music = resultData.getParcelableArrayList(MeoCloudIntentService.PARAM_METADATA);
                    if(music == null) { // Request Again authorization
                        Log.d(TAG, "Open SettingsFragment");
                        Intent intent = new Intent(getActivity(), SettingsFragment.class);
                        startActivity(intent);
                    } else {
                        for(Metadata m : music) {
                            Log.d(TAG, m.getPath());
                            myMusic.addItem(new Music.MusicEntry(m.getSize(), m.getPath()));
                        }
                        setListAdapter(new ArrayAdapter<Music.MusicEntry>(getActivity(),
                                R.layout.music_layout, R.id.label, myMusic.ITEMS));
                    }
                }
            }
        });
        getActivity().startService(intent);
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

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onMusicFragmentInteraction(Music.ITEMS.get(position).id);
        }
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onMusicFragmentInteraction(String id);
    }

}
