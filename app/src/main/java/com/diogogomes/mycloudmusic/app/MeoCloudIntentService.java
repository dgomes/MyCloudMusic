package com.diogogomes.mycloudmusic.app;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.diogogomes.meocloud.APIv1;
import com.diogogomes.meocloud.AccountInfo;
import com.diogogomes.meocloud.Media;
import com.diogogomes.meocloud.Metadata;

import org.scribe.model.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MeoCloudIntentService extends IntentService {

    private static final String TAG = MeoCloudIntentService.class.getSimpleName();

    private static final String MEOCLOUDAPIKEY = "2953f601-f1f0-41a6-9b84-eee553a4e9ca";
    private static final String MEOCLOUDAPISECRET = "321041840368789075651210243684691872620";

    public static final String EXTRA_PARAMS = "com.diogogomes.meocloud.extra.PARAMS";
    public static final String EXTRA_RESULT_RECEIVER = "com.diogogomes.meocloud.extra.RESULT_RECEIVER";

    public static final String ACTION_GETACCESSTOKEN = "com.diogogomes.meocloud.action.GETACCESSTOKEN";
    public static final String ACTION_SETACCESSTOKEN = "com.diogogomes.meocloud.action.SETACCESSTOKEN";
    public static final String ACTION_ACCOUNTINFO = "com.diogogomes.meocloud.action.ACCOUNTINFO";
    public static final String ACTION_GETAUTHOTIZATION = "com.diogogomes.meocloud.action.GETAUTHORIZATION";
    public static final String ACTION_SEARCH = "com.diogogomes.meocloud.action.SEARCH";
    public static final String ACTION_MEDIA = "com.diogogomes.meocloud.action.MEDIA";

    public static final String PARAM_VERIFIER = "com.diogogomes.meocloud.extra.VERIFIER";
    public static final String PARAM_ACCESSTOKEN = "com.diogogomes.meocloud.extra.ACCESSTOKEN";
    public static final String PARAM_ACCESSTOKENSECRET = "com.diogogomes.meocloud.extra.ACCESSTOKENSECRET";
    public static final String PARAM_ACCOUNTINFO = "com.diogogomes.meocloud.extra.ACCOUNTINFO";
    public static final String PARAM_AUTHURL = "com.diogogomes.meocloud.extra.AUTHURL";
    public static final String PARAM_METADATA = "com.diogogomes.meocloud.extra.METADATA";
    public static final String PARAM_MEDIA = "com.diogogomes.meocloud.extra.MEDIA";
    public static final String PARAM_PATH = "com.diogogomes.meocloud.extra.PATH";

    public static final int RESULT_OK = 200;
    public static final int RESULT_FAIL = 100;

    private static APIv1 api = new APIv1(MEOCLOUDAPIKEY, MEOCLOUDAPISECRET);

    public MeoCloudIntentService() {
        super("MeoCloudIntentService");

        Log.d(TAG, "MeoCloudIntentService()");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences settings = getApplication().getSharedPreferences(MainActivity.PREFS_NAME, 0);
        String accessToken = settings.getString(getString(R.string.settings_accessToken), null);
        String accessTokenSecret = settings.getString(getString(R.string.settings_accessTokenSecret), null);

        if(accessToken != null && accessTokenSecret != null) {
            api.setAccessToken(accessToken,accessTokenSecret);
        }

        Log.d(TAG, "onCreate()");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        Bundle extras = intent.getExtras();
        Bundle params = extras.getBundle(EXTRA_PARAMS);
        ResultReceiver receiver = extras.getParcelable(EXTRA_RESULT_RECEIVER);

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GETAUTHOTIZATION.equals(action)) {
                Log.d(TAG, "ACTION_GETAUTHOTIZATION");

                try {
                    String url = api.getAuthorizationUrl();

                    Bundle b = new Bundle();
                    b.putSerializable(PARAM_AUTHURL, url);
                    receiver.send(RESULT_OK, b);
                } catch (NullPointerException e) {
                    Log.e(TAG, e.getMessage());
                    receiver.send(RESULT_FAIL, null);
                }
            } else if (ACTION_SEARCH.equals(action)) {
                Log.d(TAG, "ACTION_SEARCH");
                ArrayList<Metadata> musica = api.search(null, "audio/*", 10, false); //TODO this must come as PARAM...

                Bundle b = new Bundle();
                b.putParcelableArrayList(PARAM_METADATA, musica);
                receiver.send(RESULT_OK, b);

            } else if (ACTION_GETACCESSTOKEN.equals(action)) {
                Log.d(TAG, "ACTION_GETACCESSTOKEN");

                String oauth_verifier = params.getString(PARAM_VERIFIER);
                Token t = api.getAccessToken(oauth_verifier);

                Bundle b = new Bundle();
                b.putString(PARAM_ACCESSTOKEN, t.getToken());
                b.putString(PARAM_ACCESSTOKENSECRET, t.getSecret());
                receiver.send(RESULT_OK, b);

            } else if (ACTION_SETACCESSTOKEN.equals(action)) {
                Log.d(TAG, "ACTION_SETACCESSTOKEN");

                Log.d(TAG, params.toString());
                String accessToken = params.getString(PARAM_ACCESSTOKEN);
                String accessTokenSecret = params.getString(PARAM_ACCESSTOKENSECRET);

                Log.d(TAG, accessToken + " - " + accessTokenSecret);

                //save accessToken
                SharedPreferences settings = getApplication().getSharedPreferences(MainActivity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(getString(R.string.settings_accessToken), accessToken);
                editor.putString(getString(R.string.settings_accessTokenSecret), accessTokenSecret);
                editor.commit();

                api.setAccessToken(accessToken, accessTokenSecret);
            } else if (ACTION_ACCOUNTINFO.equals(action)) {
                Log.d(TAG, "ACTION_ACCOUNTINFO");
                try {
                    AccountInfo info = api.getAccountInfo();

                    Bundle b = new Bundle();
                    b.putSerializable(PARAM_ACCOUNTINFO, info);
                    receiver.send(RESULT_OK, b);
                } catch (NullPointerException e) {
                    Log.e(TAG, e.getMessage());
                    receiver.send(RESULT_FAIL, null);
                }
            } else if (ACTION_MEDIA.equals(action)) {
                Log.d(TAG, "ACTION_MEDIA");
                try {
                    String path = params.getString(PARAM_PATH);
                    Media url = api.media(path);
                    Bundle b = new Bundle();
                    b.putParcelable(PARAM_MEDIA, url);
                    receiver.send(RESULT_OK, b);
                } catch (NullPointerException e) {
                    Log.e(TAG, e.getMessage());
                    receiver.send(RESULT_FAIL, null);
                }
            }
        }
    }

}
