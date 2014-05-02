package com.diogogomes.mycloudmusic.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.diogogomes.meocloud.AccountInfo;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SettingsFragment extends Fragment {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    private static final int REQUEST_ACCESSTOKEN = 1001;

    private static final String ARG_SectionNumber = "sectionNumber";

    // TODO: Rename and change types of parameters
    private int mSectionNumber;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(int sectionNumber) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SectionNumber, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(ARG_SectionNumber);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootview = inflater.inflate(R.layout.fragment_settings, container, false);

        final Button mbutton = (Button) rootview.findViewById(R.id.meoCloudRequestAuthorization_button);

        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsFragment.this.getActivity(), MeoCloudAuthorization.class);
                startActivityForResult(intent, REQUEST_ACCESSTOKEN);
                Log.d(TAG, "startActivity MeoCloudAuthorization");
            }
        });

        Intent intent = new Intent(getActivity(), MeoCloudIntentService.class);
        intent.setAction(MeoCloudIntentService.ACTION_ACCOUNTINFO);
        intent.putExtra(MeoCloudIntentService.EXTRA_RESULT_RECEIVER, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if(resultCode == MeoCloudIntentService.RESULT_OK && resultData.containsKey(MeoCloudIntentService.PARAM_ACCOUNTINFO)) {
                    Log.d(TAG, "Update GUI");
                    AccountInfo info = (AccountInfo) resultData.getSerializable(MeoCloudIntentService.PARAM_ACCOUNTINFO);
                    if(info == null) { // Request Again authorization
                        Intent intent = new Intent(SettingsFragment.this.getActivity(), MeoCloudAuthorization.class);
                        startActivityForResult(intent, REQUEST_ACCESSTOKEN);

                    } else {
                        Log.d(TAG, info.getDisplay_name());
                        TextView mInfo = (TextView) rootview.findViewById(R.id.meoCloudAccountInfo);
                        mInfo.setText(info.getDisplay_name());
                        mbutton.setEnabled(false);
                    }
                }
            }
        });
        getActivity().startService(intent);


        return rootview;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode = " + requestCode + " - resultCode = " + resultCode);
        if(requestCode == REQUEST_ACCESSTOKEN)
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "User has successfully authorized access to MeoCloud");

                //send to MeoCloudIntentService
                Intent msgIntent = new Intent(getActivity(), MeoCloudIntentService.class);
                msgIntent.setAction(MeoCloudIntentService.ACTION_SETACCESSTOKEN);
                Bundle params = new Bundle();
                params.putString(MeoCloudIntentService.PARAM_ACCESSTOKEN, data.getExtras().getString("accessToken"));
                params.putString(MeoCloudIntentService.PARAM_ACCESSTOKENSECRET, data.getExtras().getString("accessTokenSecret"));
                msgIntent.putExtra(MeoCloudIntentService.EXTRA_PARAMS, params);
                Log.d(TAG, params.toString());
                getActivity().startService(msgIntent);
            }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSettingsFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SectionNumber));

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
        public void onSettingsFragmentInteraction(Uri uri);
    }

}
